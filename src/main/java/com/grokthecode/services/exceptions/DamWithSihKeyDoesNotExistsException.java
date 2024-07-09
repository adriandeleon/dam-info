package com.grokthecode.services.exceptions;

public class DamWithSihKeyDoesNotExistsException extends Exception {

    public DamWithSihKeyDoesNotExistsException(final String sihKey) {
        super("A dam catalog entity with a sihKey " + sihKey + " does not exists.");
    }
}
