package test;

import javax.tools.Tool;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ServiceLoader;

/**
 * Contains different methods to check the restrictions of JDK 9
 */
public class JdkTests {

    public static void main(String[] args) throws Exception {
        System.out.println("Java version: " + System.getProperty("java.version"));
        System.out.println();

        System.out.println("Services");
        System.out.println("--------");
        useJdkService();

        System.out.println();
        System.out.println("Reflection");
        System.out.println("----------");
        useReflectionToPublicMethod();
        useReflectionToNonPublicMethodFromClassPath();
        useReflectionToNonPublicMethodFromModule();
        useReflectionToPrivateClassWithinPackagePrivateClass();
        useReflectionToNonPublicClassesFromAModule();
    }

    static void useReflectionToNonPublicClassesFromAModule() {
        System.out.println("Find non public classes from module:");
        try {
            Class<?> privateInner = Class.forName("sun.print.IPPPrintService");
            System.out.println(" - got class " + privateInner);

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useReflectionToPrivateClassWithinPackagePrivateClass() {
        System.out.println("Find package protected class from classpath:");
        try {
            Class<?> privateInner = Class.forName("test.sub.SomeOther$PrivateInner");
            System.out.println(" - got class " + privateInner);
            Constructor<?> constructor = privateInner.getDeclaredConstructor();
            constructor.setAccessible(true);
            Object privateInstance = constructor.newInstance();
            System.out.println(" - created instance " + privateInstance);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useReflectionToNonPublicMethodFromClassPath() {
        System.out.println("Calling protected method from classpath:");
        try {
            Method method = TestTool.class.getDeclaredMethod("somePrivateMethod");
            System.out.println(" - got method " + method);
            method.setAccessible(true);
            System.out.println(" - TestTool.somePrivateMethod(): " + method.invoke(new TestTool()));
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    static void useReflectionToNonPublicMethodFromModule() {
        System.out.println("Calling protected method from module:");
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
        System.out.println("Calling public method:");
        try {
            Class<?> booleanClass = Class.forName("java.lang.Boolean");
            System.out.println(" - got class " + booleanClass);
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
