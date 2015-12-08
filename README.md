# Delta

######Potter has been provided, it is base on a simple JDBC connection pool and support annotation for beans. In short, it can synchronize your java bean and data table easier, as the examples list below.

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

***Coding is creation, let's enjoy it!***
