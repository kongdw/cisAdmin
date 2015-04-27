package lab.s2jh.module.sys.service.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;
import com.nebhale.jsonpath.JsonPath;
import lab.s2jh.core.test.Unicode;
import lab.s2jh.core.util.JsonUtils;
import lab.s2jh.crawl.htmlunit.ExtJavaScriptErrorListener;
import org.apache.commons.codec.net.URLCodec;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TiebaCheck {
    public static void main(String[] args) {
        String url = "http://tieba.baidu.com/f?ie=utf-8&kw=%E7%AC%94%E8%AE%B0%E6%9C%AC";
        String adTitle = "五一狂降！全金属蓝天模具战神Z7升级128G SSD仅7999";
        try {
            WebClient webClient = buildWebClient();
            WebRequest request = buildWebRequest(url);
            request.setHttpMethod(HttpMethod.GET);
            request.setAdditionalHeaders(getAdditionalHeaders(""));
            printAdditionalHeaders(request);
            HtmlPage page = webClient.getPage(request);
            printAdditionalHeaders(request);
            printResponseHeaders(page.getWebResponse());
            printCookies(webClient);
            HtmlPage obj = webClient.getPage("http://tieba.baidu.com/billboard/pushlog/?t=1429963136356&client_type=pc_web&task=tbda&page=frs&fid=52&tid=&uid=&da_task=tbda&da_fid=52&da_tid=&da_uid=&da_page=frs&da_type_id=0002&da_obj_id=13329&da_good_id=22678&da_obj_name=%E7%A5%9E%E8%88%9F-%E6%98%9F%E5%B0%98&da_first_name=%E9%9D%9E%E6%A0%87&da_second_name=%E7%94%B5%E5%95%86&da_cpid=5&da_abtest=&da_price=100&da_verify=9f1afe2c58927e5cf2489e721d9bdb84&da_plan_id=1&da_ext_info=1_0_0_0_5_0_0_0&da_client_type=PC&da_locate=3&da_type=show");
            System.out.println(obj.asText());
            List<String> adList = getAdJsonList(page, adTitle);
            for (String adstr : adList) {
                verifyAd(adstr);
                checkAd(adstr);
                String s = getVerifyUrl(adstr);
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WebClient buildWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.FIREFOX_17);
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setJavaScriptErrorListener(new ExtJavaScriptErrorListener());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setTimeout(10000);
        webClient.getCookieManager().setCookiesEnabled(true);
        webClient.waitForBackgroundJavaScript(60 * 1000);
        return webClient;
    }

    public static WebRequest buildWebRequest(String url) throws MalformedURLException {
        return buildWebRequest(url, null);
    }

    public static WebRequest buildWebRequest(String url, Map<String, String> headers) throws MalformedURLException {
        return buildWebRequest(url, headers, null);
    }

    public static WebRequest buildWebRequest(String url, Map<String, String> headers, List<NameValuePair> parameters) throws MalformedURLException {
        WebRequest webRequest = new WebRequest(new URL(url));
        if (parameters != null && parameters.size() > 0) {
            webRequest.setRequestParameters(parameters);
        }
        if (headers != null && headers.size() > 0) {
            webRequest.setAdditionalHeaders(headers);
        }
        return webRequest;
    }

    public static Map<String, String> getAdditionalHeaders(String refUrl) {
        Map<String, String> additionalHeaders = Maps.newHashMap();
        additionalHeaders.put("Connection", "keep-alive");
        additionalHeaders.put("Host", "tieba.baidu.com");
        additionalHeaders.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        additionalHeaders.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        additionalHeaders.put("Accept-Encoding", "gzip, deflate, sdch");
        additionalHeaders.put("Accept-Language", "zh-CN,zh;q=0.8,en;q=0.6");
        if (refUrl != null && refUrl.length() > 0)
            additionalHeaders.put("Referer", refUrl);
        return additionalHeaders;
    }

    public static void printAdditionalHeaders(WebRequest request) {
        System.out.println("----------------------------------------------------");
        Map<String, String> additionalHeaders = request.getAdditionalHeaders();
        for (Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
            System.out.println(String.format("--------Request Header key: %s value: %s ", entry.getKey(), entry.getValue()));
        }
    }

    public static void printResponseHeaders(WebResponse response) {
        System.out.println("----------------------------------------------------");
        List<NameValuePair> nameValuePairList = response.getResponseHeaders();
        for (NameValuePair n : nameValuePairList) {
            System.out.println(String.format("--------Response Header name: %s value: %s ", n.getName(), n.getValue()));
        }
    }

    public static List<String> getAdJsonList(HtmlPage htmlPage, String adTitle) {

        List<String> adList = new ArrayList<String>();
        String html = htmlPage.asXml();
        Pattern pattern = Pattern.compile("\\{.*adData.*\\}");
        Matcher matcher = pattern.matcher(html);
        while (matcher.find()) {
            String adStr = Unicode.unicodeToString(matcher.group());
            if (adStr.contains(adTitle.trim())) {
                adList.add(matcher.group().replaceAll("(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/", ""));
                System.out.println(String.format("----------广告连接为:%s", matcher.group().replaceAll("(?<!:)\\/\\/.*|\\/\\*(\\s|.)*?\\*\\/", "")));
            }
        }
        System.out.println(String.format("----------解析网页中的广告条数为%d", adList.size()));
        return adList;
    }


    public static void printCookies(WebClient webClient) {
        System.out.println("----------------------------------------------------");
        Set<Cookie> cookies = webClient.getCookieManager().getCookies();
        for (Cookie cookie : cookies) {
            System.out.println(String.format("--------Cookie domain: %s name: %s value:%s ", cookie.getDomain(), cookie.getName(), cookie.getValue()));
        }
    }

    public static boolean verifyAd(String verifyUrl) throws ParseException, IOException {
        NameValuePair parameter = new NameValuePair("t", String.valueOf(new Date().getTime()));
        return false;
    }

    public static boolean checkAd(String adData) throws ParseException {
        return false;
    }

    public static JsonNode parserJson(String adData) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return mapper.readValue(adData, JsonNode.class);
    }

    public static String getVerifyUrl(String adData) throws Exception {
        StringBuilder url = new StringBuilder("http://tieba.baidu.com/billboard/pushlog/?");
        ObjectMapper mapper = JsonUtils.getMapperInstance(true);
        JsonNode jsonNode = mapper.readTree(adData);
        String s = JsonPath.read("$.adData.pb_log.forum_id", jsonNode, String.class);
        String paramJson =
                "t=" + String.valueOf((new Date()).getTime()) +
                "&client_type=pc_web" +
                "&task=" + JsonPath.read("$.adData.pb_log.task", jsonNode, String.class) +
                "&page=frs" +
                "&fid=" + JsonPath.read("$.adData.pb_log.forum_id", jsonNode, Integer.class) +
                "&tid=" +
                "&uid=" +
                "&da_task=" + JsonPath.read("$.adData.pb_log.task", jsonNode, String.class) +
                "&da_fid=" + JsonPath.read("$.adData.pb_log.forum_id", jsonNode, Integer.class) +
                "&da_tid=" +
                "&da_uid=" +
                "&da_page=frs" +
                "&da_type_id=" + JsonPath.read("$.adData.typeid", jsonNode, String.class) +
                "&da_obj_id=" + JsonPath.read("$.adData.id", jsonNode, String.class) +
                "&da_good_id=" + JsonPath.read("$.adData.goods_info[0].id", jsonNode, String.class) +
                "&da_obj_name=" + JsonPath.read("$.adData.name", jsonNode, String.class) +
                "&da_first_name=" + JsonPath.read("$.adData.first_name", jsonNode, String.class) +
                "&da_second_name=" + JsonPath.read("$.adData.second_name", jsonNode, String.class) +
                "&da_cpid=" + JsonPath.read("$.adData.cpid", jsonNode, String.class) +
                "&da_abtest=" + JsonPath.read("$.adData.abtest", jsonNode, String.class) +
                "&da_price=" + JsonPath.read("$.adData.price", jsonNode, Integer.class) +
                "&da_verify=" + JsonPath.read("$.adData.verify", jsonNode, String.class) +
                "&da_plan_id=" + JsonPath.read("$.adData.plan_id", jsonNode, Integer.class) +
                "&da_ext_info=" + JsonPath.read("$.adData.ext_info", jsonNode, String.class) +
                "&da_client_type=" + JsonPath.read("$.adData.client_type", jsonNode, String.class) +
                "&da_locate=" + JsonPath.read("$.adData.pos_name", jsonNode, String.class) +
                "&da_type=show";
        return encodeUrl(url.append(paramJson).toString(),"UTF-8");
    }
    public static String encodeUrl(String url, String encodingCharset) throws UnsupportedEncodingException {
        return new URLCodec().encode(url, encodingCharset).replace("%3A", ":").replace("%2F", "/").replace("%3F", "?").replace("%3D", "=").replace("%26", "&");
    }
}
