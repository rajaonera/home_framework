package mg.framework.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import mg.framework.utils.CheckController;

public class FrontController  extends HttpServlet{
    
    // Sprint 01
    private ArrayList<Class<?>> classController;
    
    public void init() throws ServletException{
        try {
            String packageCtrl = this.getInitParameter("packageName");
            this.setClassController(CheckController.getControllerClasses(packageCtrl));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Class<?>> getClassController() {
        return classController;
    }

    public void setClassController(ArrayList<Class<?>> classController) {
        this.classController = classController;
    }


    // Sprint 00
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
        out.println("URL : " + request.getRequestURI());
    
    // Sprint01
        ArrayList<Class<?>> classes = new ArrayList<>();
        if (classes != null) {
            classes = this.getClassController();

            out.println("Controllers scannes : ");
            for (Class<?> classe : classes) {
                out.println("- " + classe.getSimpleName());
            }
        }
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
}