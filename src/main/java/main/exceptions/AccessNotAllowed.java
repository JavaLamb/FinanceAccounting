package main.exceptions;

public class AccessNotAllowed extends RuntimeException {
    public AccessNotAllowed(String message) {
        super(message);
    }
}
