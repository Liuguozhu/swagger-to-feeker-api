package feeker.net.tools.bean;

import java.util.List;

/**
 * 自定义的响应类
 * Created by LGZ on 2016/8/12.
 */
public class Definition {
    private String definitionName;//类名
    private List<DefinitionProperties> propertiesList;//属性、字段

    public Definition(String definitionName, List<DefinitionProperties> propertiesList) {
        this.definitionName = definitionName;
        this.propertiesList = propertiesList;
    }

    @SuppressWarnings("unused")
    public String getDefinitionName() {
        return definitionName;
    }

    public List<DefinitionProperties> getPropertiesList() {
        return propertiesList;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "definitionName='" + definitionName + '\'' +
                ", propertiesList=" + propertiesList +
                '}';
    }

    public static class DefinitionProperties {
        private String propertiesName;//属性名
        private String propertiesType;//属性类型
        private String propertiesDescription;//属性描述
        private Object[] propertiesEnum;//限定值
        private String referenceClass;//引用类

        public DefinitionProperties(String propertiesName, String propertiesType, String propertiesDescription, Object[] propertiesEnum, String referenceClass) {
            this.propertiesName = propertiesName;
            this.propertiesType = propertiesType;
            this.propertiesDescription = propertiesDescription;
            this.propertiesEnum = propertiesEnum;
            this.referenceClass = referenceClass;
        }

        public String getPropertiesName() {
            return propertiesName;
        }

        public String getPropertiesType() {
            return propertiesType;
        }

        public String getPropertiesDescription() {
            return propertiesDescription;
        }

        public Object[] getPropertiesEnum() {
            return propertiesEnum;
        }

        public String getReferenceClass() {
            return referenceClass;
        }

        public String propertiesEnumToString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            for (Object properties : propertiesEnum) {
                builder.append("'");
                builder.append(properties);
                builder.append("',");
            }
            if (builder.lastIndexOf(",") != -1)
                builder.delete(builder.lastIndexOf(","), builder.length());
            builder.append("]");
            return builder.toString();
        }

        @Override
        public String toString() {
            return "DefinitionProperties{" +
                    "propertiesName='" + propertiesName + '\'' +
                    ", propertiesType='" + propertiesType + '\'' +
                    ", referenceClass='" + referenceClass + '\'' +
                    ", propertiesDescription='" + propertiesDescription + '\'' +
                    ", propertiesEnum='" + propertiesEnumToString() + '\'' +
                    '}';
        }
    }

}
