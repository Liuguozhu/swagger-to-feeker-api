package feeker.net.tools;

import feeker.net.tools.bean.Definition;
import feeker.net.tools.bean.JieKouBean;
import feeker.net.tools.bean.TagWithProtocol;
import feeker.net.tools.constant.Constants;
import feeker.net.tools.util.ChineseCharToEn;
import feeker.net.tools.util.DateUtil;
import org.apache.commons.lang3.StringUtils;


import java.util.*;

/**
 * 获取swaggerUi，转换为jspWiki
 * Created by LGZ on 2016/8/10.
 */
@SuppressWarnings("unchecked")
public class SwaggerToJspWiki {

    private static void editJspWiki(Map<String, List<TagWithProtocol>> tagMap,
                                    Map<String, JieKouBean> jieKouMap,
                                    Map<String, Definition> definitionsMap) {
//        String leftMenuFooterUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME+"LeftMenuFooter";
        String loginUrl = JspWikiCommon.LOGIN_BASE_URL;
        System.out.println("*******开始登录***********");
        JspWikiCommon.login(loginUrl);

        String leftMenuUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + JspWikiCommon.PAGE_LEFT_MENU;
        System.out.println("*******开始编辑左侧导航***********");
        String leftMenuFooterPage = JspWikiCommon.getEditPage(leftMenuUrl);
        List<Map<String, String>> pageList = new ArrayList<Map<String, String>>();
        String newNavigationContent = tagMapToContent(tagMap, pageList);
        boolean isRetainOldContent = Constants.needRetainPage.contains(JspWikiCommon.PAGE_LEFT_MENU);
        Map<String, String> navigationParameters = JspWikiCommon.parseEditPage(leftMenuFooterPage, isRetainOldContent, newNavigationContent);
        System.out.println("*******编辑完成，保存左侧导航***********");
        JspWikiCommon.saveEditPage(leftMenuUrl, navigationParameters);
        int i = 1;
        for (Map<String, String> page : pageList) {
            String pagination = page.get("pagination");
//            String pageName = page.get("pageName");
            String protocolPath = page.get("protocolPath");//接口地址
            String editPageUrl = JspWikiCommon.EDIT_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + pagination;
            System.out.println("*******开始编辑****" + "第" + i + "个页面" + "*******");
            String getEditPage = JspWikiCommon.getEditPage(editPageUrl);
            String newContent = jieKouMapToPageContent(protocolPath, jieKouMap, definitionsMap);
            isRetainOldContent = Constants.needRetainPage.contains(pagination);
            Map<String, String> parameters = JspWikiCommon.parseEditPage(getEditPage, isRetainOldContent, newContent);
            System.out.println("*******编辑完成，保存页面***********");
            JspWikiCommon.saveEditPage(editPageUrl, parameters);
            i++;
        }

        System.out.println("*******获取左侧导航,让新增加的页面状态改变***********");
        leftMenuUrl = JspWikiCommon.GET_PAGE_BASE_URL + JspWikiCommon.PARAMETER_NAME + JspWikiCommon.PAGE_LEFT_MENU;
        JspWikiCommon.getPage(leftMenuUrl);

//        System.out.println("*******删除页面***********");
//        String deletePageUrl = DELETE_PAGE_BASE_URL +PARAMETER_NAME+ "Page2.89";
//        deletePage(deletePageUrl, null);
//        System.out.println("*******获取页面***********");
    }

