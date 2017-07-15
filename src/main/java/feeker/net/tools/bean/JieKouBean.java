package feeker.net.tools.bean;

import java.util.List;

/**
 * SwaggerUi json 中接口的实体类
 * Created by LGZ on 2016/8/11.
 */
public class JieKouBean {
    String tag;//接口所在标签名称
    String protocolName;//接口名称
    String pathUrl;//请求地址
    String author;//接口作者
    String requestMode;//请求方式
    List<JieKouParameter> jieKouParameter;
    List<JieKouResponse> jieKouResponse;

    public JieKouBean(String tag, String protocolName, String pathUrl, String author, String requestMode,
                      List<JieKouParameter> jieKouParameter, List<JieKouResponse> jieKouResponse) {
        this.tag = tag;
        this.protocolName = protocolName;
        this.pathUrl = pathUrl;
        this.author = author;
        this.requestMode = requestMode;
        this.jieKouParameter = jieKouParameter;
        this.jieKouResponse = jieKouResponse;
    }

    public String getTag() {
        return tag;
    }

    public String getProtocolName() {
        return protocolName;
    }

    public String getPathUrl() {
        return pathUrl;
    }

    public String getAuthor() {
        return author;
    }

    public String getRequestMode() {
        return requestMode;
    }

    public List<JieKouParameter> getJieKouParameter() {
        return jieKouParameter;
    }

    public List<JieKouResponse> getJieKouResponse() {
        return jieKouResponse;
    }

    @Override
    public String toString() {
        return "JieKouBean{" +
                "pathUrl='" + pathUrl + '\'' +
                ", requestMode='" + requestMode + '\'' +
                ", jieKouParameter=" + jieKouParameter +
                ", jieKouResponse=" + jieKouResponse +
                '}';
    }

    /**
     * 参数
     */
    public static class JieKouParameter {
        String parameterName;//参数名称
        String parameterPurpose;//参数用途
        String parameterDescription;//描述
        Object parameterExample;//示例
        boolean parameterRequired;//是否必须
        String parameterType;//参数类型

        /**
         * 不含参数用途的构造方法
         *
         * @param parameterName        参数名称
         * @param parameterDescription 描述
         * @param parameterRequired    是否必须
         * @param parameterType        参数类型
         */
        public JieKouParameter(String parameterName, String parameterDescription,Object parameterExample,
                               boolean parameterRequired, String parameterType) {
            this.parameterName = parameterName;
            this.parameterDescription = parameterDescription;
            this.parameterExample = parameterExample;
            this.parameterRequired = parameterRequired;
            this.parameterType = parameterType;
        }

        /**
         * 含参数用途的构造方法
         *
         * @param parameterName        参数名称
         * @param parameterPurpose     参数用途
         * @param parameterDescription 描述
         * @param parameterRequired    是否必须
         * @param parameterType        参数类型
         */
        @SuppressWarnings("unused")
        public JieKouParameter(String parameterName, String parameterPurpose,
                               String parameterDescription,Object parameterExample,
                               boolean parameterRequired, String parameterType) {
            this.parameterName = parameterName;
            this.parameterPurpose = parameterPurpose;
            this.parameterDescription = parameterDescription;
            this.parameterExample = parameterExample;
            this.parameterRequired = parameterRequired;
            this.parameterType = parameterType;
        }

        public String getParameterName() {
            return parameterName;
        }

        @SuppressWarnings("unused")
        public String getParameterPurpose() {
            return parameterPurpose;
        }

        public String getParameterDescription() {
            return parameterDescription;
        }

        public boolean isParameterRequired() {
            return parameterRequired;
        }

        public String getParameterType() {
            return parameterType;
        }

        public Object getParameterExample() {
            return parameterExample;
        }

        @Override
        public String toString() {
            return "JieKouParameter{" +
                    "parameterName='" + parameterName + '\'' +
                    ", parameterPurpose='" + parameterPurpose + '\'' +
                    ", parameterDescription='" + parameterDescription + '\'' +
                    ", parameterExample='" + parameterExample + '\'' +
                    ", parameterRequired=" + parameterRequired +
                    ", parameterType='" + parameterType + '\'' +
                    '}';
        }
    }

    /**
     * 响应体
     */
    public static class JieKouResponse {
        String status;//HTTP状态码
        String description;//响应描述
        String schema;//响应模型

        public JieKouResponse(String status, String description, String schema) {
            this.status = status;
            this.description = description;
            this.schema = schema;
        }

        public String getStatus() {
            return status;
        }

        public String getDescription() {
            return description;
        }

        public String getSchema() {
            return schema;
        }

        @Override
        public String toString() {
            return "JieKouResponse{" +
                    "status='" + status + '\'' +
                    ", description='" + description + '\'' +
                    ", schema='" + schema + '\'' +
                    '}';
        }
    }


}
