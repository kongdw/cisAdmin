package lab.s2jh.core.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by irock on 15-4-21.
 */
public class Unicode {
    public static String unicodeToString(String str) {

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }

    public static void main(String[] args) {
        String s =
                unicodeToString("# \\u6784\\u5efa\\u7248\\u672c,\\u7528\\u4e8e\\u53d1\\u5e03\\u7248\\u672c\\u4fe1\\u606f\\u663e\\u793a\\u548c\\u7f13\\u5b58\\u7248\\u672c\\u6807\\u8bc6\n" +
                        "# \\u5f53\\u524d\\u4e3a\\u624b\\u5de5\\u6307\\u5b9a\\u503c\\uff0c\\u901a\\u8fc7\\u81ea\\u52a8\\u5316\\u6784\\u5efa\\u4f1a\\u81ea\\u52a8\\u66f4\\u65b0\\u6b64\\u503c\n" +
                        "# src\\main\\filter\\build.filter.properties\n" +
                        "# \\u81ea\\u52a8\\u5316\\u6784\\u5efa\\u8f93\\u51fa\\u503c\\u683c\\u5f0f\\uff1aMM-dd_HH:mm:ss\n" +
                        "build_version=1.0.0\n" +
                        "\n" +
                        "# \\u6743\\u9650\\u63a7\\u5236\\u7b49\\u7ea7\\uff0c\\u53ef\\u9009\\u503c\\u8bf4\\u660e\\uff1a\n" +
                        "# high=\\u6240\\u6709\\u672a\\u914d\\u7f6e\\u5bf9\\u5e94\\u6743\\u9650\\u7684URL\\u8bf7\\u6c42\\u4f5c\\u4e3a\\u53d7\\u4fdd\\u62a4\\u8d44\\u6e90\\u4e25\\u683c\\u63a7\\u5236\\uff0c\\u8bbf\\u95ee\\u5219\\u629b\\u51fa\\u8bbf\\u95ee\\u62d2\\u7edd\\u5f02\\u5e38\n" +
                        "# low= \\u6240\\u6709\\u672a\\u914d\\u7f6e\\u5bf9\\u5e94\\u6743\\u9650\\u7684URL\\u8bf7\\u6c42\\u4f5c\\u4e3a\\u975e\\u4fdd\\u62a4\\u8d44\\u6e90\\u5bbd\\u677e\\u63a7\\u5236\\uff0c\\u53ea\\u8981\\u767b\\u5f55\\u7528\\u6237\\u90fd\\u53ef\\u4ee5\\u8bbf\\u95ee\n" +
                        "auth_control_level=high\n" +
                        "\n" +
                        "# Quartz\\u96c6\\u7fa4\\u5b9a\\u65f6\\u4efb\\u52a1\\u542f\\u52a8\\u5ef6\\u65f6\\uff08\\u79d2\\u6570\\uff09\n" +
                        "# \\u5f00\\u53d1\\u914d\\u7f6e\\u8bbe\\u7f6e\\u8f83\\u5927\\u503c\\u4ee5\\u907f\\u514dConsole\\u9891\\u7e41\\u6253\\u5370SQL\\u67e5\\u8be2\\u8bed\\u53e5\\u5e72\\u6270\\u6b63\\u5e38\\u5f00\\u53d1\\u8c03\\u8bd5\n" +
                        "# \\u751f\\u4ea7\\u914d\\u7f6e\\u8bbe\\u7f6e\\u5408\\u7406\\u503c\\uff0c\\u598210\\u79d2\n" +
                        "quartz_cluster_startup_delay=1000000\n" +
                        "\n" +
                        "# \\u6846\\u67b6\\u6269\\u5c55\\u5c5e\\u6027\\u52a0\\u8f7d\\uff1aDynamicConfigService\\u76f8\\u5173\\u63a5\\u53e3\\u83b7\\u53d6\\u53c2\\u6570\\u503c\\uff0c\\u4f18\\u5148\\u4ece\\u6570\\u636e\\u5e93ConfigProperty\\u8868\\u53d6\\u503c\\uff0c\\u672a\\u53d6\\u5230\\u5219\\u53d6\\u5c5e\\u6027\\u914d\\u7f6e\\u6587\\u4ef6\\u4e2d\\u7684\\u503c\n" +
                        "# \\u4e3a\\u4e86\\u907f\\u514d\\u610f\\u5916\\u7684\\u6570\\u636e\\u5e93\\u914d\\u7f6e\\u5bfc\\u81f4\\u7cfb\\u7edf\\u5d29\\u6e83\\uff0c\\u7ea6\\u5b9a\\u4ee5cfg\\u6253\\u5934\\u6807\\u8bc6\\u7684\\u53c2\\u6570\\u8868\\u793a\\u53ef\\u4ee5\\u88ab\\u6570\\u636e\\u5e93\\u53c2\\u6570\\u8986\\u5199\\uff0c\\u5176\\u4f59\\u7684\\u5219\\u4e0d\\u4f1a\\u8986\\u76d6\\u6587\\u4ef6\\u5b9a\\u4e49\\u7684\\u5c5e\\u6027\\u503c\n" +
                        "cfg_system_title=S2JH4Net Prototype\n" +
                        "\n" +
                        "# \\u81ea\\u52a9\\u6ce8\\u518c\\u7ba1\\u7406\\u8d26\\u53f7\\u529f\\u80fd\\u5f00\\u5173\\uff1atrue=\\u5173\\u95ed\\u7ba1\\u7406\\u8d26\\u53f7\\u81ea\\u52a9\\u6ce8\\u518c\\uff0cfalse=\\u5f00\\u653e\\u6ce8\\u518c\n" +
                        "cfg_mgmt_signup_disabled=true\n" +
                        "\n" +
                        "#database settings\n" +
                        "#jdbc_url=jdbc:h2:mem:h2db\n" +
                        "#MySQL\\u8fde\\u63a5\\u914d\\u7f6e\n" +
                        "jdbc_url=jdbc:mysql://127.0.0.1:3306/s2jh?characterEncoding=utf8\n" +
                        "jdbc_username=root\n" +
                        "jdbc_password=\n" +
                        "\n" +
                        "# \\u90ae\\u4ef6\\u76f8\\u5173\\u53c2\\u6570\\uff0c\\u6839\\u636e\\u5b9e\\u9645\\u90ae\\u4ef6\\u7cfb\\u7edf\\u914d\\u7f6e\\u8bbe\\u5b9a\n" +
                        "# cfg.mail.mock.mode\\u53ef\\u7528\\u4e8e\\u5f00\\u53d1\\u8fc7\\u7a0b\\u6a21\\u62df\\u90ae\\u4ef6\\u53d1\\u9001\\u8c03\\u7528\n" +
                        "# true\\uff1a\\u7b80\\u5355\\u5728\\u65e5\\u5fd7\\u8f93\\u51fa\\u6253\\u5370\\u53d1\\u9001\\u90ae\\u4ef6\\u4fe1\\u606f\\uff0cfalse\\uff1a\\u5b9e\\u9645\\u8c03\\u7528Java Mail\\u53d1\\u9001\\u90ae\\u4ef6\n" +
                        "cfg.mail.mock.mode=true\n" +
                        "mail.host=smtp.xxx.com\n" +
                        "mail.username=admin@xxx.com\n" +
                        "mail.password=xxx\n" +
                        "mail.smtp.auth=true\n" +
                        "cfg.mail.from=${mail.username}\n" +
                        "\n" +
                        "# \\u5de5\\u4f5c\\u6d41\\u5904\\u7406\\u7684\\u81ea\\u7531\\u56de\\u9000\\u529f\\u80fd\\u652f\\u6301\\u63a7\\u5236\n" +
                        "# \\u5982\\u679c\\u6d41\\u7a0b\\u6d41\\u8f6c\\u8fc7\\u7a0b\\u5b58\\u5728\\u4e1a\\u52a1\\u6570\\u636e\\u4ea4\\u4e92\\u5904\\u7406\\uff0c\\u81ea\\u7531\\u56de\\u9000\\u529f\\u80fd\\u5f88\\u53ef\\u80fd\\u5bfc\\u81f4\\u6570\\u636e\\u91cd\\u590d\\u5904\\u7406\\u6216\\u4e0d\\u4e00\\u81f4\\u7684\\u60c5\\u51b5\\u53d1\\u751f\n" +
                        "# \\u56e0\\u6b64\\u9664\\u975e\\u6d41\\u7a0b\\u548c\\u4e1a\\u52a1\\u7ed3\\u5408\\u5904\\u7406\\u9664\\u975e\\u7ecf\\u8fc7\\u4ed4\\u7ec6\\u7684\\u8bbe\\u8ba1\\u5b9e\\u73b0\\uff0c\\u5efa\\u8bae\\u5173\\u95ed\\u81ea\\u7531\\u56de\\u9000\\u529f\\u80fd\\u6216\\u6709\\u7ba1\\u7406\\u5458\\u4e34\\u65f6\\u5e72\\u9884\\u63a7\\u5236\n" +
                        "# \\u53ef\\u9009\\u503c\\u8bf4\\u660e\\uff1adisabled=\\u5168\\u5c40\\u5173\\u95ed; enable=\\u5168\\u5c40\\u542f\\u7528; admin=\\u53ea\\u6709ROLE_ADMIN\\u89d2\\u8272\\u7528\\u6237\\u624d\\u6709\\u529f\\u80fd\\u6743\\u9650\n" +
                        "cfg.bpm.process.back.support=admin");
        System.out.printf(s);
    }
}
