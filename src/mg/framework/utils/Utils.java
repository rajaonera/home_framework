package mg.framework.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.sql.Date;

import mg.framework.annotations.servlet.Get;
import mg.framework.annotations.servlet.Post;
import mg.framework.annotations.servlet.Url;
import mg.framework.exception.CastException;
import mg.framework.servlet.Mapping;

public class Utils {
    public static String getFileName(String fileName, String extension) {
        return fileName.substring(0, (fileName.length() - extension.length()) - 1);
    }
    
    public static ArrayList<Class<?>> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes;
        }
        File packageDirectory = new File(resource.getFile().replace("%20", " "));
        
        for (File file : packageDirectory.listFiles()) {
            if (file.isDirectory()) {
                classes.addAll(Utils.getClasses(packageName + "." + file.getName()));
            } else {
                String className = packageName + "." + Utils.getFileName(file.getName(), "class");
                classes.add(Class.forName(className));
            }
        }
        
        return classes;
    }

    public static ArrayList<Method> getListMethod(Class<?> classes) throws Exception {
        ArrayList<Method> result = new ArrayList<Method>();
        Method[] declaredMethods = classes.getDeclaredMethods();
        for (Method method : declaredMethods) {
            result.add(method);
        }
        return result;
    }

    public static Method getMethod(Class<?> classe, String methodName) throws Exception {
        return classe.getDeclaredMethod(methodName);
    }

    public static Method getMethodAnnoted(Class<?> clazz, String methodName){
        Method [] methods = clazz.getDeclaredMethods();
        Method result = null;
        for (int i=0;i < methods.length;i++){
            if (methods[i].isAnnotationPresent(Url.class) && methods[i].getName().compareTo(methodName)==0) {
                result=methods[i];
            }
        }
        return result;
    }

    public static Object executeSimpleMethod(Object obj, String methodName) throws Exception {
        return obj.getClass().getMethod(methodName).invoke(obj);
    }

    public static String toUpperCase(String word) {
        return word.substring(0,1).toUpperCase() + word.substring(1);
    }

    public static boolean isObject(Class<?> clazz){
        if(clazz == String.class){
            return false;
        }
        if(clazz == Integer.class || clazz == int.class){
            return false;
        }
        if(clazz == Double.class || clazz == double.class){
            return false;
        }
        if(clazz == Date.class){
            return false;
        }
        return true;
    }

    public static Object castValue(String value, Class<?> clazz) throws Exception{
        Object result = null;
        if(clazz == String.class){
            result = value;
        }
        if(clazz == Integer.class || clazz == int.class){
           try {
                result = Integer.valueOf(value);
           } catch(RuntimeException e) {
                throw new CastException("Can't cast Text to Integer!");
           }
        }
        if(clazz == Double.class || clazz == double.class){
            try {
                result = Double.valueOf(value);
            } catch (RuntimeException e) {
                throw new CastException("Can't cast Text to Double!");
            }
        }
        if(clazz == Date.class){
            try {
                result = Date.valueOf(value);
            } catch (RuntimeException e) {
                throw new CastException("Can't cast Text to Date!");
            }
        } 
        return result;
    }

    public static String getVerb(Method method) {
        String verb = "Get";
        if (method.isAnnotationPresent(Get.class)) {
            verb = "Get";
        }
        if (method.isAnnotationPresent(Post.class)) {
            verb = "Post";
        }
        return verb;
    }

    public static boolean isVerbExistInMapping(Mapping map, VerbAction verbAction) {
        for (VerbAction verb : map.getVerbAction()) {
            if (verb.getVerb().compareToIgnoreCase(verbAction.getVerb()) == 0) {
                return true;
            }
        }
        return false;
    } 

    public static VerbAction checkUrlMethod(Mapping mapping, String verb) {
        VerbAction verbAction = null;
        for (VerbAction va : mapping.getVerbAction()) {
            if (va.getVerb().compareToIgnoreCase(verb) == 0) {
                verbAction = va;
            }
        }
        return verbAction;
    }

    public static boolean isNumeric(String value) {
        return value.matches("-?\\d+(\\.\\d+)?");
    }

    public static boolean isDate(String value) {
        boolean result = false;
        try {
            java.sql.Date.valueOf(value);
            result = true;
        }catch (RuntimeException e){
            result = false;
        }
        return result;
    }
}
