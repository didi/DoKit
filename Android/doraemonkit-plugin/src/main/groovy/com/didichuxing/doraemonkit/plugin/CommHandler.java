package com.didichuxing.doraemonkit.plugin;

import org.gradle.util.TextUtil;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jint（金台）
 * 版    本：1.0
 * 创建日期：2020/4/27-10:44
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CommHandler extends DefaultHandler {
    public static final String ATTR_NAME = "android:name";
    private List<String> application = new ArrayList<>();
    private List<String> activities = new ArrayList<>();
    private List<String> services = new ArrayList<>();
    private List<String> providers = new ArrayList<>();
    private List<String> receivers = new ArrayList<>();

    public List<String> getApplication() {
        return application;
    }


    public List<String> getActivities() {
        return activities;
    }


    public List<String> getServices() {
        return services;
    }


    public List<String> getProviders() {
        return providers;
    }


    public List<String> getReceivers() {
        return receivers;
    }


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        String name = attributes.getValue(ATTR_NAME);
        if (StringUtils.isEmpty(name)) {
            return;
        }
//        System.out.println("uri=>" + uri + " localName=>" + localName + " qName=>" + qName + " attributes=>" + attributes.getValue(ATTR_NAME));
        switch (qName) {
            case "application":
                application.add(name);
                break;

            case "activity":
                activities.add(name);
                break;
            case "service":
                services.add(name);
                break;
            case "provider":
                providers.add(name);
                break;
            case "receiver":
                receivers.add(name);
                break;

            default:
                break;
        }
    }
}
