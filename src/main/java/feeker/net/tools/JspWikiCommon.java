package feeker.net.tools;

import feeker.net.tools.constant.Constants;
import feeker.net.tools.util.HttpClientFactory;
import feeker.net.tools.util.HttpClientUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 操作jspWiki
 * Created by LGZ on 2016/8/22.
 */
public class JspWikiCommon {
    public static HttpClient client = HttpClientFactory.getNewHttpClient();
    public static final String LOGIN_BASE_URL = Constants.LOGIN_BASE_URL;
    public static final String EDIT_PAGE_BASE_URL = Constants.EDIT_PAGE_BASE_URL;
    public static final String GET_PAGE_BASE_URL = Constants.GET_PAGE_BASE_URL;
    @SuppressWarnings("unused")
    public static final String DELETE_PAGE_BASE_URL = Constants.DELETE_PAGE_BASE_URL;

    protected static final String PARAMETER_NAME = "?page=";
    /**
     * 左菜单
     */
    protected static final String PAGE_LEFT_MENU = "LeftMenu";
    /**
     * 左菜单页脚
     */
    @SuppressWarnings("unused")
    protected static final String PAGE_LEFT_MENU_FOOTER = "LeftMenuFooter";

    /**
     * 登录
     *
     * @param reqUrl 登陆地址
     * @return string
     */
    public static String login(String reqUrl) {
        if (StringUtils.isBlank(reqUrl))
            reqUrl = LOGIN_BASE_URL;
        return HttpClientUtils.httpPost(client, reqUrl, new NameValuePair[]{
                new NameValuePair("j_username", Constants.LOGIN_USER_NAME),
                new NameValuePair("j_password", Constants.LOGIN_PASSWORD),
                new NameValuePair("redirect", "Main"),
                new NameValuePair("submitlogin", "登录")
        });
    }

    /**
     * 编辑完成，保存页面
     *
     * @param reqUrl     请求地址
     * @param parameters 参数
     * @return string
     */
    public static String saveEditPage(String reqUrl, Map<String, String> parameters) {
        if (StringUtils.isBlank(reqUrl))
            return null;
        NameValuePair[] nameValuePairs = new NameValuePair[parameters.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs[i] = new NameValuePair(entry.getKey(), entry.getValue());
            i++;
        }
        return HttpClientUtils.httpPost(client, reqUrl, nameValuePairs);
    }

    /**
     * 删除某个页面
     *
     * @param reqUrl     请求地址
     * @param parameters 参数
     * @return string
     */
    @SuppressWarnings("unused")
    public static String deletePage(String reqUrl, Map<String, String> parameters) {
        if (null == parameters)
            parameters = new HashMap<String, String>();
        parameters.put("delete-all", "删除整个页面");
        if (StringUtils.isBlank(reqUrl))
            return null;
        NameValuePair[] nameValuePairs = new NameValuePair[parameters.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            nameValuePairs[i] = new NameValuePair(entry.getKey(), entry.getValue());
            i++;
        }
        return HttpClientUtils.httpPost(client, reqUrl, nameValuePairs);
    }

    /**
     * 获取编辑页面
     *
     * @param reqUrl 请求地址
     * @return string
     */
    public static String getEditPage(String reqUrl) {
        if (StringUtils.isBlank(reqUrl))
            return null;
        return HttpClientUtils.httpGet(client, reqUrl);
    }

    /**
     * 获取页面
     *
     * @param reqUrl 请求地址
     * @return string
     */
    public static String getPage(String reqUrl) {
        if (StringUtils.isBlank(reqUrl))
            return null;
        return HttpClientUtils.httpGet(client, reqUrl);
    }

    /**
     * 截取部分页面生成保存页面的参数
     *
     * @param editPage           获取到的页面内容
     * @param isRetainOldContent 是否保留页面原有内容
     * @return map
     */
    protected static Map<String, String> parseEditPage(String editPage, boolean isRetainOldContent, String newContent) {
        Map<String, String> map = new HashMap<String, String>();
        String other = editPage.substring(editPage.indexOf("<input name=\"page\""),
                editPage.indexOf("<input type=\"submit\" name=\"ok\" value=\"保存\"\n" +
                        "    accesskey=\"s\"\n" +
                        "        title=\"保存 [ s ]\" />"));

        String oldContent = "";
        if (isRetainOldContent)
            oldContent = getPageContent(editPage);

        oldContent += newContent;
        parse(other, map);
        map.put("ok", "保存");
        map.put("submit_auth", "");
        map.put("changenote", "");
        map.put("tbFIND", "");
        map.put("tbREPLACE", "");
        map.put("tbGLOBAL", "");
        map.put("_editedtext", oldContent);
        return map;
    }

    /**
     * 获取页面原内容
     *
     * @param page 整个jsp页面
     * @return 页面可编辑内容
     */
    public static String getPageContent(String page) {
        String tag = "<textarea id=\"editorarea\" name=\"_editedtext\"\n" +
                "         class=\"editor\"\n" +
                "          rows=\"20\" cols=\"80\">";
        String content;
        content = page.substring(page.indexOf(tag) + tag.length());
        content = content.substring(0, content.indexOf("</textarea>"));
        return content;
    }

    /**
     * 将截取的页面内容解析为map
     *
     * @param page page
     * @param map  map
     */
    private static void parse(String page, Map<String, String> map) {
        String name = page.substring(page.indexOf("name") + 6, page.indexOf("type") - 2);
        String value = page.substring(page.indexOf("value") + 7, page.indexOf("/>") - 2);
        map.put(name, value);
        String other = page.substring(page.indexOf("/>") + 2);
        if (other.length() > 10)
            parse(other, map);
    }

}
