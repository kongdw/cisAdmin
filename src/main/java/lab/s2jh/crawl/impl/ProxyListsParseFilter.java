package lab.s2jh.crawl.impl;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lab.s2jh.crawl.filter.AbstractParseFilter;
import lab.s2jh.crawl.filter.ParseFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyListsParseFilter extends AbstractParseFilter {

    private final Logger logger = LoggerFactory.getLogger(TMallSingleParseFilter.class);

    @Override
    public void doFilterInternal(String url, ParseFilterChain filterChain) throws Exception {
        HtmlPage htmlPage = fetchHtmlPage(url);
        HtmlElement ipTable = htmlPage.getFirstByXPath("//TABLE[@style='width:700px;']");
        String ips = ipTable.asText().trim();
        Pattern pattern = Pattern.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))\\s+\\d{1,4}\\s+[0-9a-zA-Z]+\\s+[0-9a-zA-Z]+");
        Matcher matcher = pattern.matcher(ips);
        while (matcher.find()){
            logger.debug("Ip Info: {}",matcher.group());
        }
    }
}
