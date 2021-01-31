package com.lag.edu.server;

import java.util.ArrayList;
import java.util.List;

/**
 * @author June
 * @since 2021年01月
 */
public class MyMapper {
    private List<MyHost> myHostList = new ArrayList<>();

    public List<MyHost> getMyHostList() {
        return myHostList;
    }

    public void setMyHostList(List<MyHost> myHostList) {
        this.myHostList = myHostList;
    }
}
