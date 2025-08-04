package mg.framework.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class FrontController  extends HttpServlet{
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        PrintWriter out = response.getWriter();
        out.println("URL : " + request.getRequestURI());
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        this.processRequest(request,response);
    }
}