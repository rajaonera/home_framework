package mg.framework.servlet;

import java.lang.reflect.Field;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.framework.utils.Utils;

public class Session {
    HttpSession session;

    public Session() {}
    public Session(HttpSession session) {
        this.session = session;
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }

    public Object get(String key) {
        return session.getAttribute(key);
    }

    public  void add(String key, Object object) {
        session.setAttribute(key, object);
    }

    public void delete(String key) {
        session.removeAttribute(key);
    }

    public void deleteAll() {
        session.invalidate();
    }

    public void addSession(Object object, HttpServletRequest request) throws Exception {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            if (field.getType() == Session.class) {
                String parameterName = "set" + Utils.toUpperCase(field.getName());
                Session session = new Session(request.getSession());
                Object[] parameters = new Object[1];
                parameters[0] = session;
                object.getClass().getDeclaredMethod(parameterName, Session.class).invoke(object, session);
                break;
            }
        }
    }
}
