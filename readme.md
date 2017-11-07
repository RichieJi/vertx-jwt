# jwt认证示例

## 配置启动项
在IDEA15+版本中

1、打开Edit Configurations界面

2、修改main-class为```io.vertx.core.Launcher```

3、修改Program arguments为```run com.fr.cert.Server```

经过上面3步后，可以和普通的应用程序一样启动和调试。

## 认证测试

1、访问[http://localhost:8080/login?username=lilei&password=lilei](http://localhost:8080/login?username=lilei&password=lilei)

2、将第一步请求得到的token值记录下来

3、访问[http://localhost:8080/get?token=xxx](http://localhost:8080/get?token=xxx)，其中xxx为第二步记录的值

4、如果传入的是正确的token，可以看到返回的是用户名信息，如果token做了任何修改，可以看到会提示：Invalid token!