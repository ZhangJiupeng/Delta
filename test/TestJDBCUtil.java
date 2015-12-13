import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class TestJDBCUtil {
    private static List<Connection> connections;
    private final static int POOL_SIZE = 10;

    private TestJDBCUtil() {

    }

    static {
        connections = new ArrayList<>();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            for (int i = 0; i < POOL_SIZE; i++) {
                connections.add(DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test",
                        "root", "root"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showPool() {
        System.out.println(Arrays.toString(connections.toArray()));
    }

    private static Connection getConnection() throws SQLException {
        if (connections.size() == 0) {
            for (int i = 0; i < POOL_SIZE; i++) {
                connections.add(DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test",
                        "root", "root"));
            }
        }
        return connections.remove(0);
    }

    private static void free(Connection conn) throws SQLException {
        if (connections.size() == POOL_SIZE) {
            conn.close();
        }
        connections.add(conn);
    }

    public static void doQuery() throws SQLException {
        ResultSet rs = TestJDBCUtil.executeQuery("select * from s");
        while (rs.next()) {
            System.out.println(rs.getString(2));
        }
        rs.close();
    }

    public static int executeUpdate(String sql, Object... objs) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < objs.length; i++) {
            pstmt.setObject(i + 1, objs[i]);
        }
        int returnValue = pstmt.executeUpdate();
        pstmt.close();
        free(conn);
        return returnValue;
    }

    public static ResultSet executeQuery(String sql, Object... objs) throws SQLException {
        Connection conn = getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        for (int i = 0; i < objs.length; i++) {
            pstmt.setObject(i + 1, objs[i]);
        }
        ResultSet rs = pstmt.executeQuery();
//        pstmt.close();
        free(conn);
        return rs;
    }

    public static void main(String[] args) throws Exception {
//        long start = System.currentTimeMillis();
//        System.out.println(System.currentTimeMillis() - start);
        Properties properties = new Properties();
        properties.load(new FileInputStream("config.properties"));
        System.out.println(properties.getProperty("mykiey", "default"));
    }
}
