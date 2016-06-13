# ![](http://7xp1jv.com1.z0.glb.clouddn.com/deltamark.png) Delta - A lightweight java web framework
------

![DELTA](http://7xp1jv.com1.z0.glb.clouddn.com/deltaBG.jpg)
















## 什么是 Delta Framework

**Delta** 是一个基于MVC架构的轻量级WEB开发框架，基于jdk1.8开发，目前最新版本更新为 **delta_1.1_beta**，项目完全开源，并提供包装后的jar包方便用户快速开发，下面是对其基本结构的说明。

**Delta** 主要分为以下三大组成部分（此外，项目中还附带了一些文本加解密、生成验证码等常用工具类）：
> * **Porter** 负责数据库与一级实体之间的联系与转换，对JDBC的进一步封装，常应用在业务的DAO层。
> * **Assembler** 负责项目各层代码间的注入与拼装，并提供了动态代理生成组件 ***ProxyFactory*** 等常用工具。
> * **Rover** 处理框架的核心逻辑，主要接管请求关系映射，实现了对物理资源的隐式访问，并提供了黑名单机制以便开发者屏蔽关键资源，提供了表单验证组件 ***XForm*** 以及 ***Convertor*** 简化了以往表单填充、验证等重复操作。

![cmd-markdown-logo](http://7xp1jv.com1.z0.glb.clouddn.com/deltaframework.jpg)





















如果您对SpringMVC、Struts2等框架已经有所了解，同时又很想快速体验Delta Framework的特色，您可以 [从Github下载](https://github.com/ZhangJiupeng/Delta) 或 [浏览由该框架实现的项目](http://lab.zhangjiupeng.com/)，当然，非常欢迎您加入我们的开发过程，及时向我们提出代码解决方案、建议或者不足。项目目前正处于测试阶段，我们会持续跟进更新，敬请关注！

接下来是使用本框架进行快速开发的使用手册，由于Porter模块较为独立，有关它的使用说明将首先列出。

------

## 如何独立使用 Porter

Porter 的功能正如它的名字（搬运工）一样，它实现的核心功能即ORM，它依赖于基于连接池技术的JDBCUtil，JDBCUtil目前解除了对具体数据库的依赖，您可以在需要使用MySQL、Oracle、SQLServer以及其他关系型数据库的项目中通用代码，当然、如果您需要执行数据库相关的特定SQL语句，JDBCUtil中提供的 executeQuery / executeUpdate / execute 等方法便能完成您的需求。此外，由于执行方法的独立以及事务管理的原子性，JDBCUtil提供了组合事务合并查询的一套方法，由于在JDBCUtil是Delta框架中辅助部分，且普通用户无需直接对其操作，该手册中将不再对其详细说明，在delta_1.1版本中(JDBCUtil升级到2.1子版本)，对JDBCUtil分离出SimpleDataSource提供支持，同时也允许用户将通过配置文件配置tomcat-jdbc、c3p0等其他数据源）。

使用Porter前，jdbc.properties的参数配置是必须的，它需要被提供在src主目录下，同时，数据库驱动也是必须的。

![cmd-markdown-logo](http://7xp1jv.com1.z0.glb.clouddn.com/jdbc_conf.jpg)



















配置完成！现在让我们开始第一段代码吧！先看看我要映射的数据表结构，以最简单的 t_user 表为例。

| Field      | Type        |  Extra                   |
| --------   | -----:      | :----:                   |
| id         | int (11)    |   PK, auto_increment     |
| username   | char (32)   |                          |
| password   | char (32)   |                          |

建完数据表，接下来要做的就是编写实体类代码了。

```java
package com.entity;

import com.delta.core.porter.annotation.Entity;
import com.delta.core.porter.annotation.Ignore;

@Entity("t_user")
public class User {
    private int id;
    private String username;
    private String password;

    @Ignore
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    @Ignore
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
```
上面这段代码大多工序是由 `com.delta.core.porter.devtools.PorterUtil` 完成的，直接使用 `PorterUtil.createBeanByTableReference((String)你要映射的数据表名称)` 即可在项目src目录下生成实体类代码（当然，不借助工具自己编写也OK），`@Entity("t_user")`这个注解是指将User类映射到数据表t_user中，那么，接下来的工作就很简单了，相对于Porter而言，如果你不希望它将数据库此项内容搬运到内存中，就请在对应字段的Getter方法上方附上`@Ignore`注解。与之对应的，如果你不希望Porter将对象该字段的值写入数据库中（比如这个字段是自增主键无需提供值），在Setter方法上附上`@Ignore`注解即可。

如果你目前所有步骤均已完成，那么恭喜你，大功已告成，让我们一起来体验一下通过 Porter 同步数据库的优雅代码吧！

```java
    @Test
    public void loadBeansTest() throws IllegalBeanEntityException {
        // 查看User所映射的数据表中的所有数据（实体集以List形式返回）
        Porter.loadBeans(User.class).forEach(System.out::println);
        // List<实体> loadBeans(实体类类型 [过滤条件] (条件中'?'对应的对象));
        Porter.loadBeans(User.class, "id < ?", 4).forEach(System.out::println);
    }

    @Test
    public void saveBeanTest() throws IllegalBeanEntityException {
        User user = new User();
        user.setUsername("zapler");
        Porter.saveBean(user);
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
关于Porter的说明就到此为止了，Porter通常用在项目的Dao层，它仅支持简单的增删改查，更复杂的数据库事务您可以通过JDBCUtil提供的方法完成。

------

## 如何独立使用 Assembler

**Assembler** 大多数的功能都是与Rover相互联系的，但Rover中提供了一种生成简单代理对象的代理工厂类 `com.delta.core.assembler.ProxyFactory`，您只需提供您要代理的接口、实现对象，以及实现了接口 `com.delta.core.assembler.SingleProxy` 的代理方法的类即可，用java8中的Lambda编写就显得尤为简洁了，假如我们已经有了接口TestDao以及TestDaoImpl实现类，
```java
	TestDao dao = ProxyFactory.getProxyInstance(TestDao.class, TestDaoImpl.class, () -> {
		System.out.println("before dao.test()");
	});
	dao.test();
```
dao对象执行test()前会首先执行您提供的代理方法，所以`before dao.test()`会优先输出。

**Assembler** 中还提供了一些为对象注入实现类的方法，Rover正是利用了这个特点实现了IOC，在此不做详细说明。

------

## 如何将代码集成到 Delta 中

终于可以开始集成框架了。首先，你的项目结构需要以MVC为基础。

事实上，WEB项目的灵活与否取决于功能模块间的耦合程度，一个优秀的WEB应用允许开发者付出最小的代价来删减程序模块，这是较粗粒度的解耦。与之相比，同一个模块间的不同分层之间的解耦更值得我们去认真设计，因为它直接影响到我们修改程序功能的效率，况且不同的设计模式决定了不同的开发模式与分工。

需要我们首先理解的是松耦合的意义，最直观的体现则是耦合度低的系统当其需求发生改变时程序结构能够很快适应变化，体现出良好的可维护性，而大多数我们看到的应用，或多或少都存在着不正确使用模式设计的情况，导致自己的程序不能很好的适应外界变化，变得尤为脆弱。

再回到对接口的理解，最开始学习使用接口的时候我们往往并不能理解它的真正含义，因为当时编码并没有考虑到后期维护或者重构的问题，但是在实际项目中我们会发现，在合理的位置加入接口意义重大，在面向接口编码的过程中，我们会直接调用接口声明中提供的规范方法，这使得我们不必再关心此功能的具体实现细节（或者说接口的实现类被我们很好的隔离了）

MVC的概念及详细分层这里不做深入，下面是Delta Framework推荐的分层结构。

![Rover Structure](http://7xp1jv.com1.z0.glb.clouddn.com/rover.jpg)

下面对Action层及Interceptor的实现流程进行说明。

- Action层示例代码

```java
@Controller(namespace = "/user")
public class UserAction {
    UserService userService;
    
    @Detachable // 这个注解声明给Assembler，使其可以对userService进行注入
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
	
	// patterns允许用户将多组URL映射到同一方法中
    @RequestMapping(patterns = {"/login", "/hehe"})
    public String toLogin(HttpServletRequest request, HttpServletResponse response) {
	    // 直接返回该页面的结果
        return "/login.jsp";
    }

	/* 
	 * 这个注解用来建立URL映射，由于这个Action的namespace是"/user"，那么当用户访问
	 * namespace + pattern 即访问 "/user/doLogin" 时便会由Rover寻找并执行此方法。
	 * 此方法的写法与Servlet的写法无较大区别。method注解项中需提供该方法覆盖的提交方式，
	 * 默认为RequestMethod.GET。
	 */
    @RequestMapping(patterns = "/doLogin", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, HttpServletResponse response) {
        User user;
        // 省略获得用户的代码，后面会进行说明 ...
        try {
	        // 执行service层代码
            if ((user = userService.get(user)) == null) {
	            // 在反馈路径前加上"redirect:"前缀指定浏览器进行二次请求跳转
	            // 在反馈路径前加上"out:"前缀指定Rover在response中输出相应文本
                return "redirect:/user/login?note=illegal identity.";
            }
            request.getSession().setAttribute("user", user);
            return "redirect:/friend/list";
        } catch (Exception e) {
            return "redirect:/user/login?note=" + e.getMessage();
        }
    }
}
```

- Interceptor示例代码

```java
public class XSSDispatcher implements ActionInterceptor {
    @Override
    public String intercept(Method method /* 拦截器识别目标方法用 */, 
	    HttpServletRequest httpServletRequest, 
	    HttpServletResponse httpServletResponse) throws AccessDenyException {
        String queryString = httpServletRequest.getQueryString();
        try {
            queryString = queryString == null ? null 
			            : URLDecoder.decode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
        // 这个拦截器负责拦截请求串中的'<'字符， 在执行UserAction前进行拦截
        if (queryString != null && (queryString.contains("<")) {
	        // 拦截有效，跳过Action直接在拦截器中做处理
            return "redirect:/user/login?note=XSS detected.";
        }
        // 返回null表明通过检查，允许其继续执行Action中的方法
        return null;
    }
}

```

##XForm
------
下面说明一下XForm的功能及用法，众所周知，与数据库和项目的依赖关系一样（Porter解决了ORMapping的问题），表单也是客户与服务器间交流的重要媒介之一，然而程序员进行表单填充、检查的过程却十分机械繁琐，所以有必要对其中重复操作进行提取，于是XForm便引入了，XForm其实只是一个接口，delta1.0中，XForm只有一个validate方法，开发者需要对这个表单验证的方法进行重写，另外，与XForm一起配合使用的类还有XFormLoader、XFormConvertor，前者提供了newInstance可以根据request中的Map自动填充XForm的值并生成XForm实体，后者的cast方法可以根据field的名称进行匹配，将二级表单实体的值尝试填充到一级数据实体对象中并随后生成数据实体，这样大大降低了二级实体向一级实体转换的代价，实例代码如下（这就是解释Action时补全后的doLogin方法）。

```java
@RequestMapping(patterns = "/doLogin", method = RequestMethod.POST)
    public String doLogin(HttpServletRequest request, HttpServletResponse response) {
        User user;
        try {
	        // 现在两行代码解决问题
            XForm userLoginForm = XFormLoader.newInstance(request, UserForm.class);
            user = XFormConverter.cast(userLoginForm, User.class);
        } catch (XFormCastException e) {
            return "redirect:/user/login?note=invalid form found.";
        }
        try {
            if ((user = userService.get(user)) == null) {
                return "redirect:/user/login?note=illegal identity.";
            }
            request.getSession().setAttribute("user", user);
            return "redirect:/friend/list";
        } catch (Exception e) {
            return "redirect:/user/login?note=" + e.getMessage();
        }
    }
```

####**Delta Framework 还有很多特性这里并未提到，并且仍有很大提升空间，我一直坚信，框架是为快速开发和维护设计的，所以，希望经验丰富的你能够给我留下一些宝贵的改进建议，让后续版本的delta更加精彩。最后，衷心希望能与大家一起进步，一起成长！**

####[Zapler @ 20151224](http://www.zhangjiupeng.com/)
***Coding is creation, let's enjoy it!***
