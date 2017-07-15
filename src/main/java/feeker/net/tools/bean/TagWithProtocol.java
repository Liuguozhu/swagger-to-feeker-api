package feeker.net.tools.bean;

/**
 * TagWithProtocol bean
 * Created by LGZ on 2016/8/12.
 */

public class TagWithProtocol {
    private String tagName;//标签名称
    private String protocolName;//接口名称
    private String protocolPath;//接口地址

    public TagWithProtocol(String tagName, String protocolName, String protocolPath) {
        this.tagName = tagName;
        this.protocolName = protocolName;
        this.protocolPath = protocolPath;
    }

    @SuppressWarnings("unused")
    public String getTagName() {
        return tagName;
    }
    
    public String getProtocolName() {
        return protocolName;
    }

    public String getProtocolPath() {
        return protocolPath;
    }

    @Override
    public String toString() {
        return "TagWithProtocol{" +
                "tagName='" + tagName + '\'' +
                ", protocolName='" + protocolName + '\'' +
                ", protocolPath='" + protocolPath + '\'' +
                '}';
    }
}
