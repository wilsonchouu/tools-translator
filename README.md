# tools_translator
Android、iOS资源文件机翻工具

[项目链接](https://github.com/wilsonchouu/tools_translator)

### 前言
由于APP开发中经常需要进行国际化，在人翻还没有进行完整翻译之前，使用机翻可以大概了解UI变动或者部分简单文本翻译。使用本工具省去了以往复制粘贴的繁琐操作。翻译功能基于百度翻译API实现，UI基于Swing实现。

### 功能
- 支持翻译Android的string.xml文件
- 支持翻译iOS的Localizable.strings文件
- 简单拓展语言种类，修改Language.java即可
- 灵活配置百度翻译API KEY

### 特别注意
!!! 若翻译后文件出现乱码，可以用编辑工具进行转码复原（尽量使用命令脚本执行jar包）

### 如何使用
Windows: 运行runnable目录下的startrun.vbs（注意startrun.vbs与translator.jar需要在同级目录）  
Linux  : //TODO（可以参照startrun.vbs进行命令行执行）  
Mac    : //TODO（可以参照startrun.vbs进行命令行执行）

![image](https://raw.githubusercontent.com/wilsonchouu/tools_translator/master/screenshot/screenshot.png)  

1. 填入百度app key和security
2. 选择待翻译的string.xml或Localizable.strings
3. 选择翻译语言
4. 选择翻译平台
5. 开始翻译