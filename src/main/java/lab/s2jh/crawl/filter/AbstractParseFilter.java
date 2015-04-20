package lab.s2jh.crawl.filter;

import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import lab.s2jh.crawl.CrawlLogger;
import lab.s2jh.crawl.service.CrawlService;
import lab.s2jh.crawl.service.HtmlunitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * URL解析过滤抽象基类
 */
public abstract class AbstractParseFilter implements ParseFilter {

    private static final Logger logger = LoggerFactory.getLogger(AbstractParseFilter.class);

    protected Pattern urlMatchPattern;

    protected CrawlService crawlService;

    /**
     * 注入爬虫服务，对于查询或分类页面解析出来一批链接后需要调用爬虫服务 {@link CrawlService#scheduleCrawl(String...)}
     * 安排新的爬虫作业到线程池
     *
     * @param crawlService
     */
    public void setCrawlService(CrawlService crawlService) {
        this.crawlService = crawlService;
    }

    /**
     * 外部XML Bean定义配置设定当前过滤器匹配的URL正则表达式
     *
     * @param urlMatchRegex
     */
    public void setUrlMatchRegex(String urlMatchRegex) {
        HtmlunitService.addUrlRule(urlMatchRegex);
        urlMatchPattern = Pattern.compile(urlMatchRegex);
    }

    @Override
    public boolean isAcceptUrl(String url) {
        //基于UIRL和配置的正则表达式进行比对判断是否执行当前解析逻辑
        if (urlMatchPattern.matcher(url).find()) {
            return true;
        }
        return false;
    }

    @Override
    public void doFilter(String url, ParseFilterChain filterChain) {
        //基于UIRL和配置的正则表达式进行比对判断是否执行当前解析逻辑
        if (isAcceptUrl(url)) {
            logger.debug("Thread: {}, Invoking Filter: {}", Thread.currentThread().getId(), this.getClass());
            try {
                doFilterInternal(url, filterChain);
            } catch (Exception e) {
                CrawlLogger.LOG_DATA_ERROR.error(url);
                logger.error("error.parse.url: " + url, e);
            }
        } else {
            filterChain.doFilter(url, filterChain);
        }
    }

    /**
     * 帮助类方法：基于当前HtmlPage对象和xpath返回对应的文本字符串
     *
     * @param htmlPage
     * @param xpath
     * @return
     */
    protected String getSingleTextByXPath(HtmlPage htmlPage, String xpath) {
        DomNode el = htmlPage.getFirstByXPath(xpath);
        if (el == null) {
            return "";
        }
        String text = el.asText();
        if (text == null) {
            return "";
        }
        return text.trim();
    }

    /**
     * 帮助类方法：基于url调用HtmlUnit接口获取页面HtmlPage内容
     *
     * @param url
     * @return
     */
    protected HtmlPage fetchHtmlPage(String url) {
        return HtmlunitService.fetchHtmlPage(url);
    }

    /**
     * 帮助类方法：基于url,headers调用HtmlUnit接口获取页面HtmlPage内容
     *
     * @param url               网页url
     * @param additionalHeaders request headers
     * @return HtmlPage
     */
    protected HtmlPage fetchHtmlPage(String url, Map<String, String> additionalHeaders) {
        return HtmlunitService.fetchHtmlPage(url, additionalHeaders);
    }

    /**
     * 帮助类方法：基于url,headers调用HtmlUnit接口获取页面HtmlPage内容
     *
     * @param url               网页url
     * @param cookies           Cookies
     * @param additionalHeaders request headers
     * @return HtmlPage
     */
    protected HtmlPage fetchHtmlPage(String url, Set<Cookie> cookies, Map<String, String> additionalHeaders) {
        return HtmlunitService.fetchHtmlPage(url, cookies, additionalHeaders);
    }


    /**
     * 解析过滤器子类实现逻辑抽象定义
     *
     * @param url
     * @param filterChain
     */
    public abstract void doFilterInternal(String url, ParseFilterChain filterChain) throws Exception;
}
