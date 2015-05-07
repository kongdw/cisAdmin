package lab.s2jh.module.sys;

import com.gargoylesoftware.htmlunit.*;
import lab.s2jh.crawl.htmlunit.ExtHtmlunitCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlUnitService {

    private static Logger logger = LoggerFactory.getLogger(HtmlUnitService.class);

    private static final ThreadLocal<WebClient> webClientThreadLocal = new ThreadLocal<WebClient>();

    public static WebClient buildWebClient(boolean isEnableJavaScript, boolean isEnableCache, boolean isEnableCookies, ProxyConfig proxyConfig) {
        WebClient webClient = webClientThreadLocal.get();
        if (webClient == null) {
            synchronized (webClientThreadLocal) {
                logger.debug("Initing web client for thread: {}", Thread.currentThread().getId());
                webClient = new WebClient(BrowserVersion.CHROME);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setAppletEnabled(false);
                webClient.getOptions().setThrowExceptionOnScriptError(false);

                // 启用javascript支持
                if (isEnableJavaScript) {
                    webClient.getOptions().setJavaScriptEnabled(true);
                    // AJAX support
                    webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                    webClient.waitForBackgroundJavaScript(600 * 1000);
                }

                // 设置代理
                if (proxyConfig != null) {
                    webClient.getOptions().setProxyConfig(proxyConfig);
                }
                webClient.getOptions().setRedirectEnabled(true);

                // 启用Cookie管理
                if (isEnableCookies) {
                    webClient.getCookieManager().setCookiesEnabled(true);
                }
                // Use extension version htmlunit cache process
                if (isEnableCache) {
                    webClient.setCache(new ExtHtmlunitCache());
                }
                //设置足够高度以支持一些需要页面内容多需屏幕滚动显示的页面
                webClient.getCurrentWindow().setInnerHeight(60000);
                webClientThreadLocal.set(webClient);
            }
        } else {
            if (proxyConfig != null) {
                ProxyConfig oldProxyConfig = webClient.getOptions().getProxyConfig();
                if (!equalsProxyConfig(oldProxyConfig, proxyConfig)) {
                    synchronized (webClientThreadLocal) {
                        webClient.getOptions().setProxyConfig(proxyConfig);
                        webClient.getCache().clear();
                    }
                }
            }
        }
        return webClient;
    }


    private static boolean equalsProxyConfig(ProxyConfig oldProxyConfig, ProxyConfig currentProxyConfig) {
        return (oldProxyConfig.getProxyHost().equals(currentProxyConfig.getProxyHost()))
                && (oldProxyConfig.getProxyPort() == currentProxyConfig.getProxyPort())
                && (oldProxyConfig.isSocksProxy() == currentProxyConfig.isSocksProxy());
    }

}
