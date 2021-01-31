package com.lag.edu.server;

import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Step9
 * @author June
 * @since 2021年01月
 */
public class RequestProcessor extends Thread {
    private Socket socket;
    private Map<String, HttpServlet> servletMap;
    private MyMapper myMapper;

    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    public RequestProcessor(Socket socket, MyMapper myMapper) {
        this.socket = socket;
        this.myMapper = myMapper;
    }

    @Override
    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            HttpServlet httpServlet = null;

            List<MyContext> myContextList =  myMapper.getMyHostList().get(0).getMyContextList();
            String[] urlArray = request.getUrl().trim().split("/");
            for(MyContext myContext : myContextList){
                if(myContext.getAppName().equals(urlArray[1])){
                    List<MyWrapper> myWrapperList = myContext.getMyWrapperList();
                    for(MyWrapper myWrapper : myWrapperList){
                        if(myWrapper.getUrlPath().replace("/","").equals(urlArray[2])){
                            httpServlet  = myWrapper.getServlet();
                        }
                    }
                }
            }

            // 静态资源处理
            if (servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源servlet请求
                // HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
