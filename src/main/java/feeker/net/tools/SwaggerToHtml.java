package feeker.net.tools;

import feeker.net.tools.bean.Definition;
import feeker.net.tools.bean.JieKouBean;
import feeker.net.tools.bean.Node;
import feeker.net.tools.bean.TagWithProtocol;
import feeker.net.tools.constant.Constants;
import feeker.net.tools.db_mysql.OperateDatabase;
import feeker.net.tools.util.ChineseCharToEn;
import feeker.net.tools.util.DateUtil;
import feeker.net.tools.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * 获取swaggerUi，转换为生成文件
 * Created by LGZ on 2016/8/22.
 */
@SuppressWarnings("unchecked")
public class SwaggerToHtml {
    static final String FILE_BASE_PATH = Constants.FILE_BASE_PATH;
    private static final Logger logger = Logger.getLogger(SwaggerToHtml.class);

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
        Map<String, Object> result = SwaggerCommon.getSwaggerUiJson();
        Map<String, List<TagWithProtocol>> tagMap = (Map<String, List<TagWithProtocol>>) result.get("tag");
        Map<String, JieKouBean> jieKouMap = (Map<String, JieKouBean>) result.get("jieKou");
        Map<String, Definition> definitionsMap = (Map<String, Definition>) result.get("definitionsMap");
        generateHtmlFile(tagMap, jieKouMap, definitionsMap);
    }

    /**
     * 生成api的html页面
     *
     * @param tagMap         tag集合
     * @param jieKouMap      接口集合
     * @param definitionsMap 自定义响应类集合
     */
    private static void generateHtmlFile(Map<String, List<TagWithProtocol>> tagMap, Map<String, JieKouBean> jieKouMap, Map<String, Definition> definitionsMap) {
        List<Map<String, String>> pageList = new ArrayList<Map<String, String>>();
        tagMapToNavigation(tagMap, pageList);
        int i = 1;
        for (Map<String, String> page : pageList) {
            String pagination = page.get("pagination");
            String protocolPath = page.get("protocolPath");//接口地址
            logger.debug("*******开始生成****" + "第" + i + "个页面" + "*******");
            String html = jieKouMapToHtml(protocolPath, jieKouMap, definitionsMap);
            StringBuilder content = new StringBuilder();
            content.append("<html>\n");
            content.append(" <head>\n");
            content.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
            content.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../front/css/jspwiki.css\" />\n");
            content.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"../front/css/feekerwiki.css\" />\n");
            content.append(" </head>\n");
            content.append("<body>\n");
            content.append(html);
            content.append("</body>\n");
            content.append("</html>");
            String filePath = FILE_BASE_PATH + "/" + pagination + ".html";
            try {
                FileUtil.writeToFile(filePath, content.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            i++;
        }
    }

    /**
     * 生成导航栏数据
     *
     * @param tagMap   tag
     * @param pageList 要生成的html文件名称&接口名称&接口请求地址
     * @return html body
     */
    private static String tagMapToNavigation(Map<String, List<TagWithProtocol>> tagMap, List<Map<String, String>> pageList) {
        int i = 1;
        for (Map.Entry<String, List<TagWithProtocol>> entry : tagMap.entrySet()) {
            String tag = entry.getKey();

            String time = DateUtil.format(new Date(), DateUtil.FORMAT_STRING);
            Node parentNode = new Node(tag, "", 1, 0, time, 2, 1);
            long parentId = OperateDatabase.addNode(parentNode);
            List<TagWithProtocol> pages = entry.getValue();
            int j = 1;
            for (TagWithProtocol tagWithProtocol : pages) {
                String basePage = ChineseCharToEn.getAllInitialAndINITCAP(tag);
                String pagination = basePage + "_" + i + "." + j;//页码 页面链接
                Node node = new Node(tagWithProtocol.getProtocolName(), "/html/" + pagination + ".html", 2, parentId, time, 2, 1);
                OperateDatabase.addNode(node);
                Map<String, String> page = new HashMap<String, String>();
                page.put("pagination", pagination);
                page.put("pageName", tagWithProtocol.getProtocolName());
                page.put("protocolPath", tagWithProtocol.getProtocolPath());
                pageList.add(page);
                j++;
            }
            i++;
        }
        return null;
    }

    /**
     * 生成api页面文件
     *
     * @param protocolPath   接口请求地址
     * @param jieKouMap      接口
     * @param definitionsMap 自定义响应类
     * @return html body
     */
    private static String jieKouMapToHtml(String protocolPath, Map<String, JieKouBean> jieKouMap, Map<String, Definition> definitionsMap) {
        StringBuilder builder = new StringBuilder();
        JieKouBean jieKouBean = jieKouMap.get(protocolPath);
        String pathUrl = jieKouBean.getPathUrl();
        String protocolName = jieKouBean.getProtocolName();
        String author = jieKouBean.getAuthor();
        String requestMode = jieKouBean.getRequestMode();
        List<JieKouBean.JieKouParameter> parameterList = jieKouBean.getJieKouParameter();
        List<JieKouBean.JieKouResponse> responseList = jieKouBean.getJieKouResponse();
        builder.append("<div class=\"tabs\">");
        builder.append("<h2>");
        builder.append("1.接口说明");
        builder.append("</h2>");
        builder.append("\n");
        builder.append("<p>");
        builder.append(protocolName);
        builder.append("</p>");
        builder.append("\n");
        builder.append("</p>");
        builder.append("作者：");
        builder.append(author);
        builder.append("</p>");
        builder.append("\n");
        builder.append("<p>");
        builder.append("文档最后修改时间：");
        builder.append(DateUtil.format(new Date(), DateUtil.FORMAT_STRING));
        builder.append("</p>");
        builder.append("\n\n");
        builder.append("<h2>");
        builder.append("2.请求地址");
        builder.append("</h2>");
        builder.append("\n");
        builder.append("<p>");
        builder.append(pathUrl);
        builder.append("</p>");
        builder.append("\n\n");
        builder.append("<h2>");
        builder.append("3.请求方式");
        builder.append("</h2>");
        builder.append("\n");
        builder.append("<p>");
        builder.append(requestMode);
        builder.append("</p>");
        builder.append("\n\n");
        builder.append("<h2>");
        builder.append("4.参数列表");
        builder.append("</h2>");
        builder.append("\n");
        builder.append("<table class=\"wikitable\" border=\"1\"  style=\"width:80%;\"");
        builder.append("\n");
        builder.append("<tr>");
        builder.append("\n");
        builder.append("<th class=\"thOne\" >编号</th>");
        builder.append("\n");
        builder.append("<th>参数名称</th>");
        builder.append("\n");
        builder.append("<th>描述</th>");
        builder.append("\n");
        builder.append("<th class=\"thFour\" >是否必传</th>");
        builder.append("\n");
        builder.append("<th class=\"thFour\" >参数类型</th>");
        builder.append("\n");
        builder.append("<th class=\"thFive\">示例</th>");
        builder.append("\n");
        builder.append("</tr>");
        int i = 1;
        String requestDemo = pathUrl;
        if (parameterList.size() > 0)
            requestDemo = requestDemo + "?";
        for (JieKouBean.JieKouParameter parameter : parameterList) {
            String parameterName = parameter.getParameterName();
            String parameterDescription = parameter.getParameterDescription();
            boolean isParameterRequired = parameter.isParameterRequired();
            Object parameterExample = parameter.getParameterExample();
            String parameterType = parameter.getParameterType();
            requestDemo = requestDemo + parameterName + "=" + "xxx&";
            builder.append("\n");
            builder.append("<tr>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(i);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(parameterName);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append("\n");
            builder.append("<span>");
            builder.append(parameterDescription);
            builder.append("</span>");
            builder.append("\n");
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(isParameterRequired);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(parameterType);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            if (null != parameterExample)
                builder.append(parameterExample);
            builder.append("</td>");
            builder.append("\n");
            builder.append("</tr>");
            i++;
        }
        builder.append("</table>");
        builder.append("<br>");
        if (requestDemo.length() > pathUrl.length())
            requestDemo = requestDemo.substring(0, requestDemo.length() - 1);
        builder.append("\n");
        builder.append("请求示例：");
        builder.append("\n");
        builder.append("<p>");
        builder.append(requestDemo);
        builder.append("</p>");
        builder.append("\n\n");
        builder.append("<h2>");
        builder.append("5.响应结果");
        builder.append("</h2>");
        builder.append("\n");
        i = 1;
        builder.append("<table class=\"wikitable\" border=\"1\"  style=\"width:80%;\"");
        builder.append("\n");
        builder.append("<tr>");
        builder.append("<th class=\"thTwo\">http状态码</th>");
        builder.append("\n");
        builder.append("<th class=\"thThree\">描述</th>");
        builder.append("\n");
        builder.append("<th class=\"thSix\">响应模型</th>");
        builder.append("<br>");
        builder.append("</tr>");
        for (JieKouBean.JieKouResponse response : responseList) {
            String responseStatus = response.getStatus();
            String responseDescription = response.getDescription();
            String responseSchema = response.getSchema();
            builder.append("\r\n");

            builder.append("\n");
            builder.append("<tr>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(responseStatus);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append(responseDescription);
            builder.append("</td>");
            builder.append("\n");
            builder.append("<td>");
            builder.append("\n");
            builder.append("<span>");

            String definitions = "#/definitions/";
            if (responseSchema.startsWith(definitions))
                responseSchema = responseSchema.substring(definitions.length());
            builder.append(responseSchema);
            Definition definition = definitionsMap.get(responseSchema);
            if (definition != null) {
                List<Definition.DefinitionProperties> propertiesList = definition.getPropertiesList();
                if (propertiesList.size() > 0)
                    builder.append("\n");
                builder.append("{");
                if (propertiesList.size() > 0) {
                    builder.append("<br>");
                    builder.append("\n");
                }
                for (Definition.DefinitionProperties properties : propertiesList) {
                    String propertiesName = properties.getPropertiesName();
                    String propertiesType = properties.getPropertiesType();
                    String referenceClass = properties.getReferenceClass();
                    String description = properties.getPropertiesDescription();
                    Object propertiesEnum[] = properties.getPropertiesEnum();
                    builder.append("<br>");
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
                    builder.append("<br>");
                    builder.append("\n");
                }
                builder.append("}");
                builder.append("\n\n");
            }
            builder.append("</span>");
            builder.append("\n");
            builder.append("</td>");
            builder.append("\n");
            builder.append("</tr>");

            i++;
        }
        builder.append("\n\n");
        builder.append("</div>");
        return builder.toString();
    }

    /**
     *获取引用类的信息
     * @param referenceClassName 引用类名称
     * @param definitionsMap 自定义响应类
     * @param num 层数，最多三层
     * @return 引用类的详细信息字符串
     */
    private static String appendReferenceClass(String referenceClassName, Map<String, Definition> definitionsMap, int num) {
        StringBuilder builder = new StringBuilder();
        if (null == referenceClassName)
            return builder.toString();
        if (num >= 3)
            return builder.toString();
        num++;
       String space= getSpace(num);
        Definition definition = definitionsMap.get(referenceClassName);
        List<Definition.DefinitionProperties> propertiesList = definition.getPropertiesList();
        if (propertiesList.size() > 0){
            builder.append("\n");
        }
        builder.append("{");
        for (Definition.DefinitionProperties properties : propertiesList) {
            String propertiesName = properties.getPropertiesName();
            String propertiesType = properties.getPropertiesType();
            String referenceClass = properties.getReferenceClass();
            String description = properties.getPropertiesDescription();
            Object propertiesEnum[] = properties.getPropertiesEnum();
            builder.append("<br>");
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
        builder.append("<br>");
        builder.append(space);
        builder.append("}");
        return builder.toString();
    }

    /**
     * 获取间隔字符串
     * @param num 间隔数量
     * @return 间隔字符串
     */
    private static String getSpace(int num) {
        StringBuilder builder=new StringBuilder();
        for(int i=0;i<num;i++){
            builder.append("&nbsp;&nbsp;&nbsp;&nbsp;");
        }
        return builder.toString();
    }


    public static void main(String[] args) {
        Map<String, Object> parameter = new HashMap<String, Object>();
//        parameter.put("SWAGGER_JSON_URL", "https://coding.net/api/api-docs");
//        parameter.put("JIE_KOE_BASE_URL", "https://coding.net");
        parameter.put("SWAGGER_JSON_URL", "http://localhost:82/api/swagger.json");
        parameter.put("JIE_KOE_BASE_URL", "http://192.168.1.152:82");
        startGenerateAPI(parameter);
    }
}
