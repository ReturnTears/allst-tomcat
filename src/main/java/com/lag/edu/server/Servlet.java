package com.lag.edu.server;

/**
 * Step6
 *
 * @author June
 * @since 2021年01月
 */
public interface Servlet {

    void init() throws Exception;

    void destory() throws Exception;

    void service(Request request, Response response) throws Exception;
}
