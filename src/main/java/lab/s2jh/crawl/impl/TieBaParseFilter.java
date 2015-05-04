package lab.s2jh.crawl.impl;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lab.s2jh.crawl.filter.AbstractParseFilter;
import lab.s2jh.crawl.filter.ParseFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

/**
 * 百度贴吧爬虫
 */
public class TieBaParseFilter extends AbstractParseFilter {
    private final Logger logger = LoggerFactory.getLogger(TieBaParseFilter.class);

    @Override
    public void doFilterInternal(String url, ParseFilterChain filterChain) throws Exception {
        Map<String,String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Connection","keep-alive");
        additionalHeaders.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Accept-Encoding","gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
        HtmlPage htmlPage = fetchHtmlPage(url,additionalHeaders);
        //String page = htmlPage.asXml();
        //logger.debug("html page:{}",page);
    }


}
