package utils;

/**
 * Author : zhouyx
 * Date   : 2017/8/24
 * Description :
 */
public enum Language {

    EN("英文", "en"),
    TW("繁体", "cht"),
    JP("日文", "jp");

    private String name;
    private String language;

    Language(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }
}
