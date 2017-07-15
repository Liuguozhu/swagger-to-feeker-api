package feeker.net.tools.constant;

import feeker.net.tools.util.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量类
 * Created by LGZ on 2016/8/12.
 */
public class Constants {
    public static final String SWAGGER_JSON_URL = Config.getStringProperty("SWAGGER_JSON_URL");
    public static final String JIE_KOE_BASE_URL = Config.getStringProperty("JIE_KOE_BASE_URL");
    @SuppressWarnings("unused")
    public static final String SWAGGER_JSON_ENCODING = Config.getStringProperty("SWAGGER_JSON_ENCODING");
    public static final String BASE_PAGE = Config.getStringProperty("BASE_PAGE");
    public static final String LOGIN_BASE_URL = Config.getStringProperty("LOGIN_BASE_URL");
    public static final String EDIT_PAGE_BASE_URL = Config.getStringProperty("EDIT_PAGE_BASE_URL");
    public static final String GET_PAGE_BASE_URL = Config.getStringProperty("GET_PAGE_BASE_URL");
    public static final String DELETE_PAGE_BASE_URL = Config.getStringProperty("DELETE_PAGE_BASE_URL");
    private static final String NEED_RETAIN_PAGE_str = Config.getStringProperty("NEED_RETAIN_PAGE");
    public static final String LOGIN_USER_NAME = Config.getStringProperty("LOGIN_USER_NAME");
    public static final String LOGIN_PASSWORD = Config.getStringProperty("LOGIN_PASSWORD");
    public static final String FILE_BASE_PATH = Config.getStringProperty("FILE_BASE_PATH");
    public static List<String> needRetainPage = new ArrayList<String>();

    static {
        String[] needRetainPages = NEED_RETAIN_PAGE_str.split("\\|");
        for (String page : needRetainPages) {
            page=page.trim();
            if(!"".equals(page))
            needRetainPage.add(page);
        }
    }
    public static void main(String[] args) {
        System.out.println(SWAGGER_JSON_URL);
        System.out.println(JIE_KOE_BASE_URL);
        System.out.println(SWAGGER_JSON_ENCODING);
        System.out.println(BASE_PAGE);
        System.out.println(LOGIN_BASE_URL);
        System.out.println(EDIT_PAGE_BASE_URL);
        System.out.println(GET_PAGE_BASE_URL);
        System.out.println(DELETE_PAGE_BASE_URL);
        System.out.println(NEED_RETAIN_PAGE_str);
        System.out.println("需要保留的页面" + needRetainPage.size());
        for(String page: needRetainPage){
            System.out.println(page);
        }
    }
}
