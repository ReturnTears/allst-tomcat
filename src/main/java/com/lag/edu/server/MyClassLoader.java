package com.lag.edu.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/**
 * @author June
 * @since 2021年01月
 */
public class MyClassLoader extends ClassLoader {
    @Override
    protected Class<?> findClass(String name, String servletName) {
        String myPath = name;
        System.out.println(myPath);
        byte[] cLassBytes = null;
        Path path = null;
        Class clazz = null;
        try {
            if (!servletName.endsWith(".LagServlet")) {
                return super.loadClass(name);
            }

            InputStream is = new FileInputStream(myPath);
            byte[] bytes = inputStream2byte(is);
            clazz = defineClass(servletName, bytes, 0, bytes.length);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public static byte[] inputStream2byte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while ((rc = inputStream.read(buff, 0, 100)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
