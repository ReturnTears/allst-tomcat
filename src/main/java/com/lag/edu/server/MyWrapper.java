package com.lag.edu.server;

/**
 * @author June
 * @since 2021年01月
 */
public class MyWrapper {
    private String urlPath;

    private String clsName;

    private HttpServlet servlet;

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getClsName() {
        return clsName;
    }

    public void setClsName(String clsName) {
        this.clsName = clsName;
    }

    public HttpServlet getServlet() {
        return servlet;
    }

    public void setServlet(HttpServlet servlet) {
        this.servlet = servlet;
    }
}
