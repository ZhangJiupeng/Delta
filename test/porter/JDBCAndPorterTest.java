package porter;

import com.delta.core.porter.Porter;
import com.delta.core.porter.except.IllegalBeanEntityException;
import com.delta.core.util.JDBCUtil;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCAndPorterTest {

    @Test
    public void MySQLConnectionTest() {
        try (ResultSet rs = JDBCUtil.executeQuery("select now()")) {
            if (rs.next()) System.out.println(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void charInsertTest() {
        JDBCUtil.executeUpdate("update s set sex = ? where false", 's');
    }

    @Test
    public void loadBeansTest() throws IllegalBeanEntityException {
        Porter.loadBeans(JDBCTemplateClass.class).forEach(System.out::println);
        Porter.loadBeans(JDBCTemplateClass.class, "sid < ?", 4).forEach(System.out::println);
    }

    @Test
    public void saveBeanTest() throws IllegalBeanEntityException {
        JDBCTemplateClass templateClass = new JDBCTemplateClass();
        templateClass.setSname("zapler");
        Porter.saveBean(templateClass);
    }

    @Test
    public void removeBeanTest() throws IllegalBeanEntityException {
        Porter.removeBeans(JDBCTemplateClass.class, "sid > 6");
    }

    @Test
    public void updateBeanTest() throws IllegalBeanEntityException {
        JDBCTemplateClass templateClass = new JDBCTemplateClass();
        templateClass.setSname("zapler");
        Porter.updateBeans(templateClass, "sid = 6");
    }

}
