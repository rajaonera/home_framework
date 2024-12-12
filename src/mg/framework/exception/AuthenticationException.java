package mg.framework.exception;

public class AuthenticationException extends Exception{
    public AuthenticationException(String message) {
        super("You are not authorized to access " + message);
    }
}
