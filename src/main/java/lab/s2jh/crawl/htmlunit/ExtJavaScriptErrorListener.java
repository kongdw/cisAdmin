package lab.s2jh.crawl.htmlunit;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by irock on 15-4-23.
 */
public class ExtJavaScriptErrorListener implements JavaScriptErrorListener {

    @Override
    public void scriptException(HtmlPage htmlPage, ScriptException e) {

    }

    @Override
    public void timeoutError(HtmlPage htmlPage, long l, long l1) {

    }

    @Override
    public void malformedScriptURL(HtmlPage htmlPage, String s, MalformedURLException e) {

    }

    @Override
    public void loadScriptError(HtmlPage htmlPage, URL url, Exception e) {

    }
}
