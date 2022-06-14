package com.didichuxing.doraemonkit.kit.permissionlist;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XMLparser {
    private HashMap<String,MyPermissionInfo> permissonmap;

    public ArrayList<MyPermissionInfo> parser(InputStream path){
        // 1.权限名-对象的映射map
        permissonmap=new HashMap<>();
        //2.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //3.创建DocumentBuilder对象
        try {
            ArrayList<MyPermissionInfo> myPermissionInfoList =new ArrayList<>();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d = builder.parse(path);
            NodeList sList = d.getElementsByTagName("permission");
            //System.out.println(sList.getLength());
            for(int i=0;i<sList.getLength();i++){
                Element element = (Element) sList.item(i);
                String key=element.getAttribute("android:name");
                String[] names=element.getAttribute("android:name").split("\\.");
                String name=names[names.length-1];
                String[] permissionGroups=element.getAttribute("android:permissionGroup").split("\\.");
                String permissionGroup=permissionGroups[permissionGroups.length-1];
                String[] descriptions=element.getAttribute("android:description").split("\\_");
                String description=descriptions[descriptions.length-1];
                String protectionLevel=element.getAttribute("android:protectionLevel");
                //System.out.println("[no:"+i+"]"+"name:"+name+" permissiongroup:"+permissionGroup+" descri:"+description+" level:"+protectionLevel);

                MyPermissionInfo p=new MyPermissionInfo(name,permissionGroup,description,protectionLevel);
                myPermissionInfoList.add(p);
                permissonmap.put(key,p);
            }
            return myPermissionInfoList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public  HashMap<String,MyPermissionInfo> getMap(){
        return permissonmap;
    }
}
