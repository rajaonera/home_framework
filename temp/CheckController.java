package mg.framework.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

import mg.framework.annotations.Controller;

public class CheckController {

    public static ArrayList<Class<?>> getClasses(String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        if (resource == null) {
            return classes;
        }
        File packageDirectory = new File(resource.getFile().replace("%20", " "));

        for (File file : Objects.requireNonNull(packageDirectory.listFiles())) {
            if (file.isDirectory()) {
                classes.addAll(CheckController.getClasses(packageName + "." + file.getName()));
            } else {
                String className = packageName + "." + Utils.getFileName(file.getName(), "class");
                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    public static ArrayList<Class<?>> getControllerClasses(String packageName) throws ClassNotFoundException, IOException {
        ArrayList<Class<?>> classes = getClasses(packageName);
        return getClasses(classes);
    }

    static ArrayList<Class<?>> getClasses(ArrayList<Class<?>> classes) {
        ArrayList<Class<?>> result = new ArrayList<>();

        for(Class<?> classe : classes) {
            if (classe.isAnnotationPresent(Controller.class)) {
                result.add(classe);
            }
        }

        return result;
    }

}