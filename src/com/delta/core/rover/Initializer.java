package com.delta.core.rover;

import com.delta.core.assembler.Assembler;
import com.delta.core.assembler.except.DetachException;
import com.delta.core.rover.except.IllegalControllerException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;
import java.net.URL;

public class Initializer {
    // TODO 这里的Controller（Action）类地址、Action-Service映射、Service-Dao映射均从配置文件中读取

    private static void initRequestFilter() {
        RequestFilter.characterSet = "UTF-8";
    }

    /*  try {
            ActionMapping.load(PlayAction.class);
            ActionMapping.load(TestAction.class);
        } catch (IllegalControllerException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Object action;
        Object actionInterceptor;
        Object serviceImpl;
        Object daoImpl;

        // load PlayAction
        action = PlayAction.class.newInstance();
        ActionMapping.controllers.put(action.getClass().getName(), action);

        // load TestAction
        Class<?> actionClass = TestAction.class;
        Class<?> actionInterceptorClass = TestInterceptor.class;
        Class<?> serviceImplClass = TestServiceImpl.class;
        Class<?> daoImplClass = TestDaoImpl.class;

        daoImpl = daoImplClass.newInstance();
        serviceImpl = serviceImplClass.newInstance();
        action = actionClass.newInstance();
        actionInterceptor = actionInterceptorClass.newInstance();

        // inject implements
        serviceImpl = Assembler.detach(serviceImpl, daoImpl);   // 在ServiceImpl里注入Dao的实现
        action = Assembler.detach(action, serviceImpl);         // 在Action里注入ServiceImpl
        ActionMapping.controllers.put(action.getClass().getName(), action);

        // regist for action-interceptors
        ActionMapping.interceptors.put(actionClass, (ActionInterceptor) actionInterceptor);
    }
    */

    public static void initActionAndService() throws DocumentException, ClassNotFoundException,
            IllegalAccessException, InstantiationException, DetachException, IllegalControllerException, SAXException {

        String resourceName = "delta.xml";
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);
        if (url == null) {
            url = Initializer.class.getClassLoader().getResource(resourceName);
        }
        assert url != null;

        SAXReader reader = new SAXReader();
        reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document doc = reader.read(new File(url.getPath()));
        Element root = doc.getRootElement();

        for (Object object : root.elements()) {
            Element element = (Element) object;
            switch (element.getName()) {
                case "settings":
                    for (Object object1 : element.elements()) {
                        Element element1 = (Element) object1;
                        switch (element1.getName()) {
                            case "welcome-page":
                                RequestFilter.welcomePage = element1.getStringValue();
                                break;
                            case "handle-error":
                                Rover.handleError = element1.getStringValue().equals("true");
                                break;
                            case "character-encoding":
                                RequestFilter.characterSet = element1.getStringValue();
                                break;
                            case "blacklist":
                                RequestFilter.blackList.add(element1.getStringValue());
                                break;
                        }
                    }
                    break;
                case "action":
                    Object action = Class.forName(element.attributeValue("class")).newInstance();
                    for (Object object1 : element.elements()) {
                        Element element1 = (Element) object1;
                        switch (element1.getName()) {
                            case "interceptor":
                                ActionMapping.interceptors.put(action.getClass(),
                                        (ActionInterceptor) Class.forName(element1.attributeValue("class")).newInstance());
                                break;
                            case "service":
                                Object serviceImpl = Class.forName(element1.attributeValue("impl")).newInstance();
                                for (Object object2 : element1.elements()) {
                                    Element element2 = (Element) object2;
                                    if (element2.getName().equals("dao")) {
                                        Object daoImpl = Class.forName(element2.attributeValue("impl")).newInstance();
                                        serviceImpl = Assembler.detach(serviceImpl, daoImpl);
                                    }
                                }
                                action = Assembler.detach(action, serviceImpl);
                                break;
                        }
                    }
                    ActionMapping.load(action.getClass());
                    ActionMapping.controllers.put(action.getClass().getName(), action);
                    break;
            }
        }
    }

    public static void doInit() throws Exception {
        initRequestFilter();
        initActionAndService();
    }

}
