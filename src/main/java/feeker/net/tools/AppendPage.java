package feeker.net.tools;

import feeker.net.tools.bean.Definition;
import feeker.net.tools.bean.JieKouBean;
import feeker.net.tools.constant.Constants;
import feeker.net.tools.util.ChineseCharToEn;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * swagger to jsp wiki tool
 * append one page
 * Created by LGZ on 2016/8/17.
 */
@SuppressWarnings("unchecked")
public class AppendPage {
    /**
     * 追加指定接口文档
     *
     * @param protocolPaths 接口名称
     */
    public static void appendPageAndNavigation(String ... protocolPaths) {
        for(String protocolPath:protocolPaths){
            if (!protocolPath.startsWith(SwaggerCommon.JIE_KOE_BASE_URL))
                protocolPath = SwaggerCommon.JIE_KOE_BASE_URL + protocolPath;

            Map<String, Object> result = SwaggerCommon.getSwaggerUiJson();
            Map<String, JieKouBean> jieKouMap = (Map<String, JieKouBean>) result.get("jieKou");
            Map<String, Definition> definitionsMap = (Map<String, Definition>) result.get("definitionsMap");

            String loginUrl = JspWikiCommon.LOGIN_BASE_URL;
            System.out.println("*******开始登录***********");
            JspWikiCommon.login(loginUrl);

            String leftMenuUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME +
                    JspWikiCommon.PAGE_LEFT_MENU;
            System.out.println("*******开始编辑左侧导航***********");
            String leftMenuFooterPage = JspWikiCommon.getEditPage(leftMenuUrl);
            JieKouBean jieKouBean = jieKouMap.get(protocolPath);
            if (null == jieKouBean) {
                System.out.println("******警告！未获取到此接口"+protocolPath+"信息，请检查是否已部署到swagger系统***********");
                continue;
            }
            Map<String, String> paginationMap = new HashMap<String, String>();
            String newNavigationContent = appendNavigationToTag(leftMenuFooterPage, jieKouBean, paginationMap);
            Map<String, String> navigationParameters = JspWikiCommon.parseEditPage(leftMenuFooterPage, false,
                    newNavigationContent);
            System.out.println("*******编辑完成，保存左侧导航***********");
            JspWikiCommon.saveEditPage(leftMenuUrl, navigationParameters);
            String pagination = paginationMap.get("pagination");
            String isExistStr = paginationMap.get("isExist");
            boolean isExist = Boolean.parseBoolean(isExistStr);

            if (!isExist)
                appendPage(pagination, protocolPath, jieKouMap, definitionsMap);
            System.out.println("*******获取左侧导航,让新增加的页面状态改变***********");
            leftMenuUrl = JspWikiCommon.GET_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + JspWikiCommon.PAGE_LEFT_MENU;
            JspWikiCommon.getPage(leftMenuUrl);
        }
    }

    /**
     * 追加接口文档
     *
     * @param pagination     页码
     * @param protocolPath   接口请求地址
     * @param jieKouMap      接口map
     * @param definitionsMap 自定义响应类
     */
    private static void appendPage(String pagination, String protocolPath,
                                   Map<String, JieKouBean> jieKouMap, Map<String, Definition> definitionsMap) {
        System.out.println("*******开始追加接口文档***********");
        String editPageUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + pagination;
        String getEditPage = JspWikiCommon.getEditPage(editPageUrl);
        String newContent = SwaggerToJspWiki.jieKouMapToPageContent(protocolPath, jieKouMap, definitionsMap);
        boolean isRetainOldContent = Constants.needRetainPage.contains(pagination);
        Map<String, String> parameters = JspWikiCommon.parseEditPage(getEditPage, isRetainOldContent, newContent);
        JspWikiCommon.saveEditPage(editPageUrl, parameters);
        System.out.println("*******追加新接口文档成功***********");
    }

    /**
     * 追加接口文档链接到导航栏对应的tag下
     *
     * @param leftMenuFooterPage 导航栏页面内容
     * @param jieKouBean         this protocol bean
     * @return 追加后的导航栏页面内容
     */
    private static String appendNavigationToTag(String leftMenuFooterPage, JieKouBean jieKouBean, Map<String, String> paginationMap) {
        String protocolName = jieKouBean.getProtocolName();
        String tag = jieKouBean.getTag();
        String pageContent = JspWikiCommon.getPageContent(leftMenuFooterPage);
        return getNewPageContent(pageContent, tag, protocolName, paginationMap);
    }

