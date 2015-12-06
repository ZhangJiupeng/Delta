package porter;

import com.delta.core.porter.annotation.Entity;
import com.delta.core.porter.annotation.TreatAs;

@Entity("s")
public class JDBCTemplateClass {
    private int sid;
    @TreatAs("s_name") // TODO This Annotation Not Done Yet.
    private String sname;
    private int age;
    private char sex;

    public JDBCTemplateClass() {

    }

    //    @Ignore
    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "JDBCTemplateClass{" +
                "sid=" + sid +
                ", sname='" + sname + '\'' +
                ", age=" + age +
                ", sex=" + sex +
                '}';
    }
}
