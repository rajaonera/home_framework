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
    }

    public HashMap<String, Mapping> setMappingUrls() {
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
            HashMap<String,Mapping> map = ServletManager.getControllerMethod(this.getClassController(),this.setMappingUrls());
            if (map == null) {
                throw new Exception("Duplicate annotation in multiple methods!");
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    public void showMappingsUrlsAndMethods(PrintWriter out, String url) {
        Mapping map = ServletManager.getUrl(this.setMappingUrls(), url);
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
}