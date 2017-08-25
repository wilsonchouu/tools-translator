package handler;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author : zhouyx
 * Date   : 2017/8/25
 * Description :
 */
public class IOSHandler implements IHandler {

    private File elementFile;
    private int elementCount = 0;
    private LinkedHashMap<String, String> elementMaps = new LinkedHashMap<>();

    /**
     * 读IOS资源文件
     *
     * @param file
     * @return
     */
    @Override
    public boolean parse(File file) {
        try {
            elementFile = file;
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String str;
            Pattern linePattern = Pattern.compile("\".*\" = \".*\";");
            Pattern kvPattern = Pattern.compile("\"(.+?)\"");
            while ((str = bufferedReader.readLine()) != null) {
                if (!linePattern.matcher(str).matches()) {
                    continue;
                }
                Matcher matcher = kvPattern.matcher(str);
                if (!matcher.find()) {
                    continue;
                }
                String key = matcher.group(0).replace("\"", "");
                String value = matcher.group(1).replace("\"", "");
                elementMaps.put(key, value);
                elementCount++;
            }
            bufferedReader.close();
            fileReader.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean build(LinkedHashMap<String, String> translatedMap, File target) {
        try {
            StringBuilder builder = new StringBuilder();
            for (String key : translatedMap.keySet()) {
                builder.append("\"").append(key).append("\"")
                        .append(" = ")
                        .append("\"").append(translatedMap.get(key)).append("\"")
                        .append("\n");
            }
            FileWriter writer = new FileWriter(target);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(builder.toString());
            bw.close();
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
