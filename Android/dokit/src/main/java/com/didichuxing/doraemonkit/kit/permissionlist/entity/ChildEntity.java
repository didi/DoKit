package com.didichuxing.doraemonkit.kit.permissionlist.entity;
import com.didichuxing.doraemonkit.kit.permissionlist.MyPermissionInfo;

/**
 * 子项数据的实体类
 */
public class ChildEntity {

    private MyPermissionInfo child;

    public ChildEntity(MyPermissionInfo child) {
        this.child = child;
    }

    public MyPermissionInfo getChild() {
        return child;
    }

    public void setChild(MyPermissionInfo child) {
        this.child = child;
    }
}
