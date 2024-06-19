package mg.framework.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.framework.annotations.Controller;
import mg.framework.annotations.Get;
import mg.framework.exception.DuplicateException;
import mg.framework.exception.PackageException;
import mg.framework.exception.ReturnException;
import mg.framework.exception.UrlNotFoundException;
import mg.framework.models.Mapping;
import mg.framework.models.ModelView;


@SuppressWarnings("deprecation")
public class ServletManager {
    public static ArrayList<Class<?>> getControllerClasses(String packageName) throws Exception {
        ArrayList<Class<?>> classes = Utils.getClasses(packageName);
        ArrayList<Class<?>> result = new ArrayList<Class<?>>();
        
        for(Class<?> classe : classes) {
            if (classe.isAnnotationPresent(Controller.class)) {
                result.add(classe);
            } 
        }
        if (result.size() <= 0) {
            throw new PackageException(packageName);
        }
        return result;
    }

    public static HashMap<String,Mapping> getControllerMethod(ArrayList<Class<?>> classes) throws Exception {
        HashMap<String,Mapping> result = new HashMap<>();
        if (classes != null) {
            for(Class<?> classe : classes) {
                ArrayList<Method> methods = Utils.getListMethod(classe);
                for (Method method : methods) {
                    if (method.isAnnotationPresent(Get.class)) {
                        String url = ((Get) method.getAnnotation(Get.class)).value();
                        if (result.get(url)==null) {
                            Mapping mapping = new Mapping(classe.getSimpleName(),method.getName());
                            result.put(url, mapping);
                        } else {
                            throw new DuplicateException();
                        }
                    }
                }
            }
        }
        return result;
    }

    public static Mapping getUrl(HashMap<String, Mapping> maps, String url) throws Exception {
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
        if (result == null) {
            throw new UrlNotFoundException();
        }
        return result;
    }

    public static void executeMethodController(String url, HttpServletRequest request, HttpServletResponse response, String packageName, HashMap<String,Mapping> controllerAndMethod) throws Exception {
        PrintWriter out = response.getWriter();        
        Mapping map = ServletManager.getUrl(controllerAndMethod, url);

        String className = map.getClassName();
        String methodName = map.getMethodName();
        String classPath = packageName+"."+className;
        
        Class<?> controllerClass = Class.forName(classPath);
        Method controllerMethod = Utils.getMethod(controllerClass, methodName);

        if (controllerMethod.getReturnType() == String.class || controllerMethod.getReturnType() == ModelView.class) {
            Object ctrlObj = controllerClass.newInstance(); 
            
            if (controllerMethod.getReturnType() == String.class) {
                String methodReturn =  (String) Utils.executeSimpleMethod(ctrlObj, methodName);
            
                out.print("After executing the "+ methodName +" method in the "+ className +".class, this method returned the value: ");
                out.println(methodReturn);
            } 
            if (controllerMethod.getReturnType() == ModelView.class) {
                ModelView modelView = (ModelView) Utils.executeSimpleMethod(ctrlObj, methodName);
                dispatchModelView(modelView, request, response);
            }
        } else {
            throw new ReturnException(methodName, className);
        }
    }

    public static void dispatchModelView(ModelView modelView, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Map.Entry<String,Object> data : modelView.getData().entrySet()){
            String varName = data.getKey();
            Object varValue = data.getValue();
            request.setAttribute(varName,varValue);
        }
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/"+modelView.getUrl());
        dispatcher.forward(request,response);
    }
}
