package mg.framework.utils;

import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletRequest;
import mg.framework.annotations.Date;
import mg.framework.annotations.Mail;
import mg.framework.annotations.Numeric;
import mg.framework.annotations.Required;

public class ValidationManager {

    public boolean isValid (Field field, String value) throws Exception {
        boolean result = true;

        if (field.isAnnotationPresent(Numeric.class)) {
            if (!Utils.isNumeric(value)){
                result = false;
                throw new Exception("The value of the input "+ field.getName() +" must be numeric!");
            }
        }

        if (field.isAnnotationPresent(Date.class)){
            if (!Utils.isDate(value)) {
                result = false;
                throw new Exception("The value of the input "+ field.getName() +" must be of type Date!");
            }
        }

        if (field.isAnnotationPresent(Mail.class)){
            if (!value.contains("@")){
                result = false;
                throw new Exception("The value of the input "+ field.getName() +" must be of type Mail!");
            }
        }

        if (field.isAnnotationPresent(Required.class)){
            if (value == null || value.equals("")) {
                result = false;
                throw new Exception("The input "+ field.getName() +" cannot be empty!");
            }
        }

        return result;
    }

    public String isFieldValid (Field field, String value) throws Exception {
        String error = null;
        if (field.isAnnotationPresent(Numeric.class)) {
            if (!Utils.isNumeric(value)){
                error = "The value of the input "+ field.getName() +" must be numeric!";
            }
        }

        if (field.isAnnotationPresent(Date.class)){
            if (!Utils.isDate(value)) {
                error = "The value of the input "+ field.getName() +" must be of type Date!";
            }
        }

        if (field.isAnnotationPresent(Mail.class)){
            if (!value.contains("@")){
                error = "The value of the input "+ field.getName() +" must be of type Mail!";
            }
        }

        if (field.isAnnotationPresent(Required.class)){
            if (value == null || value.equals("")) {
                error = "The input "+ field.getName() +" cannot be empty!";
            }
        }

        return error;
    }

    public String getPreviousPage(HttpServletRequest request) {
        String page = request.getParameter("previous_page");
        if (page == null) {
            page = request.getHeader("Referer").substring(request.getHeader("Referer").lastIndexOf("/") + 1);
        }
        return page;
    }

    public String setPreviousPage(String error, HttpServletRequest request){
        String page = request.getParameter("previous_page");
        String result = "";
        if (page != null) {
            result = "<input type='hidden' name='previous_page' value='" + page + "' >";
        } 
        return error + " " + result;
    }
}
