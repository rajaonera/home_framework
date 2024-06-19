package mg.framework.exception;

public class ReturnException extends Exception{
    public ReturnException(String methodName, String className) {
        super("The return type of method "+ methodName + " in " + className + ".class is invalid!" );
    }
    public ReturnException(String message) {
        super(message);
    }
}
