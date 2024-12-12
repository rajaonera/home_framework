package mg.framework.utils;

import java.lang.annotation.Annotation;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;
import java.lang.reflect.Field;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import mg.framework.annotations.Controller;
import mg.framework.annotations.Url;
import mg.framework.annotations.RequestParam;
import mg.framework.annotations.RestAPI;
import mg.framework.exception.DuplicateException;
import mg.framework.exception.PackageException;
import mg.framework.exception.ReturnException;
import mg.framework.exception.UrlNotFoundException;
import mg.framework.models.Mapping;
import mg.framework.models.ModelView;
import mg.framework.models.Session;


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
                    if (method.isAnnotationPresent(Url.class)) {
                        String verb = Utils.getVerb(method);

                        String url = ((Url) method.getAnnotation(Url.class)).value();
                        Mapping map = result.get(url);
                        
                        if (map == null) {
                            Mapping mapping = new Mapping(classe.getSimpleName());
                            mapping.addVerbAction(new VerbAction(method.getName(), verb));
                            result.put(url, mapping);
                        } else {
                            VerbAction verbAction = new VerbAction(method.getName(), verb);
                            if (!Utils.isVerbExistInMapping(map, verbAction)) {
                                map.addVerbAction(verbAction);
                            } else {
                                throw new DuplicateException();
                            }
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

    public static void addSession(Object object, HttpServletRequest request) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == Session.class) {
                String parameterName = "set" + Utils.toUpperCase(field.getName());
                Session session = new Session(request.getSession());
                Object[] parameters = new Object[1];
                parameters[0] = session;
                object.getClass().getDeclaredMethod(parameterName, Session.class).invoke(object, session);
                break;
            }
        }
    }

    public static void executeMethodController(String url, VerbAction verbAction,HttpServletRequest request, HttpServletResponse response, String packageName, HashMap<String,Mapping> controllerAndMethod) throws Exception {
        PrintWriter out = response.getWriter();        
        Mapping map = ServletManager.getUrl(controllerAndMethod, url);

        String className = map.getClassName();
        String methodName = verbAction.getMethod();
        String classPath = packageName + "." + className;
        
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

    public static String [] getParameterName(Method method){
        Paranamer paranamer = new AdaptiveParanamer();
        String [] parameterName = paranamer.lookupParameterNames(method);
        return  parameterName;
    }

    public static Object prepareObject (String name, Object object, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Field[] attributs = object.getClass().getDeclaredFields();
        ValidationManager checker = new ValidationManager();
        boolean isErrorPresent = false;

        for (Field attribut : attributs){
            
            String attribut_name = attribut.getName();
            String method_name = "set" + Utils.toUpperCase(attribut_name);
            Method method = object.getClass().getDeclaredMethod(method_name, attribut.getType());
            String input_name = name + ":" + attribut_name;
            String value = request.getParameter(input_name); 

            if(value != null){
                String error = checker.isFieldValid(attribut, value);
                if (error == null) {
                    method.invoke(object, Utils.castValue(value, attribut.getType()));
                    String key = "value_" + attribut_name;
                    request.setAttribute(key, value);
                } else {
                    String key = "error_" + attribut_name;
                    request.setAttribute(key, error);
                    isErrorPresent = true;
                }
            }
        }

        if (isErrorPresent) {
            String page = checker.getPreviousPage(request);
            request.getRequestDispatcher("/"+page+"?previous_page="+page).forward(request, response);
        }
        return object;
    }

    public static void returnJson(Object obj, Method method, HttpServletResponse response) throws Exception {
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (method.getReturnType() == ModelView.class) {
            ModelView modelView = (ModelView) obj;
            for(Map.Entry<String,Object> data : modelView.getData().entrySet()){
                String jsonValue = gson.toJson(data.getValue());
                out.println(jsonValue);
            }
        } else {
            String jsonValue = gson.toJson(obj);
            out.println(jsonValue);
        }
    }

    
    public static List<Object> preparedParameter(Object obj, Method method, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<Object> result = new ArrayList<>();
        Parameter [] parameters = method.getParameters();
        String [] parameterName = getParameterName(method);

        for (int i = 0 ; i < parameters.length; i++){
            Annotation argumentAnnotation = parameters[i].getAnnotation(RequestParam.class);
            String argumentName = parameterName[i];
            if (argumentAnnotation != null){
                argumentName = ((RequestParam) argumentAnnotation).value();
            } 
            
            Class<?> clazz = parameters[i].getType();

            if(clazz == Session.class){
                Session session = new Session(request.getSession());
                result.add(session);
            }
            if(clazz == Part.class){
                result.add(request.getPart(argumentName));
            }
            if (Utils.isObject(clazz) && clazz != Session.class && clazz != Part.class){
                Object o = clazz.newInstance();
                result.add(prepareObject(argumentName, o, request, response));
            }
            if (!Utils.isObject(clazz)) {
                
                if(request.getParameter(argumentName)!=null){
                    result.add(Utils.castValue(request.getParameter(argumentName),parameters[i].getType()));
                
                }
            }
        }

        return result;
    }

    public static void executeMethod (String packageCtrl, Mapping map, VerbAction verbAction, HttpServletRequest request, HttpServletResponse response) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException, IOException , Exception {
        PrintWriter out = response.getWriter();
        
        Class<?> clazz = Class.forName(packageCtrl+"."+map.getClassName());
        Method method = Utils.getMethodAnnoted(clazz, verbAction.getMethod());
        Object object = clazz.newInstance();
        addSession(object, request);
        
        List<Object> methodParameters = new ArrayList<>();
        
        if (method.getParameters().length > 0) {
            methodParameters = preparedParameter(object, method,request, response);

            if (methodParameters.size() != method.getParameters().length) {
                throw new Exception("Parameters number is insufficient!");
            }
        }

        if (method.isAnnotationPresent(RestAPI.class)) {
            Object obj = method.invoke(object, methodParameters.toArray(new Object[]{}));
            returnJson(obj, method, response);

        } else {
            if (method.getReturnType() == String.class){
                out.println("Method return : "+ method.invoke(object, methodParameters.toArray(new Object[]{})).toString());
            }
            else if (method.getReturnType() == ModelView.class){
                dispatchModelView((ModelView) method.invoke(object, methodParameters.toArray(new Object[]{})), request, response);
            }
            else {
                throw new Exception("The return type of the method " + method.getName() + " in " + clazz.getName() + ".class is invalid!");
            }

        }
    }

}