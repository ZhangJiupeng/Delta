import com.delta.core.porter.Porter;
import com.delta.core.util.JDBCUtil;
import com.delta.core.util.SimpleDataSource;
import com.delta.depend.util.PropParser;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PolTest {
    @Test
    public void doTest() throws SQLException {
        System.out.println("haha@");
        PoolProperties p = new PoolProperties();
        p.setUrl("jdbc:mysql://localhost:3306/mysql");
        p.setDriverClassName("com.mysql.jdbc.Driver");
        p.setUsername("root");
        p.setPassword("root");
        p.setJmxEnabled(true);
        p.setTestWhileIdle(false);
        p.setTestOnBorrow(true);
        p.setValidationQuery("SELECT 1");
        p.setTestOnReturn(false);
        p.setValidationInterval(30000);
        p.setTimeBetweenEvictionRunsMillis(30000);
        p.setMaxActive(100);
        p.setInitialSize(10);
        p.setMaxWait(10000);
        p.setRemoveAbandonedTimeout(60);
        p.setMinEvictableIdleTimeMillis(30000);
        p.setMinIdle(10);
        p.setLogAbandoned(true);
        p.setRemoveAbandoned(true);
        p.setJdbcInterceptors(
                "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;"+
                        "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
        DataSource datasource = new DataSource();
        datasource.setPoolProperties(p);
//        JDBCUtil.setDataSource(datasource);
        ResultSet rs = JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        JDBCUtil.executeQuery("select 1");
        rs.next();
        System.out.println(rs.getString(1));

   }
}
