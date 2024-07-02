package com.grokthecode.data.responses;

import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.sql.Timestamp;

public record HttpErrorResponse(String timestamp, int httpStatus, String error, String message, String path) implements Serializable {
}
