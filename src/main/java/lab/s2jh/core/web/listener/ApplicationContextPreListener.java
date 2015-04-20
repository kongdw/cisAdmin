package lab.s2jh.core.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import lab.s2jh.core.service.GlobalConfigService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spring容器加载“之前”的ServletContextListener
 */
public class ApplicationContextPreListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextPreListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //强制设置优先采用IPV4协议
        System.setProperty("java.net.preferIPv4Stack", "true");
        //设置当前WEB_ROOT根目录到配置属性以便在单纯的Service运行环境取到应用根目录获取WEB-INF下相关资源
        GlobalConfigService.setWebRootRealPath(event.getServletContext().getRealPath("/"));

        logger.debug("Invoke ApplicationContextPreListener contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Invoke ApplicationContextPreListener contextDestroyed");
    }

}
