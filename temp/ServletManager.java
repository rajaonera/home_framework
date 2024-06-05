package mg.framework.utils;

import mg.framework.annotations.Controller;
import mg.framework.annotations.Get;
import mg.framework.models.Mapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class ServletManager {
    public static ArrayList<Class<?>> getControllerClasses(String packageName) throws ClassNotFoundException, IOException {
        ArrayList<Class<?>> classes = Utils.getClasses(packageName);
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();

        for(Class<?> classe : classes) {
            if (classe.isAnnotationPresent(Controller.class)) {
                result.add(classe);
            }
        }

        return result;
    }

    public static void getControllerMethod(ArrayList<Class<?>> classes,HashMap<String,Mapping>urlsHashMap) throws Exception {
        HashMap<String, Mapping> result = new HashMap<String, Mapping>();
        if (classes != null) {
            for(Class<?> classe : classes) {
                ArrayList<Method> methods = Utils.getClassMethod(classe);
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Get.class)) {
                        String url = ( method.getAnnotation(Get.class)).value();
                        if (urlsHashMap.get(url)==null) {
                            Mapping mapping = new Mapping(classe.getSimpleName(),method.getName());
                            urlsHashMap.put(url, mapping);
                        } else {
                            throw new Exception("Duplicate annotation : "+ url +" in multiple methods!");
                        }
                    }
                }
            }
        }
    }

    public static Mapping getUrl(HashMap<String, Mapping> maps, String url) {
        Mapping result = null;
        String[] path = url.split("/");
        String newUrl = new String();
        int lenght = path.length-1;
        for (int i = lenght; i >= 0; i--) {
            if (i < lenght) {
                newUrl = "/" + newUrl;
            }
            newUrl = path[i] + newUrl;
            Mapping map = maps.get(newUrl);

            if (map != null) {
                result = map;
            }
        }
        return result;
    }
}
