package com.didichuxing.doraemonkit.kit.fileexplorer;

import com.didichuxing.doraemonkit.constant.SpInputType;

public class SpBean {
    public String key;
    public Object value;
    public Class clazz;

    private SpBean() {

    }

    public SpBean(String key, Object value) {
        this.key = key;
        this.value = value;
        clazz = value.getClass();
    }

    public Object toDefaultClass(String string) {
        setDefaultClass(string);
        return value;
    }

    private void setDefaultClass(String string) {
        switch (clazz.getSimpleName()) {
            case SpInputType.FLOAT:
                value = Float.valueOf(string);
                break;
            case SpInputType.INTEGER:
                value = Integer.valueOf(string);
                break;
            case SpInputType.STRING:
                value = String.valueOf(string);
                break;
            case SpInputType.LONG:
                value = Long.valueOf(string);
                break;
        }

    }

}
