package porter;

import com.delta.core.porter.devtools.PorterUtil;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;

public class PorterUtilTest {
    @Test
    public void Test() throws IOException, SQLException {
        PorterUtil.createBeanByTableReference("employee");
    }
}
