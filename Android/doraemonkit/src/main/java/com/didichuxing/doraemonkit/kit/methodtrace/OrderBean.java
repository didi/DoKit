package com.didichuxing.doraemonkit.kit.methodtrace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hasee on 2017/4/20.
 */
 class OrderBean implements Comparable<OrderBean> {
    private String time;
    private String functionName;
    private boolean isXit;
    private String content;
    private String costTime = "0";
    private String entTime;
    private String threadId;
    private String threadName;
    private long order;
    private static final String GET_SPLIT_REGEX = " +\\d{1,}-? ?\\.*";
    private static final String GET_NUM_REGEX = "\\d{1,}";

    public OrderBean(String content) {
        this.content = content;
    }

    public OrderBean() {
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public boolean isXit() {
        return isXit;
    }

    public void setXit(boolean xit) {
        isXit = xit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getEntTime() {
        return entTime;
    }

    public void setEntTime(String entTime) {
        this.entTime = entTime;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }


    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public void setTimeAndFuction() {
        if (content == null || content.equals("")) {
            return;
        }

        final String ENT = " ent ";
        final String XIT = " xit ";
        String regex = "";
        regex = getRegex(content, GET_SPLIT_REGEX);
        String str = content.split(regex)[0];
        String thread = getRegex(str.trim(), GET_NUM_REGEX);
        this.threadId = thread;
        String time = "";
        if (content.contains(ENT)) {
            this.setXit(false);
        } else if (content.contains(XIT)) {
            this.setXit(true);
        }
        time = getRegex(regex.trim(), GET_NUM_REGEX);
        //System.out.println(time);
        this.time = time;
        String fuction = "";
        try {
            fuction = content.split(regex)[1].trim();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.println(fuction);
        this.functionName = fuction;
    }


    private static String getRegex(String str, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        String group = "";
        while (matcher.find()) {
            group = matcher.group(0);
            // System.out.println(group.trim());
        }
        return group;
    }

    @Override
    public int compareTo(OrderBean o) {
        // TODO Auto-generated method stub
        //return 0;
        return (int) (order - o.order);
    }


}
