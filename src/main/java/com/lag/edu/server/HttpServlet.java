package com.lag.edu.server;

/**
 * Step7
 *
 * @author June
 * @since 2021年01月
 */
public abstract class HttpServlet implements Servlet {

    public abstract void doGet(Request request, Response response);

    public abstract void doPost(Request request, Response response);

    @Override
    public void service(Request request, Response response) throws Exception {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }
}
