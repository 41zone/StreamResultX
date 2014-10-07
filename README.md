# StreamResultX 1.2
## 兼容性

1. 兼容`struts2-sunspoter-stream-1.0.jar`版本
2. JDK最低版本1.6

## 使用建议

在1.2版本中，建议使用`cc.fozone.struts2.StreamResultX`方式，更为简单。

```Xml
<result-types>  
    <result-type name="streamx" class="cc.fozone.struts2.StreamResultX"/>  
</result-types>  
```
 
## 使用场景
Struts2环境下，通过Struts2提供的下载方式进行下载时出现的`java.lang.IllegalStateException`异常

```Java
2011-1-820:34:20 org.apache.catalina.core.StandardWrapperValve invoke  
严重: Servlet.service() for servlet default threw exception  
java.lang.IllegalStateException  
at org.apache.catalina.connector.ResponseFacade.sendError(ResponseFacade.java:407)  
at javax.servlet.http.HttpServletResponseWrapper.sendError(HttpServletResponseWrapper.java:108)  
at com.opensymphony.module.sitemesh.filter.PageResponseWrapper.sendError(PageResponseWrapper.java:176)  
at javax.servlet.http.HttpServletResponseWrapper.sendError(HttpServletResponseWrapper.java:108)  
at org.apache.struts2.dispatcher.Dispatcher.sendError(Dispatcher.java:770)  
at org.apache.struts2.dispatcher.Dispatcher.serviceAction(Dispatcher.java:505)  
at org.apache.struts2.dispatcher.FilterDispatcher.doFilter(FilterDispatcher.java:395)  
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)  
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)  
at com.opensymphony.sitemesh.webapp.SiteMeshFilter.obtainContent(SiteMeshFilter.java:129)  
at com.opensymphony.sitemesh.webapp.SiteMeshFilter.doFilter(SiteMeshFilter.java:77)  
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)  
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)  
at org.apache.struts2.dispatcher.ActionContextCleanUp.doFilter(ActionContextCleanUp.java:102)  
at org.apache.catalina.core.ApplicationFilterChain.internalDoFilter(ApplicationFilterChain.java:235)  
at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)  
at org.apache.catalina.core.StandardWrapperValve.invoke(StandardWrapperValve.java:233)  
at org.apache.catalina.core.StandardContextValve.invoke(StandardContextValve.java:191)  
at org.apache.catalina.core.StandardHostValve.invoke(StandardHostValve.java:127)  
at org.apache.catalina.valves.ErrorReportValve.invoke(ErrorReportValve.java:102)  
at org.apache.catalina.core.StandardEngineValve.invoke(StandardEngineValve.java:109)  
at org.apache.catalina.connector.CoyoteAdapter.service(CoyoteAdapter.java:298)  
at org.apache.coyote.http11.Http11Processor.process(Http11Processor.java:852)  
at org.apache.coyote.http11.Http11Protocol$Http11ConnectionHandler.process(Http11Protocol.java:588)  
at org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:489)  
at java.lang.Thread.run(Thread.java:662)  
```

## 解决方案
1. 将`StreamResultX-1.2.jar`，并复制在/WEB-INF/lib下
2. 在原有的`struts.xml`的基础上进行相应的配置，配置如下例

**配置如下**

```Xml
<package name="default" namespace="/"extends="struts-default">  
<!-- 只需要添加这行配置 -->  
<result-types>  
    <result-type name="streamx" class="cc.fozone.struts2.StreamResultX"/>  
</result-types>  

<action name="download"class="com.DownloadAction">  
    <!-- 将原有的type="stream"类型修改为type="streamx"即可 -->               
    <result name="success" type="streamx">  
        <param name="inputName">download</param>  
        <param name="bufferSize">4096</param>  
        <param name="contentDisposition">filename=""</param>  
        <param name="contentCharSet">UTF-8</param>  
    </result>  
</action>
```  

### 重点概述
1. 在这种方式下，只需添加一个result-type；
2. 将原有的result中type改为“streamx”，其他一律不变；
3. 在这种情况下，点击“取消”的同时也关闭了流，不会再报出该异常。
4. 之后的执行“取消”后的结果如下：（配置了"log4j.properties"才能看到该结果）

`21:23:44,676  WARN StreamResult:45 - StreamResultX Warn : socket write error`

如果出现该警告说明正确执行，该警告说明，Socket非正常中断，但是流确实已经关闭，自此再也不用看到上面出现的讨厌异常结果。

## 1.0/1.1版本 到 1.2版本切换建议配置

将1.0/1.1中的class

```Xml
<result-type name="streamx" class="com.sunspoter.lib.web.struts2.dispatcher.StreamResultX"/>
```

指向修改如下：

```Xml
<result-type name="streamx" class="cc.fozone.struts2.StreamResultX"/>
```