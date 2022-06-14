package com.didichuxing.doraemonkit.kit.permissionlist;

import com.didichuxing.doraemonkit.kit.permissionlist.entity.ChildEntity;
import com.didichuxing.doraemonkit.kit.permissionlist.entity.ExpandableGroupEntity;

import java.util.ArrayList;

/**
 * Depiction:
 * Author: teach
 * Date: 2017/3/20 15:51
 */
public class GroupModel {

    /**
     * 获取可展开收起的组列表数据(默认展开)
     *
     * @param PermissionInfoList  所有权限列表
     * @param grouplist 分类权限列表
     * @return
     */
    public static ArrayList<ExpandableGroupEntity> getExpandableGroups(ArrayList<MyPermissionInfo> PermissionInfoList,
                                                                       ArrayList<ArrayList<MyPermissionInfo>> grouplist) {
        String[] titlename=new String[]{"普通权限_normal","签名权限_signature","危险权限_dangerous","未分类权限"};
        int sum=0;//总权限数目
        for(ArrayList<MyPermissionInfo> arr:grouplist){
            sum+=arr.size();
        }
        ArrayList<ExpandableGroupEntity> groups = new ArrayList<>();
        for (int i = 0; i < grouplist.size(); i++) {
            ArrayList<ChildEntity> children = new ArrayList<>();
            ArrayList<MyPermissionInfo> permissionlist=grouplist.get(i);
            for (int j = 0; j < permissionlist.size(); j++) {
                children.add(new ChildEntity(permissionlist.get(j)));
            }
            groups.add(new ExpandableGroupEntity(titlename[i]+" ("+permissionlist.size()+
                "/"+sum+")",
                "", true, children));
        }
        return groups;
    }

}
