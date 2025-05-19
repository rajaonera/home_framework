package mg.framework.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.framework.exception.VerbException;
import mg.framework.servlet.Mapping;
import mg.framework.manager.ServletManager;
import mg.framework.utils.Utils;
import mg.framework.utils.VerbAction;
import mg.framework.exception.Error;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

@MultipartConfig
public class FrontController extends HttpServlet{
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
        ServletManager manager = new ServletManager();
        try {
            String packageCtrl = this.getInitParameter("package_name");
            this.setClassController(manager.getControllerClasses(packageCtrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() throws ServletException{
        ServletManager manager = new ServletManager();
        this.initController();
        try {
            HashMap<String,Mapping> map = manager.getControllerMethod(this.getClassController());
            
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
            Mapping map = new Mapping().getUrl(this.getControllerAndMethod(), url);
            out.println("Controller Name : " + map.getClassName());
        } catch (Exception e) {
            out.println("Error : " + e.getMessage());
        }
    }

    public void processExecuteMethod(String url, String packageCtrl, HttpServletRequest request, HttpServletResponse response, String verb) throws IOException, ServletException, Exception{
        Mapping map = new Mapping().getUrl(this.getControllerAndMethod(), url);
        VerbAction verbAction = Utils.checkUrlMethod(map, verb);
        if (verbAction != null) {
            new ServletManager().executeMethod(packageCtrl, map, verbAction,request, response);
        } else {
            throw new VerbException();
        }
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response, String verb) throws IOException, ServletException{
        PrintWriter out = response.getWriter();
        String packageCtrl = this.getInitParameter("package_name");
        String url =  request.getRequestURI();
        try {
            this.processExecuteMethod(url, packageCtrl, request, response, verb);
        } catch (Exception e) {
            response.setContentType("text/html");
            out.println(Error.getError(e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.processRequest(request,response, "Get");
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
        this.processRequest(request,response, "Post");
    }
}