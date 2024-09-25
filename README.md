# E-Down
## 基本信息
### 什么是E-Down？
E-Down是一款基于命令行，适用于e621、e926及其他相关网站的下载器，可以通过在配置文件中输入的标签等信息下载关联文件（图片和视频）。
### 操作系统
E-Down适用于`Windows`操作系统，但目前仅在`Windows 10`及更高版本的环境测试过，使用其他版本可能会导致一些错误。
### 注意事项
E-Down通过调用e621、e926的API获取相关文件信息，再进行下载。因此，在使用前，请确保您的需求可以通过调用该API实现，且熟悉该API的参数规则。
### API基本信息
|API名称|根URL|方法|
|----|----|----|
|List|`/posts.json`|`GET`|

**相关API文档：** https://e926.net/help/api

具体参数规则请参考官方API文档。
## 使用说明
### 下载
在release中找到对应的压缩文件后进行下载，解压后请确保文件的结构不变，不要将可执行程序单独放在其他文件夹中，或是删除文件夹内的其他文件，否则程序可能无法顺利执行。
### 运行前的配置
**每次运行程序前请务必先进行配置！**

E-Down不支持在程序内进行配置，因此，每次打开程序时，程序会立即开始执行。

配置文件为`/conf/config.properties`，可用文本文档打开，具体参数在配置文件中予以说明，请保证配置文件项的完整性，仅修改”=“右侧的内容。

配置文件修改完毕后，重新打开程序，即可根据配置内容获取相关文件。
### 缺陷和错误
通常情况下，E-Down会在出错时提示错误信息，若非用户自身的配置问题，你可以向我报告这一缺陷。

如果E-Down出现了没有完成预期任务且突然结束了进程的情况，试着用命令提示符（CMD）运行程序，并获取相关错误信息。
