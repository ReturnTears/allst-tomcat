package com.lag.edu.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author June
 * @since 2021年01月
 */
public class MyContext {
    private String appName;
    private List<MyWrapper> myWrapperList = new ArrayList<>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<MyWrapper> getMyWrapperList() {
        return myWrapperList;
    }

    public void setMyWrapperList(List<MyWrapper> myWrapperList) {
        this.myWrapperList = myWrapperList;
    }
}
