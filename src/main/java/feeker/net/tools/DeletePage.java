package feeker.net.tools;

import java.util.Map;

/**
 * swagger to jsp wiki tool
 * delete page
 * Created by LGZ on 2016/8/17.
 */
public class DeletePage {

    /**
     * 删除页面及导航
     *
     * @param paginationS 页码
     */
    public static void deletePageAndNavigation(String ...paginationS) {
        String loginUrl = JspWikiCommon.LOGIN_BASE_URL;
        System.out.println("*******开始登录***********");
        JspWikiCommon.login(loginUrl);
        for(String pagination:paginationS ){
            System.out.println("*******删除页面***********");
            String deletePageUrl = JspWikiCommon.DELETE_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + pagination;
            JspWikiCommon.deletePage(deletePageUrl, null);
            System.out.println("*******删除这个页面的左侧导航***********");

            String leftMenuUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + JspWikiCommon.PAGE_LEFT_MENU;
            System.out.println("*******开始编辑左侧导航***********");
            String leftMenuFooterPage = JspWikiCommon.getEditPage(leftMenuUrl);

            String newNavigationContent = deleteNavigationByOnePage(leftMenuFooterPage, pagination);
            Map<String, String> navigationParameters = JspWikiCommon.parseEditPage(leftMenuFooterPage, false, newNavigationContent);
            System.out.println("*******编辑完成，保存左侧导航***********");
            JspWikiCommon.saveEditPage(leftMenuUrl, navigationParameters);
        }

    }

    private static String deleteNavigationByOnePage(String editPage, String pagination) {
        String originalContent = JspWikiCommon.getPageContent(editPage);

        int index = originalContent.indexOf(pagination);
        int pageEndIndex = 0;
        if (index > 0)
            pageEndIndex = index + pagination.length() + 1;
        String preceding = originalContent.substring(0, pageEndIndex);
        int pageBeginIndex = preceding.lastIndexOf("![");
        if (pageBeginIndex > 0)
            preceding = preceding.substring(0, pageBeginIndex);
        if (preceding.endsWith("\n"))
            preceding = preceding.substring(0, preceding.lastIndexOf("\n"));
        if (preceding.endsWith("\r\n"))
            preceding = preceding.substring(0, preceding.lastIndexOf("\r\n"));

        String rear = originalContent.substring(pageEndIndex);

        return preceding + rear;
    }


    public static void main(String args[]) {
        deletePageAndNavigation("Fymx_3.6");
    }
}
