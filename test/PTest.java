import com.delta.core.porter.Porter;
import com.delta.core.porter.except.IllegalBeanEntityException;
import porter.JDBCTemplateClass;

import java.util.List;

public class PTest {
    public static void main(String[] args) throws IllegalBeanEntityException {
        List list = Porter.loadBeans(JDBCTemplateClass.class);
    }
}
