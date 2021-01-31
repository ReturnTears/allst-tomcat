package com.lag.edu.server;

import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

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
     * <p>
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
     * <p>
     * 完成MiniCat2.0版本
     * http://localhost:8080/index.html
     * 需求：封装Request和Response对象，返回html静态资源文件
     *
     * @throws IOException 异常处理
     */
    public void start2_1() throws IOException {
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

    public void start2_2() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            response.outputHtml(request.getUrl());
            socket.close();
        }
    }

    /**
     * MiniCat3.0版本：可以请求动态资源(servlet)
     */
    public void start3() throws Exception {
        // 加载解析web.xml,初始化servlet
        loadServlet();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());
            // 静态资源处理
            if (servletMap.get(request.getUrl()) == null) {
                response.outputHtml(request.getUrl());
            } else {
                // 动态资源servlet请求
                HttpServlet httpServlet = servletMap.get(request.getUrl());
                httpServlet.service(request, response);
            }
            socket.close();
        }
    }

    /**
     * MiniCat3.0版本升级版：多线程升级
     */
    public void start3_advs() throws Exception {
        // 加载解析web.xml,初始化servlet
        loadServlet();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);
            requestProcessor.start();
        }
    }

    /**
     * MiniCat3.0版本升级版：使用线程池
     */
    public void start3_pool() throws Exception {
        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        // 加载解析web.xml,初始化servlet
        loadServlet();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);
            threadPoolExecutor.execute(requestProcessor);
        }
    }

    private Map<String, HttpServlet> servletMap = new HashMap<String, HttpServlet>();

    private void loadServlet() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("web.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                // <servlet-name>lag</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>com.lag.edu.server.LagServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();

                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lag
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                servletMap.put(urlPattern, (HttpServlet) Class.forName(servletClass).newInstance());
            }
        } catch (DocumentException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * MiniCat4.0版本：模拟webapps部署效果
     */
    public void start4() throws Exception {
        // 定义一个线程池
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        // 加载解析web.xml,初始化servlet
        loadServlet4();

        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("MiniCat start on Port:" + port);
        while (true) {
            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, myMapperInfo);
            threadPoolExecutor.execute(requestProcessor);
        }
    }

    private MyMapper myMapperInfo = new MyMapper();

    private String getProjectPath() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        String path = "";
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            System.out.println("=========" + JSON.toJSONString(rootElement));
            List<Element> selectNodes = rootElement.selectNodes("//Engine");
            Element ele = (Element) selectNodes.get(0).selectSingleNode("Host");
            path = ele.attributeValue("appBase");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private void loadServlet4() throws Exception{
        //获取存放项目的路径
        String projectPath = getProjectPath();
        MyMapper myMapper = new MyMapper();
        try {
            myMapperInfo = createMapperByXml(projectPath,myMapper);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MyMapper createMapperByXml(String projectPath,MyMapper myMapper) throws Exception{
        MyHost myHost = new MyHost();
        List<MyHost> myHostList = new ArrayList<>();
        myHostList.add(getXmlPath(projectPath,myHost,0));
        myMapper.setMyHostList(myHostList);
        return myMapper;
    }

    //获取xml的路径
    private MyHost getXmlPath(String path, MyHost myHost, int count) throws Exception {
        File dir = new File(path).getAbsoluteFile();
        if (!dir.exists()) {
            System.out.println("文件不存在");
        } else {
            count++;
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isFile() && file.toString().endsWith("xml")) {//如果是文件，并且包含输入输入的文件后缀，就打印出文件路径以及文件名
                    System.out.println("文件名:" + file.getName() + "\t\t绝对路径:"
                            + file.toString());
                    for (MyContext myContext : myHost.getMyContextList()) {
                        if (myContext.getAppName()
                                .equals(file.getParentFile().getParentFile().getName())) {
                            MyWrapper myWrapper = parseXmlLoadServlet(file.toString(), file.getParentFile());
                            myContext.getMyWrapperList().add(myWrapper);
                        }
                    }
                }
                if (file.isDirectory()) {//如果是文件夹就递归
                    if (count == 1) {
                        MyContext myContext = new MyContext();
                        myContext.setAppName(file.getName());
                        myHost.getMyContextList().add(myContext);
                    }
                    getXmlPath(file.toString(), myHost, count);
                }
            }
        }
        return myHost;
    }

    private MyWrapper parseXmlLoadServlet(String xmlPath,File file) throws Exception{
        MyWrapper myWrapper = new MyWrapper();
        //使用dom4j解析
        InputStream resourceAsStream = new FileInputStream(xmlPath);
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(resourceAsStream);
        Element rootElement = document.getRootElement();
        String servletClass = "";
        List<Element> selectNodes = rootElement.selectNodes("//servlet");
        for (int i = 0; i < selectNodes.size(); i++) {
            Element element =  selectNodes.get(i);
            // <servlet-name>lagou</servlet-name>
            Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
            String servletName = servletnameElement.getStringValue();
            // <servlet-class>server.LagouServlet</servlet-class>
            Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
            servletClass = servletclassElement.getStringValue();
            // 根据servlet-name的值找到url-pattern
            Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
            // /lagou
            String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();

            myWrapper.setUrlPath(urlPattern);
            myWrapper.setClsName(servletClass);
        }
        File[] files = file.listFiles();
        MyClassLoader myClassLoader = new MyClassLoader();
        for(File f : files){
            if(f.isDirectory()){
                String path = f.getAbsolutePath();
                Class<?> aClass = myClassLoader.findClass(path+"\\"+servletClass.replace(".","\\")+".class",servletClass);
                try {
                    HttpServlet obj = (HttpServlet)aClass.newInstance();
                    myWrapper.setServlet(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                /*System.out.println("-------"+path);
                URL newurl=new URL(path);
                URLClassLoader classLoader=new URLClassLoader(new URL[]{newurl});
                Class<?> methtClass = classLoader.loadClass(servletClass);
                HttpServlet obj = (HttpServlet)methtClass.newInstance();
                myWrapper.setServlet(obj);*/
            }
        }
        return myWrapper;
    }

    /**
     * MiniCat程序启动入口
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动MiniCat
            bootstrap.start3_pool();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
