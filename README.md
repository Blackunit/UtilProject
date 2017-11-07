这个项目封装了一些常用的工具类，目前测试可用的工具类有以下几个：
### Android命令工具类
**Command.java**
```java
//无需su权限
public String execute(List<String> commands, long limitTime){
....
}
//需要su权限
public void executeBySu(List<String> commands){
...
}
```
### 文件写入工具类
**FileUtil.java**
```java
//直接存入文件中，如果文件存在了，会清空之前写入的内容
public void saveDataToFile(String data) {
...
}
//文件末尾附加的方式写入文件，logTime表示是否需要加入时间戳
public boolean saveAppend(String data, boolean logTime) {
...
}
```
### 日志工具类
**LogUtil.java**
这里参照[郭霖](http://blog.csdn.net/guolin_blog/)的《第一行代码》的实现方式，用于控制日志的输出，用法和Android的Log一样。
**FileLogUtil.java（依赖于FileUtil.java工具类）**
输出日志文件到文件中，用法和LogUtil.java一样
### 网络访问工具类
**SimpleHttp.java**
简单的对HttpURLConnection进行了封装
