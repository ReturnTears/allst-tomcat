package com.lag.edu.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author June
 * @since 2021年01月
 */
public class MyHost {
    private List<MyContext> myContextList = new ArrayList<>();

    public List<MyContext> getMyContextList() {
        return myContextList;
    }

    public void setMyContextList(List<MyContext> myContextList) {
        this.myContextList = myContextList;
    }
}
