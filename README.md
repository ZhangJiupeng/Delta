# Delta

######Potter is a kind of JDBC utility and it has been provided, it is base on a simple JDBC connection pool and support annotation for beans. In short, it can synchronize your java bean and data table easier, as the examples list below.

```java
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
```
As you see, the only thing to do is to put our annotation inside your java bean code and ensure it fields name correspond to your data table, just like this guy.

```java
@Entity("s")
public class JDBCTemplateClass {
    private int sid;
    private String sname;
    private int age;
    private char sex;

    public JDBCTemplateClass() {

    }

    @Ignore // this value will be ignored when loading beans from data table
    public int getSid() {
        return sid;
    }

    @Ignore // this value will be ignored when saving to the data table
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
```

***Coding is creation, let's enjoy it!***
