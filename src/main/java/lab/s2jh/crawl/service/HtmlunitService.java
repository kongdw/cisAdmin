package lab.s2jh.crawl.service;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Sets;
import lab.s2jh.crawl.CrawlLogger;
import lab.s2jh.crawl.htmlunit.ExtHtmlunitCache;
import lab.s2jh.crawl.htmlunit.RegexHttpWebConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * 基于HtmlUnit的数据抓取服务接口
 */
public class HtmlunitService {

    private static final Logger logger = LoggerFactory.getLogger(CrawlService.class);

    private static ThreadLocal<WebClient> threadWebClient = new ThreadLocal<WebClient>();

    //全局抓取URL规则：'+'前缀或无前缀=include url; '-'前缀=exclude url
    private static Set<String> fetchUrlRules = Sets.newHashSet();

    private static int totalFetchedCount = 0;
    private static int totalFetchTimes = 0;

    public void setFetchUrlRules(Set<String> fetchUrlRules) {
        HtmlunitService.fetchUrlRules.addAll(fetchUrlRules);
    }

    public static void addUrlRule(String rule) {
        HtmlunitService.fetchUrlRules.add(rule);
    }

    public static WebClient buildWebClient() {
        WebClient webClient = threadWebClient.get();
        if (webClient == null) {
            synchronized (threadWebClient) {
                logger.info("Initing web client for thread: {}", Thread.currentThread().getId());
                webClient = new WebClient(BrowserVersion.CHROME);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setAppletEnabled(false);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                // AJAX support
                webClient.setAjaxController(new NicelyResynchronizingAjaxController());
                webClient.getOptions().setRedirectEnabled(true);
                webClient.getCookieManager().setCookiesEnabled(true);
                // Use extension version htmlunit cache process
                webClient.setCache(new ExtHtmlunitCache());
                // Enhanced WebConnection based on urlfilter
                webClient.setWebConnection(new RegexHttpWebConnection(webClient, fetchUrlRules));
                webClient.waitForBackgroundJavaScript(600 * 1000);
                //设置足够高度以支持一些需要页面内容多需屏幕滚动显示的页面
                webClient.getCurrentWindow().setInnerHeight(60000);
                threadWebClient.set(webClient);
            }
        }
        return webClient;
    }

    /**
     * 抓取URL指定页面
     * @param url
     * @return
     */
    public static HtmlPage fetchHtmlPage(String url) {
        return fetchHtmlPage(url, null, null);
    }

    /**
     * 基于指定URL及特定请求头信息抓取页面，如设置了reference、host等特定服务器需要的头信息
     * @param url
     * @param additionalHeaders
     * @return
     */
    public static HtmlPage fetchHtmlPage(String url, Map<String, String> additionalHeaders) {
        return fetchHtmlPage(url, null, additionalHeaders);
    }

    public static HtmlPage fetchHtmlPage(String url, Set<Cookie> cookies, Map<String, String> additionalHeaders) {
        return fetchHtmlPage(url,cookies,additionalHeaders,null,true);
    }

    /**
     * 基于指定URL及特定请求头信息抓取页面，如设置了cookie登录信息、reference、host等特定服务器需要的头信息
     * @param url
     * @param additionalHeaders
     * @return
     */
    public static HtmlPage fetchHtmlPage(String url, Set<Cookie> cookies, Map<String, String> additionalHeaders,ProxyConfig proxyConfig,boolean enableJavaScript) {
        try {
            totalFetchedCount++;

            long start = new Date().getTime();
            CrawlLogger.LOG_RUN_INFO.info("Thread: {}, No.: {}, Fetching: {}", Thread.currentThread().getId(),
                    totalFetchedCount, url);
            WebRequest webRequest = new WebRequest(new URL(url));
            webRequest.setHttpMethod(HttpMethod.GET);
            if (additionalHeaders != null) {
                webRequest.setAdditionalHeaders(additionalHeaders);
            }
            WebClient webClient = buildWebClient();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    webClient.getCookieManager().addCookie(cookie);
                }
            }
            if(proxyConfig != null){
                webClient.getOptions().setProxyConfig(proxyConfig);
            }
            webClient.getOptions().setJavaScriptEnabled(enableJavaScript);
            HtmlPage page = webClient.getPage(webRequest);
            long end = new Date().getTime();
            totalFetchTimes += (end - start);

            printLog();
            return page;
        } catch (Exception e) {
            throw new RuntimeException("HtmlUnit.page.error", e);
        }
    }

    /**
     * 打印统计日志信息
     */
    private static void printLog() {
        long dur = totalFetchTimes / 1000;
        CrawlLogger.LOG_RUN_INFO.info("Total fetched pages: {}, use time: {} seconds, avg speed: {} pages/second ",
                totalFetchedCount, dur, Float.valueOf(totalFetchedCount) / dur);
    }
}
