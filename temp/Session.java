package mg.framework.models;

import jakarta.servlet.http.HttpSession;

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
}
