package feeker.net.tools.dao;

import feeker.net.tools.bean.Node;

import java.util.List;

/**
 * dao
 * Created by LGZ on 2016/8/22.
 */
@SuppressWarnings("unused")
public interface NodeDao {

    Node selectById(int id);

    List<Node> selectAll();

    void addNode(Node node);

    Node selectByTitleAndPid(String title, long pid, String name);

    void updateNode(Node node);

}
