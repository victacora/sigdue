package com.sigdue.service;

public class ResultadoServicio<T> {
    private Exception error;
    private T result;

    public T getResult() {
        return this.result;
    }

    public Exception getError() {
        return this.error;
    }

    public ResultadoServicio(T result) {
        this.result = result;
    }

    public ResultadoServicio(Exception error) {
        this.error = error;
    }
}
