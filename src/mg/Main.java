package mg;

import mg.framework.utils.CheckController;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try {
            ArrayList<Class<?>> controllers = CheckController.getControllerClasses("mg.tests.controller");
            for (Class<?> ctrl : controllers) {
                System.out.println("✅ Controller trouvé : " + ctrl.getName());
            }
        } catch (Exception e) {
           System.out.println(e.getMessage());
        }
    }
}