package mg.framework.exception;

public class DuplicateException extends Exception{
    public DuplicateException() {
        super("Duplicate annotation in multiple methods!");
    }
    public DuplicateException(String message) {
        super(message);
    }
}