    /**
     * 将jieKouMap转换为接口页面内容
     *
     * @param protocolPath 接口名称
     * @param jieKouMap    map
     * @return string
     */
    protected static String jieKouMapToPageContent(String protocolPath, Map<String, JieKouBean> jieKouMap,
                                                   Map<String, Definition> definitionsMap) {
        StringBuilder builder = new StringBuilder();
        JieKouBean jieKouBean = jieKouMap.get(protocolPath);
        String pathUrl = jieKouBean.getPathUrl();
        String protocolName = jieKouBean.getProtocolName();
        String author = jieKouBean.getAuthor();
        String requestMode = jieKouBean.getRequestMode();
        List<JieKouBean.JieKouParameter> parameterList = jieKouBean.getJieKouParameter();
        List<JieKouBean.JieKouResponse> responseList = jieKouBean.getJieKouResponse();
        builder.append("!!!1.接口说明");
        builder.append("\n");
        builder.append(protocolName);
        builder.append("\\\\");
        builder.append("作者：");
        builder.append(author);
        builder.append("\\\\");
        builder.append("文档最后修改时间：");
        builder.append(DateUtil.format(new Date(), DateUtil.FORMAT_STRING));
        builder.append("\n\n");
        builder.append("!!!2.请求地址");
        builder.append("\n");
        builder.append(pathUrl);
        builder.append("\n\n");
        builder.append("!!!3.请求方式");
        builder.append("\n");
        builder.append(requestMode);
        builder.append("\n\n");
        builder.append("!!!4.参数列表");
        builder.append("\n");
        builder.append("||编号||参数名称||描述||是否必传||参数类型||示例");
        int i = 1;
        String requestDemo = pathUrl;
        if (parameterList.size() > 0)
            requestDemo = requestDemo + "?";
        for (JieKouBean.JieKouParameter parameter : parameterList) {
            String parameterName = parameter.getParameterName();
            String parameterDescription = parameter.getParameterDescription();
            Object parameterExample = parameter.getParameterExample();
            boolean isParameterRequired = parameter.isParameterRequired();
            String parameterType = parameter.getParameterType();
            requestDemo = requestDemo + parameterName + "=" + "xxx&";
            builder.append("\r\n");
            builder.append("|");
            builder.append(i);
            builder.append("|");
            builder.append(parameterName);
            builder.append("|");
            builder.append("{{{");
            builder.append(parameterDescription);
            builder.append("}}}");
            builder.append("|");
            builder.append(isParameterRequired);
            builder.append("|");
            builder.append(parameterType);
            builder.append("|");
            if (null != parameterExample) {
                builder.append("{{{");
               // if (parameterExample instanceof Integer) {
                    builder.append(parameterExample);
//                } else if (parameterExample instanceof String) {
//                    builder.append(parameterExample);
//                }
                builder.append("}}}");
            }
            i++;
        }
        if (requestDemo.length() > pathUrl.length())
            requestDemo = requestDemo.substring(0, requestDemo.length() - 1);
        builder.append("\n");
        builder.append("请求示例：");
        builder.append("\\\\");
        builder.append(requestDemo);
        builder.append("\n\n");
        builder.append("!!!5.响应结果");
        builder.append("\n");
        i = 1;
        builder.append("||http状态码||描述||响应模型");
        for (JieKouBean.JieKouResponse response : responseList) {
            String responseStatus = response.getStatus();
            String responseDescription = response.getDescription();
            String responseSchema = response.getSchema();
            builder.append("\r\n");
            builder.append("|");
            builder.append(responseStatus);
            builder.append("|");
            builder.append(responseDescription);
            builder.append("|");
            String definitions = "#/definitions/";
            if (responseSchema.startsWith(definitions))
                responseSchema = responseSchema.substring(definitions.length());
            builder.append("{{{");
            builder.append(responseSchema);
            builder.append("}}}");
            builder.append("{{{");
            Definition definition = definitionsMap.get(responseSchema);
            if (definition != null) {
                List<Definition.DefinitionProperties> propertiesList = definition.getPropertiesList();
                if (propertiesList.size() > 0)
                    builder.append("\n\n");
                builder.append("{");
                if (propertiesList.size() > 0)
                    builder.append("\n");
                for (Definition.DefinitionProperties properties : propertiesList) {
                    String propertiesName = properties.getPropertiesName();
                    String propertiesType = properties.getPropertiesType();
                    String referenceClass = properties.getReferenceClass();
                    String description = properties.getPropertiesDescription();
                    Object propertiesEnum[] = properties.getPropertiesEnum();
                    builder.append(propertiesName);
                    builder.append("：");
                    builder.append("\t");
                    builder.append(propertiesType);
                    builder.append("\t");
                    builder.append(description);
                    String referenceClassBuilder = appendReferenceClass(referenceClass, definitionsMap, 0);
                    builder.append("\t");
                    builder.append(referenceClassBuilder);
                    builder.append("\t");
                    String s = properties.propertiesEnumToString();
                    if (propertiesEnum.length > 0) {
                        builder.append("限定值（");
                        builder.append(s);
                        builder.append("）");
                    }
                    builder.append("\n");
                }
                builder.append("}");
                builder.append("\n\n");
            }
            builder.append("}}}");
            i++;
        }
        builder.append("\n\n");
        return builder.toString();
    }


    /**
     * 获取引用类的信息
     *
     * @param referenceClassName 引用类名称
     * @param definitionsMap     自定义响应类
     * @param num                层数，最多三层
     * @return 引用类的详细信息字符串
     */
    private static String appendReferenceClass(String referenceClassName, Map<String, Definition> definitionsMap, int num) {
        StringBuilder builder = new StringBuilder();
        if (null == referenceClassName)
            return builder.toString();
        if (num >= 3)
            return builder.toString();
        num++;
        String space = getSpace(num);
        Definition definition = definitionsMap.get(referenceClassName);
        List<Definition.DefinitionProperties> propertiesList = definition.getPropertiesList();
        if (propertiesList.size() > 0) {
            builder.append("\n");
        }
        builder.append("{");
        for (Definition.DefinitionProperties properties : propertiesList) {
            String propertiesName = properties.getPropertiesName();
            String propertiesType = properties.getPropertiesType();
            String referenceClass = properties.getReferenceClass();
            String description = properties.getPropertiesDescription();
            Object propertiesEnum[] = properties.getPropertiesEnum();
            builder.append("\\\\");
            builder.append("\n");
            builder.append(space);
            builder.append("\t");
            builder.append(propertiesName);
            builder.append("：");
            builder.append("\t");
            builder.append(propertiesType);
            builder.append("\t");
            builder.append(description);
            String referenceClassBuilder = appendReferenceClass(referenceClass, definitionsMap, num);
            builder.append("\t");
            builder.append(referenceClassBuilder);
            builder.append("\t");
            String s = properties.propertiesEnumToString();
            if (propertiesEnum.length > 0) {
                builder.append("限定值（");
                builder.append(s);
                builder.append("）");
            }
            builder.append("\n");
        }
        builder.append("\\\\");
        builder.append(space);
        builder.append("}");
        return builder.toString();
    }

