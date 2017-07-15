package feeker.net.tools.bean;

/**
 * 页面节点
 * Created by LGZ on 2016/8/22.
 */
@SuppressWarnings("unused")
public class Node {
    private long id;
    private String title;
    private String name;
    private int level;
    private long pid;
    private String remark;
    private int status;
    private long group_id;

    public Node() {
    }

    public Node(String title, String name, int level, long pid, String remark, int status, long group_id) {
        this.title = title;
        this.name = name;
        this.level = level;
        this.pid = pid;
        this.remark = remark;
        this.status = status;
        this.group_id = group_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(long group_id) {
        this.group_id = group_id;
    }
}
