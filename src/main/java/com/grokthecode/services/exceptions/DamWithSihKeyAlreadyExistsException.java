package com.grokthecode.services.exceptions;

public class DamWithSihKeyAlreadyExistsException extends Exception {

    public DamWithSihKeyAlreadyExistsException(final String sihKey) {
        super("A dam catalog entity with a sihKey " + sihKey + " already exists");
    }
}
