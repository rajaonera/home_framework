package mg.framework.exception;

public class UrlNotFoundException extends Exception{
    public UrlNotFoundException () {
        super("404 not found!");
    }
    public UrlNotFoundException (String message) {
        super(message);
    }
}
