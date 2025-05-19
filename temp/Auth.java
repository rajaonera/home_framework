package mg.framework.servlet;


import jakarta.servlet.http.HttpServletRequest;

public class Auth {
    public Auth() {}
    public Auth(HttpServletRequest request, int level) {
        this.setLevel(request, level);
    }

    private void setLevel(HttpServletRequest request, int level) {
        request.getSession().setAttribute("authentication_level", level);
    }
}