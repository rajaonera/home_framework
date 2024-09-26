package mg.framework.exception;


public class CastException extends Exception{
    public CastException(String message) {
        super(message);
    }
    public CastException() {
        super("Can't cast object!");
    }
}
