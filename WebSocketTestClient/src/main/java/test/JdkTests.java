package test;

import javax.tools.Tool;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.ServiceLoader;
import java.util.spi.TimeZoneNameProvider;

/**
 * Contains different methods to check the restrictions of JDK 9
 */
public class JdkTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Java version: " + System.getProperty("java.version"));
        useJdkService();
        useReflectionToPublicMethod();
        useReflectionToNonPublicMethodFromClassPath();
        useReflectionToNonPublicMethodFromModule();
    }

    static void useReflectionToNonPublicMethodFromClassPath() {
        System.out.println("Calling protected method using reflection from classpath:");
        try {
            Method method = DummyTool.class.getDeclaredMethod("somePrivateMethod");
            System.out.println(" - got method " + method);
            method.setAccessible(true);
            System.out.println(" - DummyTool.somePrivateMethod(): " + method.invoke(new DummyTool()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useReflectionToNonPublicMethodFromModule() {
        System.out.println("Calling protected method using reflection from module:");
        try {
            Method method = ClassLoader.class.getDeclaredMethod("getPackages");
            System.out.println(" - got method " + method);
            method.setAccessible(true);
            System.out.println(" - ClassLoader.getPackages(): " + method.invoke(JdkTests.class.getClassLoader()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useReflectionToPublicMethod() {
        System.out.println("Calling public method using reflection:");
        try {
            Class<?> booleanClass = Class.forName("java.lang.Boolean");
            Method method = booleanClass.getDeclaredMethod("valueOf", String.class);
            System.out.println(" - got method " + method);
            System.out.println(" - Boolean.valueOf('true'): " + method.invoke(null, "true"));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useJdkService() {
        System.out.println("Looking for javax.tools.Tool:");
        try {
            for (Tool tool : ServiceLoader.load(Tool.class)) {
                System.out.println(" - Found: " + tool);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
