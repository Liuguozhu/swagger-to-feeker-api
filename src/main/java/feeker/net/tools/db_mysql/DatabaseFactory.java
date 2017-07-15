package feeker.net.tools.db_mysql;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * mybatis 数据源
 * Created by LGZ on 2016/8/22.
 */
public class DatabaseFactory {
    private static SqlSessionFactory sqlSessionFactory;

    public static void init() {
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }
    private DatabaseFactory() {
        if (sqlSessionFactory == null) {
            init();
        }
    }

    private static DatabaseFactory instance = new DatabaseFactory();

    public static DatabaseFactory getInstance() {
        return instance;
    }

    public  SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
}
