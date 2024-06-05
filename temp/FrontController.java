package mg.framework.controller;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import mg.framework.models.Mapping;
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
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
        String url =  request.getRequestURI();
        out.println("URL : " + url);
        this.showMappingsUrlsAndMethods(out,url);
        this.executeMethodController(out, url);
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

    public void executeMethodController(PrintWriter out, String url) throws IOException {
        String packageCtrl = this.getInitParameter("packageName");
        Mapping map = ServletManager.getUrl(this.getMappingUrls(), url);
        try {
            if (map != null) {
                String className = map.getClassName();
                String methodName = map.getMethodName();
                String classPath = packageCtrl+"."+className;

                Class<?> controllerClass = Class.forName(classPath);
                Object ctrlObj = controllerClass.newInstance(); 
                String methodReturn =  (String) Utils.executeSimpleMethod(ctrlObj, methodName);
                
                out.print("After executing the "+ methodName +" method in the "+ className +" Class, this method returned the value: ");
                out.println(methodReturn); 

            } else {
                out.println("No method found!");
            }
        } catch (Exception e) {
            out.println("Error : " + e.getMessage());
        }
    } 

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
}