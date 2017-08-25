package bean;

/**
 * Author : zhouyx
 * Date   : 2017/8/25
 * Description :
 */
public class ResultBean<T> {

    private String key;
    private T result;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
