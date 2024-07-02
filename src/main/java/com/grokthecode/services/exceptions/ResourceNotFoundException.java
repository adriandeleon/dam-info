package com.grokthecode.services.exceptions;

public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(final String resourceName) {
        super("Resource not found: " + resourceName);
    }
}
