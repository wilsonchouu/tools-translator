package handler;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Author : zhouyx
 * Date   : 2017/8/24
 * Description :
 */
public class AndroidIHandler implements IHandler {

    private File elementFile;
    private int elementCount = 0;
    private LinkedHashMap<String, String> elementMaps = new LinkedHashMap<>();

    /**
     * 解析xml
     *
     * @param file xml文件
     */
    @Override
    public boolean parse(File file) {
        try {
            elementFile = file;
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            Element root = document.getRootElement();
            for (Iterator<Element> it = root.elementIterator(); it.hasNext(); ) {
                Element element = it.next();
                elementMaps.put(element.attribute("name").getValue(), element.getStringValue());
                elementCount++;
            }
            return true;

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成xml
     *
     * @param translatedMap 内容Map
     * @return
     */
    @Override
    public boolean build(LinkedHashMap<String, String> translatedMap, File target) {
        try {
            Document document = DocumentHelper.createDocument();
            Element root = document.addElement("resources");
            for (String key : translatedMap.keySet()) {
                root.addElement("string")
                        .addAttribute("name", key)
                        .addText(new String(translatedMap.get(key).getBytes(), "UTF-8"));
            }

            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileOutputStream(target), format);
            writer.write(document);
            writer.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getElementCount() {
        return elementCount;
    }

    @Override
    public LinkedHashMap<String, String> getElementMaps() {
        return elementMaps;
    }

    @Override
    public File getFile() {
        return elementFile;
    }

}
