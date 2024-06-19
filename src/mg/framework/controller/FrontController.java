package mg.framework.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.framework.models.Mapping;
import mg.framework.utils.ServletManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class FrontController  extends HttpServlet{
    private ArrayList<Class<?>> classController = new ArrayList<>();
    private HashMap<String,Mapping> controllerAndMethod = new HashMap<>(); 

    public ArrayList<Class<?>> getClassController() {
        return classController;
    }
    public void setClassController(ArrayList<Class<?>> classController) {
        this.classController = classController;
    }
    public HashMap<String, Mapping> getControllerAndMethod() {
        return controllerAndMethod;
    }
    public void setControllerAndMethod(HashMap<String, Mapping> controllerAndMethod) {
        this.controllerAndMethod = controllerAndMethod;
    }
    public void addControllerAndMethod(String url, Mapping method) {
        this.controllerAndMethod.put(url, method);
    }

    public void initController() {
        try {
            String packageCtrl = this.getInitParameter("packageName");
            this.setClassController(ServletManager.getControllerClasses(packageCtrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws ServletException{
        this.initController();
        try {
            HashMap<String,Mapping> map = ServletManager.getControllerMethod(this.getClassController());
            
            if (map != null) {
                this.setControllerAndMethod(map);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void showController(PrintWriter out) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        if (classes != null && this.getClassController() != null && !this.getClassController().isEmpty()) {
            classes = this.getClassController();

            out.println("Controllers : ");
            for (Class<?> classe : classes) {
                out.println("- " + classe.getSimpleName());
            }
        }
    }

    public void showControllerAndMethod(PrintWriter out, String url) throws Exception{
        try {
            Mapping map = ServletManager.getUrl(this.getControllerAndMethod(), url);
            out.println("Controller Name : " + map.getClassName());
            out.println("Method Name : " + map.getMethodName());
        } catch (Exception e) {
            out.println("Error : " + e.getMessage());
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        PrintWriter out = response.getWriter();
        String packageCtrl = this.getInitParameter("packageName");
        String url =  request.getRequestURI();
        out.println("URL : " + url);

        try {
            ServletManager.executeMethodController(url, request, response, packageCtrl, this.getControllerAndMethod());
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