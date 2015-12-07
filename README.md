# Delta
A lightweight solution for java web developers.

> Potter is provide now, it is a utility for JavaCode-Database options.

```javascript
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
