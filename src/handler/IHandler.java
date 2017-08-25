package handler;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * Author : zhouyx
 * Date   : 2017/8/25
 * Description :
 */
public interface IHandler {

    boolean parse(File file);

    boolean build(LinkedHashMap<String, String> translatedMap, File target);

    int getElementCount();

    LinkedHashMap<String, String> getElementMaps();

    File getFile();

}
