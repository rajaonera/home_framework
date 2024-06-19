package mg.framework.exception;


public class PackageException extends Exception{
    public PackageException(String packageName) {
        super("The package " + packageName + " is empty or doesn't exist!");
    }
}
