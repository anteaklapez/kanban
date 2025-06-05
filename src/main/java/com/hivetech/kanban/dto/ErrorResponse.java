package com.hivetech.kanban.dto;

public class ErrorResponse {
    private String error;
    private String message;
    private String uri;

    public ErrorResponse(String error, String message, String uri) {
        this.error = error;
        this.message = message;
        this.uri = uri;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
