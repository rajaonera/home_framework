package mg.framework.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mg.framework.exception.UrlNotFoundException;
import mg.framework.utils.VerbAction;

public class Mapping {
    String className;
    List<VerbAction> verbAction = new ArrayList<VerbAction>();

    public Mapping(){}
    public Mapping(String className) {
        this.className = className;
    }
    public Mapping(String className, List<VerbAction> verbAction) {
        this.className = className;
        this.verbAction = verbAction;
    }

    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public List<VerbAction> getVerbAction() {
        return verbAction;
    }
    public void setVerbAction(List<VerbAction> verbAction) {
        this.verbAction = verbAction;
    }

    public void addVerbAction(VerbAction verbAction) {
        this.verbAction.add(verbAction);
    }
    public Mapping getUrl(HashMap<String, Mapping> maps, String url) throws Exception {
        Mapping result = null;
        String[] path = url.split("/");
        String newUrl = new String();
        int lenght = path.length-1;
        for (int i = lenght; i >= 0; i--) {
            if (i < lenght) {
                newUrl = "/" + newUrl;
            }
            newUrl = path[i] + newUrl;
            Mapping map = maps.get(newUrl);
            
            if (map != null) {
                result = map;
            }
        } 
        if (result == null) {
            throw new UrlNotFoundException();
        }
        return result;
    }
}