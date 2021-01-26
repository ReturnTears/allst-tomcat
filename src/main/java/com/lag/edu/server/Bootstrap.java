package com.lag.edu.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Step1
 * MiniCat的主类
 *
 * @author June
 * @since 2021年01月
 */
public class Bootstrap {

    /**
     *
     */
    private int port = 8080;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * MiniCat启动需要初始化的操作
     *
     * 完成MiniCat 1.0版本
     * 需求：浏览器请求http://localhost:8080,返回一个固定的字符串到页面"Hello Minicat!"
     */
    public void start1() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            // socket接收请求，获取输出流
            OutputStream outputStream = socket.getOutputStream();
            String data = "Hello MiniCat!";
            String responseText = HttpProtocolUtil.getHttpHeader200(data.getBytes().length) + data;
            outputStream.write(responseText.getBytes());
            socket.close();
        }
    }

    /**
     * MiniCat启动需要初始化的操作
     * MiniCat2.0版本
     *
     * 完成MiniCat2.0版本
     * 需求：封装Request和Response对象，返回html静态资源文件
     *
     * @throws IOException 异常处理
     */
    public void start2() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            int count = 0;
            while (count == 0) {
                count = inputStream.available();
            }
            byte[] bytes = new byte[count];
            inputStream.read(bytes);
            System.out.println("请求信息:\n" + new String(bytes));

            socket.close();
        }
    }

    /**
     * MiniCat程序启动入口
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动MiniCat
            bootstrap.start2();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
