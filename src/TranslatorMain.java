import baidu.BaiduBean;
import baidu.TransApi;
import bean.ResultBean;
import com.alibaba.fastjson.JSON;
import handler.AndroidIHandler;
import handler.IHandler;
import handler.IOSHandler;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import swing.UIContainer;
import utils.Language;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Author : zhouyx
 * Date   : 2017/8/24
 * Description :
 */
public class TranslatorMain {

    public static void main(String[] args) {
        UIContainer container = new UIContainer();
        container.setOnButtonClickListener(new UIContainer.OnButtonClickListener() {
            @Override
            public void onStartTranslateClick(UIContainer uiContainer, String filePath, boolean isAndroid, ArrayList<Language> languages,
                                              String appKey, String appSecurity) {
                TransApi.getInstance().setAppInfo(appKey, appSecurity);
                uiContainer.setStartTranslateButtonEnable(false);
                if (filePath == null || "".equals(filePath)) {
                    uiContainer.setMessage("没有选择文件");
                    uiContainer.setStartTranslateButtonEnable(true);
                    return;
                }
                if (languages.size() == 0) {
                    uiContainer.setMessage("没有选择翻译语言");
                    uiContainer.setStartTranslateButtonEnable(true);
                    return;
                }
                uiContainer.resetMessage();
                StringBuilder taskLanguage = new StringBuilder();
                for (Language language : languages) {
                    taskLanguage.append(language.getName()).append(" ");
                }
                uiContainer.appendMessage("开始翻译任务 ： " + taskLanguage.toString() + "\n" + "正在读取待翻译文件到内存中...");

                IHandler handler;
                if (isAndroid) {
                    handler = new AndroidIHandler();
                } else {
                    handler = new IOSHandler();
                }
                boolean success = handler.parse(new File(filePath));
                if (!success) {
                    uiContainer.appendMessage("读取文件失败，请检查是否正确的文件");
                    uiContainer.setStartTranslateButtonEnable(true);
                    return;
                }
                uiContainer.appendMessage("本次任务需翻译" + (handler.getElementCount() * languages.size()) + "条文本");

                ArrayList<File> translateFiles = new ArrayList<>();
                translate(handler, uiContainer, languages, translateFiles);
            }
        });
    }

    /**
     * 翻译
     */
    private static void translate(IHandler handler, UIContainer uiContainer, ArrayList<Language> languages, ArrayList<File> translateFiles) {
        LinkedHashMap<String, String> elementMaps = handler.getElementMaps();
        LinkedHashMap<String, String> translatedMap = new LinkedHashMap<>(elementMaps);
        Observable
                .create(new ObservableOnSubscribe<ResultBean<BaiduBean.TransResultBean>>() {
                    @Override
                    public void subscribe(ObservableEmitter<ResultBean<BaiduBean.TransResultBean>> observableEmitter) throws Exception {
                        for (String key : elementMaps.keySet()) {
                            String json = TransApi.getInstance().getTransResult(elementMaps.get(key),
                                    "auto", languages.get(0).getLanguage());
                            BaiduBean bean = JSON.parseObject(json, BaiduBean.class);
                            if (bean.getError_msg() != null) {
                                observableEmitter.onError(new IllegalAccessException(bean.getError_msg()));
                                return;
                            }
                            if (bean.getTrans_result().size() != 0) {
                                BaiduBean.TransResultBean transResultBean = bean.getTrans_result().get(0);
                                ResultBean<BaiduBean.TransResultBean> resultBean = new ResultBean<>();
                                resultBean.setKey(key);
                                resultBean.setResult(transResultBean);
                                observableEmitter.onNext(resultBean);
                            }
                        }
                        observableEmitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.trampoline())
                .subscribe(new Observer<ResultBean<BaiduBean.TransResultBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        uiContainer.appendMessage("\n开始翻译 : " + languages.get(0).getName() + " "
                                + languages.get(0).getLanguage() + "\n");
                    }

                    @Override
                    public void onNext(ResultBean<BaiduBean.TransResultBean> transResultBeanResultBean) {
                        uiContainer.appendMessage(transResultBeanResultBean.getResult().getSrc()
                                + " ---> " + transResultBeanResultBean.getResult().getDst());
                        translatedMap.put(transResultBeanResultBean.getKey(), transResultBeanResultBean.getResult().getDst());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        uiContainer.appendMessage(throwable.getMessage());
                        uiContainer.setStartTranslateButtonEnable(true);
                    }

                    @Override
                    public void onComplete() {
                        File translateDir = new File(handler.getFile().getParentFile().getAbsolutePath()
                                + "/" + languages.get(0).getLanguage());
                        if (!translateDir.exists()) {
                            boolean success = translateDir.mkdirs();
                            if (!success) {
                                uiContainer.appendMessage("\n\n生成翻译文件夹失败\n");
                                return;
                            }
                        }
                        File translateFile = new File(translateDir.getAbsolutePath() + "/" + handler.getFile().getName());
                        boolean success = handler.build(translatedMap, translateFile);
                        if (!success) {
                            uiContainer.appendMessage("\n\n生成翻译文件失败\n");
                            return;
                        }
                        translateFiles.add(translateFile);
                        if (languages.size() == 1) {
                            uiContainer.appendMessage("\n\n任务完成\n");
                            uiContainer.appendMessage("生成翻译文件 : ");
                            for (File file : translateFiles) {
                                uiContainer.appendMessage(file.getAbsolutePath());
                            }
                            uiContainer.setStartTranslateButtonEnable(true);
                        } else {
                            languages.remove(0);
                            translate(handler, uiContainer, languages, translateFiles);
                        }
                    }
                });
    }

}
