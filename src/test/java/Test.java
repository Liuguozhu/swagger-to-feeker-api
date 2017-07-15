/**
 * test mybatis
 * Created by LGZ on 2016/8/22.
 */

import java.io.Reader;
import java.util.Date;

import feeker.net.tools.bean.Node;
import feeker.net.tools.dao.NodeDao;
import feeker.net.tools.util.DateUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


public class Test {
    private static SqlSessionFactory sqlSessionFactory;
    private static Reader reader;

    static {
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static SqlSessionFactory getSession() {
//        return sqlSessionFactory;
//    }


    /**
     * 测试增加,增加后，必须提交事务，否则不会写入到数据库.
     */
    public static void addNode() {
        Node node = new Node();
        node.setTitle("新增接口");
        node.setName("/html/new.tml");
        node.setLevel(1);
        node.setPid(0);
        node.setStatus(2);
        String time = DateUtil.format(new Date(), DateUtil.FORMAT_STRING);
        node.setRemark(time);
        node.setGroup_id(1);
        SqlSession session = sqlSessionFactory.openSession();
        try {
            NodeDao nodeDao = session.getMapper(NodeDao.class);
            nodeDao.addNode(node);
            session.commit();
            System.out.println("当前新增 id为:" + node.getId());
        } finally {
            session.close();
        }
    }

    public static void select() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
            Node node = session.selectOne("feeker.net.tools.dao.NodeDao.selectById", 1);
            System.out.println(node.getId());
            System.out.println(node.getName());
        } finally {
            session.close();
        }
    }

    public static void main(String[] args) {
        addNode();
    }
}