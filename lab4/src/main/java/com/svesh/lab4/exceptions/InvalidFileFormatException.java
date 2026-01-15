package com.svesh.lab4.exceptions;

import java.io.IOException;

public class InvalidFileFormatException extends IOException {
    public InvalidFileFormatException(String message) {
        super(message);
    }

    public InvalidFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
