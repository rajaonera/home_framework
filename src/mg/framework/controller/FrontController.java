package mg.framework.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mg.framework.models.Mapping;
import mg.framework.models.ModelView;
import mg.framework.utils.ServletManager;
import mg.framework.utils.Utils;

public class FrontController  extends HttpServlet{
    
    // Sprint 01
    private ArrayList<Class<?>> classController;
    HashMap<String, Mapping> mappingUrls = new HashMap<>();

    public ArrayList<Class<?>> getClassController() {
        return classController;
    }

    public void setClassController(ArrayList<Class<?>> classController) {
        this.classController = classController;
    }


    // Sprint 00
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter out = response.getWriter();
        String url =  request.getRequestURI();
        out.println("URL : " + url);
        this.showMappingsUrlsAndMethods(out,url);
        this.executeMethodController(url, request, response);

    }

    public HashMap<String, Mapping> getMappingUrls() {
        return mappingUrls;
    }
    public void getMappingUrls(HashMap<String, Mapping> mappingUrl) {
        this.mappingUrls = mappingUrl;
    }

    public void setMappingUrls(String url, Mapping method) {
        this.mappingUrls.put(url, method);
    }

    public void initController() {
        try {
            String packageCtrl = this.getInitParameter("smooth.mg.controller");
            this.setClassController(ServletManager.getControllerClasses(packageCtrl));
        } catch (Exception e) {
            System.out.printf(e.getLocalizedMessage());
        }
    }

    public void init() {
        this.initController();
        try {
            ServletManager.getControllerMethod(this.getClassController(),this.getMappingUrls());
                
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void showMappingsUrlsAndMethods(PrintWriter out, String url) {
        Mapping map = ServletManager.getUrl(this.getMappingUrls(), url);
        try {
            if (map != null) {
                out.println("Controller Name : " + map.getClassName());
                out.println("Method Name : " + map.getMethodName());
            } else {
                out.println("No method found!");
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    public void dispatchModelView(ModelView modelView, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Map.Entry<String,Object> data : modelView.getData().entrySet()){
            String varName = data.getKey();
            Object varValue = data.getValue();
            request.setAttribute(varName,varValue);
        }
        RequestDispatcher dispatcher = request.getServletContext().getRequestDispatcher("/"+modelView.getUrl());
        dispatcher.forward(request,response);
    }

    public void executeMethodController(String url, HttpServletRequest request, HttpServletResponse response) throws IOException , ServletException {
        PrintWriter out = response.getWriter();
        
        String packageCtrl = this.getInitParameter("packageName");
        Mapping map = ServletManager.getUrl(this.getMappingUrls(), url);
        try {
            if (map != null) {
                String className = map.getClassName();
                String methodName = map.getMethodName();
                String classPath = packageCtrl+"."+className;

                Class<?> controllerClass = Class.forName(classPath);
                Method controllerMethod = Utils.getMethod(controllerClass, methodName);

                if (controllerMethod.getReturnType() == String.class || controllerMethod.getReturnType() == ModelView.class) {
                    Object ctrlObj = controllerClass.newInstance(); 
                    out.println("Return: "+ controllerMethod.getReturnType());
                    if (controllerMethod.getReturnType() == String.class) {
                        String methodReturn =  (String) Utils.executeSimpleMethod(ctrlObj, methodName);
                    
                        out.print("After executing the "+ methodName +" method in the "+ className +".class, this method returned the value: ");
                        out.println(methodReturn);
                    } 
                    if (controllerMethod.getReturnType() == ModelView.class) {
                        ModelView modelView = (ModelView) Utils.executeSimpleMethod(ctrlObj, methodName);
                        this.dispatchModelView(modelView, request, response);
                    }
                } else {
                    out.println("The return type of method "+ methodName + " in " + className + ".class is invalid!" );
                }

            } else {
                out.println("No method found!");
            }
        } catch (Exception e) {
            out.println("Error : " + e.getMessage());
        }
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.processRequest(request,response);
    }
}