    /**
     * @param pageContent  页面原内容
     * @param tag          需要追加接口的的标签
     * @param protocolName 追加的接口名称
     * @return NewPageContent
     */
    private static String getNewPageContent(String pageContent, String tag, String protocolName, Map<String, String> paginationMap) {
        String tagContent, precedingContent, rearContent;
        Map<String, String> otherContent = new HashMap<String, String>();
        tagContent = getTagContent(pageContent, tag, otherContent);
        if (checkProtocolNameIsExist(tagContent, protocolName)) {
            System.out.println("********警告！已经存在该接口文档，请勿重复添加！*********");
            paginationMap.put("isExist", "true");
            return pageContent;
        }
        paginationMap.put("isExist", "false");

        precedingContent = otherContent.get("precedingContent");
        rearContent = otherContent.get("rearContent");
        int beginIndex = tagContent.lastIndexOf(".");
        if (beginIndex < 0)
            beginIndex = 0;
        int endIndex = tagContent.lastIndexOf("]");
        if (endIndex < 0)
            endIndex = 0;
        String maxPaginationStr = "0";
        if (endIndex > beginIndex)
            maxPaginationStr = tagContent.substring(beginIndex + 1, endIndex);
        int maxPagination = Integer.parseInt(maxPaginationStr);
        int titleBeginIndex = tagContent.lastIndexOf("|");
        int titleEndIndex = beginIndex;
        if (titleBeginIndex < 0)
            titleBeginIndex = 0;
        if (titleBeginIndex > 0)
            titleBeginIndex += 1;
        String pagination = tagContent.substring(titleBeginIndex, titleEndIndex);
        if (maxPagination <= 0)
            pagination = ChineseCharToEn.getAllInitialAndINITCAP(tag) + "_" +
                    (Integer.parseInt(
                            precedingContent.substring(precedingContent.lastIndexOf("_") + 1,
                                    precedingContent.lastIndexOf("."))
                    ) + 1);
        pagination = pagination + "." + (maxPagination + 1);
        paginationMap.put("pagination", pagination);

        System.out.println("pagination =" + pagination);
        StringBuilder newContent = new StringBuilder();
        newContent.append(precedingContent);
        if (StringUtils.isBlank(tagContent))
            tagContent = "\n!!!" + tag + "\n";
        newContent.append(tagContent);
//        newContent.append("\n");
        newContent.append("![");
        newContent.append(protocolName);
        newContent.append("|");
        newContent.append(pagination);
        newContent.append("]");
        newContent.append("\n");
        newContent.append(rearContent);
        return newContent.toString();
    }

    /**
     * 获取标签及标签内的页面导航内容
     *
     * @param pageContent 页面内容
     * @param tagName     标签名称
     * @return 标签内容
     */
    private static String getTagContent(String pageContent, String tagName, Map<String, String> otherContent) {
        String tag = "!!!" + tagName + "\r\n";
        int tagBeginIndex = pageContent.length();
        int tagIndex = pageContent.indexOf(tag);
        if (tagIndex >= 0)
            tagBeginIndex = tagIndex;
        String preceding = pageContent.substring(0, tagBeginIndex);
        String tagPage = pageContent.substring(tagBeginIndex);
        int index = pageContent.length();
        if (tagBeginIndex >= 0 && tagBeginIndex < index)
            index = tagBeginIndex + 3;
        String tagPageWithOutExclamation = pageContent.substring(index);
        int endIndex = tagPageWithOutExclamation.indexOf("!!!");
        if (endIndex < 0)
            endIndex = tagPageWithOutExclamation.length();
        tagPageWithOutExclamation = tagPageWithOutExclamation.substring(0, endIndex);

        int tagEndIndex = tagPage.indexOf(tagPageWithOutExclamation) + tagPageWithOutExclamation.length();
        String rear = tagPage.substring(tagEndIndex);
        tagPage = tagPage.substring(0, tagEndIndex);
        otherContent.put("precedingContent", preceding);
        otherContent.put("rearContent", rear);
        return tagPage;
    }

//    private static boolean checkTagIsExist(String pageContent, String tag) {
//        if (pageContent.indexOf(tag) > 0)
//            return true;
//        return false;
//    }
//

    /**
     * 验证导航栏是否已存在此接口名称
     *
     * @param tagContent   导航页面内容
     * @param protocolName 接口名称
     * @return boolean
     */
    private static boolean checkProtocolNameIsExist(String tagContent, String protocolName) {
        if (tagContent.indexOf(protocolName) > 0)
            return true;
        return false;
    }

    public static void main(String args[]) {
//        String pageContent = "!!!登陆接口2\n" +
//                "![获取测试消息接口|Dljk2_1.1]\n" +
//                "![获取测试消息接口2|Dljk2_1.2]\n" +
//                "![获取测试消息接口3|Dljk2_1.3]\n" +
//                "!!!登陆接口\n" +
//                "![获取测试消息接口|Dljk_1.1]\n" +
//                "![获取测试消息接口2|Dljk_1.2]\n" +
//                "![获取测试消息接口3|Dljk_1.3]\n" +
//                "!!!用户管理\n" +
//                "![获取测试消息接口|Yhgl_2.1]\n" +
//                "!!!用户管理2\n" +
//                "![获取测试消息接口|Yhgl2_2.1]";
//        getTagContent(pageContent, "登陆接口", null);

//        String newContent = getNewPageContent(pageContent, "用户管理2", "追加接口",null);
//        System.out.println(newContent);

        appendPageAndNavigation("/cost/deleteDepartmentCostById","/cost/chargeAgainst");
    }

}
