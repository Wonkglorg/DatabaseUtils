package com.wonkglorg.ip;

@SuppressWarnings("unused")
public class ValidationResult<T extends IP<?>> {
    private final T ip;
    private final MalformedIpException exception;

    public ValidationResult(T ip, MalformedIpException exception) {
        this.ip = ip;
        this.exception = exception;
    }

    public ValidationResult(T ip, String exceptionMessage) {
        this.ip = ip;
        this.exception = new MalformedIpException(exceptionMessage);
    }

    public ValidationResult(T ip) {
        this.ip = ip;
        this.exception = null;
    }

    public ValidationResult(String exceptionMessage) {
        this.ip = null;
        this.exception = new MalformedIpException(exceptionMessage);
    }

    public ValidationResult(MalformedIpException exception) {
        this.ip = null;
        this.exception = exception;
    }

    public T getIp() {
        return ip;
    }

    public MalformedIpException getException() {
        return exception;
    }

    public boolean isValid() {
        return ip != null && exception == null;
    }

    public boolean hasException() {
        return exception != null;
    }

    public boolean hasIp() {
        return ip != null;
    }
}
