package feeker.net.tools;

import feeker.net.tools.bean.Definition;
import feeker.net.tools.bean.JieKouBean;
import feeker.net.tools.bean.TagWithProtocol;
import feeker.net.tools.constant.Constants;
import feeker.net.tools.util.HttpClientUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * swagger 获取，解析
 * Created by LGZ on 2016/8/22.
 */
@SuppressWarnings("unchecked")
public class SwaggerCommon {
    public static String SWAGGER_JSON_URL = Constants.SWAGGER_JSON_URL;
    public static String JIE_KOE_BASE_URL = Constants.JIE_KOE_BASE_URL;
    private static final Logger logger = Logger.getLogger(SwaggerCommon.class);

    public static Map<String, Object> getSwaggerUiJson() {
        String url = SWAGGER_JSON_URL;
        String swaggerUi = getSwaggerUi(url);
//        System.out.println(swaggerUi);
        logger.debug("swaggerJson" + swaggerUi);
        JSONObject jsonObject = JSONObject.fromObject(swaggerUi);
        Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);
        JSONObject jieKou = (JSONObject) mapJson.get("paths");
        JSONObject definitions = (JSONObject) mapJson.get("definitions");
        Map<String, Definition> definitionsMap = parseDefinitions(definitions);
        Map<String, Object> result = jsonToMap(jieKou);
        result.put("definitionsMap", definitionsMap);
        return result;
    }

    private static Map<String, Definition> parseDefinitions(JSONObject definitions) {
        Map<String, Definition> parseDefinitionsMap = new HashMap<String, Definition>();
        Map<String, Object> mapJson = JSONObject.fromObject(definitions);

        String definitionStr = "#/definitions/";
        for (Map.Entry<String, Object> entry : mapJson.entrySet()) {
            String definitionName = entry.getKey();
            Object propertiesObj = entry.getValue();
            Map<String, Object> propertiesJson = JSONObject.fromObject(propertiesObj);
            Object propertiesInfo = propertiesJson.get("properties");
            Map<String, Object> propertiesInfoJson = JSONObject.fromObject(propertiesInfo);
            List<Definition.DefinitionProperties> propertiesList = new ArrayList<Definition.DefinitionProperties>();
            for (Map.Entry<String, Object> properties : propertiesInfoJson.entrySet()) {
                String propertiesName = properties.getKey();
                Object propertiesOther = properties.getValue();
                Map<String, Object> o = JSONObject.fromObject(propertiesOther);
                String propertiesType = o.get("type") == null ? "" : (String) o.get("type");
                String propertiesDescription = o.get("description") == null ? "" : (String) o.get("description");
                JSONArray propertiesEnumArray = o.get("enum") == null ? new JSONArray() : (JSONArray) o.get("enum");
                String referenceClass = (String) o.get("$ref");
                if (referenceClass != null)
                    referenceClass = referenceClass.substring(referenceClass.indexOf(definitionStr) + definitionStr.length());

                Object propertiesEnum[] = new Object[propertiesEnumArray.size()];
                propertiesEnumArray.toArray(propertiesEnum);
                propertiesList.add(
                        new Definition.DefinitionProperties(propertiesName, propertiesType,
                                propertiesDescription, propertiesEnum, referenceClass)
                );
            }
            parseDefinitionsMap.put(definitionName, new Definition(definitionName, propertiesList));
        }
        return parseDefinitionsMap;
    }

    /**
     * json to map
     *
     * @param jsonObject jsonObject
     */
    private static Map<String, Object> jsonToMap(JSONObject jsonObject) {
        Map<String, Object> result = new HashMap<String, Object>();
        Map<String, List<TagWithProtocol>> tagMap = new HashMap<String, List<TagWithProtocol>>();
        Map<String, JieKouBean> jieKouMap = new HashMap<String, JieKouBean>();
        result.put("tag", tagMap);
        result.put("jieKou", jieKouMap);
        Map<String, Object> mapJson = JSONObject.fromObject(jsonObject);
        for (Map.Entry<String, Object> entry : mapJson.entrySet()) {
            String pathUrl = entry.getKey();//接口地址
            pathUrl = JIE_KOE_BASE_URL + pathUrl;
            Object value = entry.getValue();
            if (value instanceof net.sf.json.JSONObject) {
                Map<String, Object> mapJson2 = (JSONObject) value;
                for (Map.Entry<String, Object> entry2 : mapJson2.entrySet()) {
                    String requestMode = entry2.getKey();//请求方式
                    Object value2 = entry2.getValue();
                    if (value2 instanceof net.sf.json.JSONObject) {
                        Map<String, Object> mapJson3 = (JSONObject) value2;
                        JSONArray tagArray = (JSONArray) mapJson3.get("tags");
                        Object[] tag = tagArray.toArray();
                        String summary = (String) mapJson3.get("summary");//接口名称
                        String operationId = (String) mapJson3.get("operationId");//接口昵称
                        JSONArray parametersArray = (JSONArray) mapJson3.get("parameters");
                        Object[] parameters = parametersArray.toArray();
                        List<JieKouBean.JieKouParameter> jieKouParameterList = parseSwaggerUiParameters(parameters);
                        Map<String, Object> responses = (JSONObject) mapJson3.get("responses");
                        List<JieKouBean.JieKouResponse> jieKouResponseList = parseSwaggerUiResponses(responses);
                        String t0 = (String) tag[0];
                        List<TagWithProtocol> oldTag = tagMap.get(t0);
                        if (oldTag != null) {
                            oldTag.add(new TagWithProtocol(t0, summary, pathUrl));
                        } else {
                            oldTag = new ArrayList<TagWithProtocol>();
                            oldTag.add(new TagWithProtocol(t0, summary, pathUrl));
                            tagMap.put(t0, oldTag);
                        }
                        jieKouMap.put(pathUrl, new JieKouBean(t0, summary, pathUrl, operationId, requestMode,
                                jieKouParameterList, jieKouResponseList));
                    }
                }
            }
        }
        return result;
    }

    /**
     * 解析swagger接口的响应接口
     *
     * @param responses 响应内容
     */
    private static List<JieKouBean.JieKouResponse> parseSwaggerUiResponses(Map<String, Object> responses) {
        List<JieKouBean.JieKouResponse> jieKouResponseList = new ArrayList<JieKouBean.JieKouResponse>();
        for (Map.Entry<String, Object> entry : responses.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Map<String, Object> content = (JSONObject) value;
            String description = (String) content.get("description");
            Map<String, Object> schema = (JSONObject) content.get("schema");
            String type = (String) schema.get("$ref");
            if (null == type)
                type = (String) schema.get("type");
            jieKouResponseList.add(new JieKouBean.JieKouResponse(key, description, type));
        }
        return jieKouResponseList;
    }

    /**
     * 解析每个接口的请求参数
     *
     * @param parameters 请求参数
     */
    private static List<JieKouBean.JieKouParameter> parseSwaggerUiParameters(Object[] parameters) {
        List<JieKouBean.JieKouParameter> jieKouParameterList = new ArrayList<JieKouBean.JieKouParameter>();
        for (Object obj : parameters) {
            Map<String, Object> parameter = (JSONObject) obj;
            String name = (String) parameter.get("name");//参数名称
//            String in = (String) parameter.get("in");//参数用途
            String description = (String) parameter.get("description");//描述
            Object x_example=parameter.get("x-example");
           // String example = (String) x_example;//示例
            boolean required = (Boolean) parameter.get("required");//是否必须
            String type = (String) parameter.get("type");//参数类型
            jieKouParameterList.add(new JieKouBean.JieKouParameter(name, description, x_example, required, type));
        }
        return jieKouParameterList;
    }

    private static String getSwaggerUi(String reqUrl) {
        if (null == reqUrl)
            reqUrl = SWAGGER_JSON_URL;
        return HttpClientUtils.httpGet(JspWikiCommon.client, reqUrl);
    }

}
