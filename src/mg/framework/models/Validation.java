package mg.framework.models;


import jakarta.servlet.http.HttpServletRequest;
import mg.framework.utils.ValidationManager;

public class Validation {
    public static String error (String field_name, HttpServletRequest request){
        String error = (String) request.getAttribute("error_"+field_name);
        if (error == null) { 
            error = "";
        }
        return new ValidationManager().setPreviousPage(error, request);
    }

    public static String value (String field_name, HttpServletRequest request){
        String value = (String) request.getAttribute("value_"+field_name);
        if (value == null) {
            value = "";
        }
        return value;
    }
    
}
