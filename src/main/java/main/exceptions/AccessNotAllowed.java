package main.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccessNotAllowed extends RuntimeException {
    public AccessNotAllowed(String message) {
        super(message);
    }
}
