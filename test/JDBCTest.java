import java.sql.*;

public class JDBCTest {
    static long start;
    static long step;
    static long current;

    public static void doQuery() throws Exception {
        Connection conn = DriverManager
                .getConnection("jdbc:mysql://127.0.0.1:3306/test",
                        "root", "root");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select * from s");
//        while (rs.next()) {
//            System.out.println(rs.getString(2));
//        }
    }

    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        start = step = System.currentTimeMillis();

        doQuery();
        current = System.currentTimeMillis();
        System.out.println(current - step);
        step = current;

        doQuery();
        current = System.currentTimeMillis();
        System.out.println(current - step);
        step = current;

        doQuery();
        current = System.currentTimeMillis();
        System.out.println(current - step);
        step = current;

        doQuery();
        current = System.currentTimeMillis();
        System.out.println(current - step);
        step = current;

        doQuery();
        current = System.currentTimeMillis();
        System.out.println(current - step);
        step = current;
        System.out.println(step - start);
    }
}
