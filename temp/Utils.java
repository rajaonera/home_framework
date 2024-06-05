package mg.framework.utils;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Utils {
    public static String getFileName(String fileName, String extension) {
        return fileName.substring(0, (fileName.length() - extension.length()) - 1);
    }

    public static ArrayList<Class<?>> getClasses(String packageName) throws ClassNotFoundException{
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes;
        }
        File packageDirectory = new File(resource.getFile().replace("%20", " "));

        for (File file : Objects.requireNonNull(packageDirectory.listFiles())) {
            if (file.isDirectory()) {
                classes.addAll(Utils.getClasses(packageName + "." + file.getName()));
            } else {
                String className = packageName + "." + Utils.getFileName(file.getName(), "class");
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    public static ArrayList<Method> getClassMethod(Class<?> classes) throws Exception {
        Method[] declaredMethods = classes.getDeclaredMethods();
        return new ArrayList<Method>(Arrays.asList(declaredMethods));
    }

    public static Object executeSimpleMethod(Object obj, String methodName) throws Exception {
        return obj.getClass().getMethod(methodName).invoke(obj);
    }
}