    /**
     * 获取间隔字符串
     *
     * @param num 间隔数量
     * @return 间隔字符串
     */
    private static String getSpace(int num) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append("\t\t");
        }
        return builder.toString();
    }

    /**
     * 将tagMap 转换为导航页面内容
     *
     * @param tagMap map
     * @return string
     */
    private static String tagMapToContent(Map<String, List<TagWithProtocol>> tagMap,
                                          List<Map<String, String>> pageList) {
        StringBuilder builder = new StringBuilder();
        int i = 1;// TagWithProtocol.maxPageTag;
        for (Map.Entry<String, List<TagWithProtocol>> entry : tagMap.entrySet()) {
            String tag = entry.getKey();
            builder.append("!!!");
            builder.append(tag);
            builder.append("\n");
            List<TagWithProtocol> pages = entry.getValue();
            int j = 1;//TagWithProtocol.maxPageProtocol.get(i);
            for (TagWithProtocol tagWithProtocol : pages) {
                builder.append("![");
                builder.append(tagWithProtocol.getProtocolName());
                builder.append("|");
                String basePage = ChineseCharToEn.getAllInitialAndINITCAP(tag);
                String pagination = basePage + "_" + i + "." + j;//页码 页面链接
                builder.append(pagination);
                builder.append("]");
                builder.append("\n");
                Map<String, String> page = new HashMap<String, String>();
                page.put("pagination", pagination);
                page.put("pageName", tagWithProtocol.getProtocolName());
                page.put("protocolPath", tagWithProtocol.getProtocolPath());
                pageList.add(page);
                j++;
            }
            i++;
        }
        return builder.toString();
    }

    public static void main(String[] args) {
//        Map<String, Object> result = getSwaggerUiJson();
//        Map<String, List<TagWithProtocol>> tagMap = (Map<String, List<TagWithProtocol>>) result.get("tag");
//        Map<String, JieKouBean> jieKouMap = (Map<String, JieKouBean>) result.get("jieKou");
//        Map<String, Definition> definitionsMap = (Map<String, Definition>) result.get("definitionsMap");
//
////        System.out.println("tagMap" + tagMap);
////        System.out.println("jieKouMap" + jieKouMap);
//        editJspWiki(tagMap, jieKouMap, definitionsMap);
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("SWAGGER_JSON_URL", "http://localhost:82/api/swagger.json");
        parameter.put("JIE_KOE_BASE_URL", "http://192.168.1.152:82");
        parameter.put("IS_COVER_LEFT_MENU", true);
        startGenerateAPI(parameter);
    }

    /**
     * 开始生成API
     *
     * @param parameter 参数列表
     */
    public static void startGenerateAPI(Map<String, Object> parameter) {
        String swaggerJsonUrl = (String) parameter.get("SWAGGER_JSON_URL");
        if (StringUtils.isNotBlank(swaggerJsonUrl))
            SwaggerCommon.SWAGGER_JSON_URL = swaggerJsonUrl;
        String jieKouBaseUrl = (String) parameter.get("JIE_KOE_BASE_URL");
        if (StringUtils.isNotBlank(jieKouBaseUrl))
            SwaggerCommon.JIE_KOE_BASE_URL = jieKouBaseUrl;
        Object obj = parameter.get("IS_COVER_LEFT_MENU");
        boolean IS_RETAIN_LEFT_MENU = obj == null ? false : (Boolean) obj;
        if (IS_RETAIN_LEFT_MENU)
            Constants.needRetainPage.remove("LeftMenu");
        Map<String, Object> result = SwaggerCommon.getSwaggerUiJson();
        Map<String, List<TagWithProtocol>> tagMap = (Map<String, List<TagWithProtocol>>) result.get("tag");
        Map<String, JieKouBean> jieKouMap = (Map<String, JieKouBean>) result.get("jieKou");
        Map<String, Definition> definitionsMap = (Map<String, Definition>) result.get("definitionsMap");
        editJspWiki(tagMap, jieKouMap, definitionsMap);
    }
}
