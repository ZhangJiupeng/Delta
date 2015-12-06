package porter;

import com.delta.core.porter.Porter;
import com.delta.core.porter.except.IllegalBeanEntityException;

public class Test {
    public static void main(String[] args) throws IllegalBeanEntityException {
        JDBCTemplateClass templateClass = new JDBCTemplateClass();
        templateClass.setSname("ygx");
        templateClass.setAge(30);
        templateClass.setSex('k');
        Porter.saveBean(templateClass);
    }
}
