package garbage.logger;

import com.delta.garbage.logger.LogFactory;
import org.junit.Test;

class MyClass {
    public int i;
    public String str;
}

public class LoggerTest {
    @Test
    public void LoggerFunctionTest() {
        MyClass mc = new MyClass();
        MyClass pmc = LogFactory.toProxyInstance(mc, "i");
        pmc.i = 3;
    }
}
