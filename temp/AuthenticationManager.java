package mg.framework.utils;

import java.lang.annotation.Annotation;

import jakarta.servlet.http.HttpServletRequest;
import mg.framework.annotations.Authentication;

public class AuthenticationManager {
    public boolean isHabilitate(Annotation annotation, HttpServletRequest request) {
        if (annotation != null) {
            int auth_level = ((Authentication) annotation).level();
            int client_level = 0;
            if (request.getSession().getAttribute("authentication_level") != null) {
                client_level = (int) request.getSession().getAttribute("authentication_level");
            }
            
            if (auth_level > client_level) {
                return false;
            }
        }
        return true;
    }
}
