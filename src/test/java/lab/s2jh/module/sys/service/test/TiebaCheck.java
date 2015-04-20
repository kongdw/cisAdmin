package lab.s2jh.module.sys.service.test;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lab.s2jh.crawl.htmlunit.RegexHttpWebConnection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TiebaCheck {
    public static void main(String[] args) throws IOException {
        String url = "http://tieba.baidu.com/link?client_type=pc_web&tbjump=lid%253D2420267854_11374_5_52_%2526url%253D6b85JYM_5PlGb7bqvA_laILgv1zh000JfBpc4HzaFTQSw8FwfxTnoPNsLF9He6-joRFNqxOcd71k-bQ_v0P2EjszXR0gwNvLFaxVVuRQL_AG7qWbT0d7O2rWpnK3wtalMt1LvimntpMTjanJMp6xtmdgHGq1E5kAv8_2nrPYA_tS25zadQnXiky1OxNctGWeUjNvya_I4VGE54TuIkmE27SqvfM27k6yKAOJN4SoYKPHIxVQxShq_q5_bAV8P_jP19DJVA2F7JCuTRCMeI-nQvWq_jdGEPjUpP3hMghyPkCXhmliJ5d4x70U7Rue-ZCq20ng306fdQjyTpGqV3TRPnOOfsDCo-tx1BH1RH2fPjo4T7Uy3nV_uERHMlV-ndxjxc37O-GRooPWst8UMkwHIX0J9IfHADoq4YIaLFltUsrrvPOU7qpuNDUe99F2P_JbwiTqUPhQ--GzF8l3AhjIbT7iU1MNXgjOS6qY1DPwc5UFKhXuMjo1-79uVAWyvZvIUrKdt2fl8QfKNKM-uHe1fLTAa8O7nIcQlKUyf2R7K2zDRIf6K-cv_ioUkbgkOOeExTB-vWtejPgtg0QGPRiNs3fYXu7ynBx_fL3ehE6jG6fBd0O3q2aUEyDMfkeR5dYuKVZ94AebZz8-TCWCx6KVPeyFNxK6mB3PfevxzD7Rsj5-fqc06NfuEBMg9GxtUaz23ZHZNwvRSBSMmLwbx0XWRb-0Yqdb8kT73hbQmZRZNETUbiAbKRmsmr4AgdjPCwKxYgTYm4fPOzod52BYp2sMgQEuNqq7UD99wdNYTephiqtuFYsKvdpJ8NLeyqbcJYWM1pyC8kdIr2iSP-eh5A&ie=utf-8&task=&locate=&page=pt-pc-frsui&type=click&url=http%3A%2F%2Ftieba.baidu.com%2Ff%3Fie%3Dutf-8%26kw%3D%25E7%25AC%2594%25E8%25AE%25B0%25E6%259C%25AC%3Fie%3Dutf-8%26kw%3D%25E7%25AC%2594%25E8%25AE%25B0%25E6%259C%25AC&ie=utf-8&refer=&ie=utf-8&fid=52&ie=utf-8&fname=%E7%AC%94%E8%AE%B0%E6%9C%AC&ie=utf-8&uid=&uname=&is_new_user=&tid=&_t=1428576020&obj_id=11374";
        //String url="http://tieba.baidu.com/f?ie=utf-8&kw=%E7%AC%94%E8%AE%B0%E6%9C%AC";
        WebClient webClient = getWebClient();
        WebRequest request = new WebRequest(new URL(url));
        request.setHttpMethod(HttpMethod.GET);
        HtmlPage page = webClient.getPage(request);
       System.out.println(page.asXml());
        List<NameValuePair> nameValuePairList =  page.getWebResponse().getResponseHeaders();
        for(NameValuePair n:nameValuePairList){
            System.out.println(n.getName() +"="+n.getValue());
        }
        System.out.println();
        System.out.println();
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        for(Cookie cookie:cookies){
            System.out.println(cookie.getDomain() + " " + cookie.getName() + "=" + cookie.getValue());
        }
        //BAIDUID=5CCCF97691C3B3420A8184B03D91BA29:FG=1
        //Set<Cookie> cookies = Sets.newHashSet();

    }

    public static WebClient getWebClient(){
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setCssEnabled(true);
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setTimeout(60000);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.waitForBackgroundJavaScript(600 * 1000);
        return webClient;
    }

    public static Map<String,String> getAdditionalHeaders(){
        Map<String, String> additionalHeaders = Maps.newHashMap();
        //additionalHeaders.put("Connection","keep-alive");
        additionalHeaders.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Accept-Encoding","gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language","zh-CN,zh;q=0.8,en;q=0.6");
    return additionalHeaders;
    }
}
