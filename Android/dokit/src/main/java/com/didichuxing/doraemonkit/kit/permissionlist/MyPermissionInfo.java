package com.didichuxing.doraemonkit.kit.permissionlist;

public class MyPermissionInfo {
    private String name;
    private String permissionGroup;
    private String description;
    private String label;
    private String protectionLevel;

    public MyPermissionInfo(String name, String permissionGroup, String description, String protectionLevel) {
        this.name = name;
        this.permissionGroup = permissionGroup;
        this.description = description;
        this.protectionLevel = protectionLevel;
        label="";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    public String getPermissionGroup() {
        return permissionGroup;
    }

    public String getDescription() {
        return description;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }
}
