package feeker.net.tools.db_mysql;

import feeker.net.tools.bean.Node;
import feeker.net.tools.dao.NodeDao;
import feeker.net.tools.util.DateUtil;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.Date;

/**
 * 操作数据库
 * Created by LGZ on 2016/8/22.
 */
public class OperateDatabase {

    /**
     * 新增node
     *
     * @param node 导航节点
     * @return node id
     */
    public static long addNode(Node node) {
        long id=0;

        SqlSessionFactory sqlSessionFactory = DatabaseFactory.getInstance().getSqlSessionFactory();
        SqlSession session = sqlSessionFactory.openSession();
        NodeDao dao = session.getMapper(NodeDao.class);
        String title=node.getTitle();
        String name= node.getName();
        long pid = node.getPid();
        Node oldNode = dao.selectByTitleAndPid(title, pid,name);

        if (oldNode == null){
            dao.addNode(node);
            id=node.getId();
        }
        if (null != oldNode) {
            oldNode.setTitle(node.getTitle());
            oldNode.setRemark(node.getRemark());
            oldNode.setStatus(node.getStatus());
            oldNode.setPid(node.getPid());
            oldNode.setGroup_id(node.getGroup_id());
            oldNode.setLevel(node.getLevel());
            oldNode.setName(node.getName());
            dao.updateNode(oldNode);
            id=oldNode.getId();
        }
        session.commit();
        session.close();
        return id;
    }

    public static void main(String[] args) {
        Node node = new Node();
        node.setTitle("新增接口2");
        node.setName("/html/new.tml");
        node.setLevel(1);
        node.setPid(0);
        node.setStatus(2);
        String time = DateUtil.format(new Date(), DateUtil.FORMAT_STRING);
        node.setRemark(time);
        node.setGroup_id(1);
        addNode(node);
        System.out.println("新增node id=" + node.getId());
    }
}
