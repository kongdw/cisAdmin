## 总体说明

基于Spring MVC的异常处理

## Spring MVC的异常处理主要特性

* 使用Spring MVC提供的简单异常处理器SimpleMappingExceptionResolver；基于XML配置映射；
* 实现Spring的异常处理接口HandlerExceptionResolver 自定义自己的异常处理器；
* 使用@ExceptionHandler注解实现异常处理；

### 框架实现

lab.s2jh.core.web.exception.AnnotationHandlerMethodExceptionResolver

其中注入contentNegotiationManager，判断根据不同请求类型构造对应的数据格式响应，如JSON或JSP页面；

根据不同异常类型，做一定的错误消息友好转义处理，区分控制不同异常是否需要进行logger日志记录；

logger记录时把相关请求数据基于MDC方式记录下来，以便问题排查；