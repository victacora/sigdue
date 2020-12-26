package com.sigdue.webservice.modelo;

import java.util.List;

public class WSGruparResult<T> {
    private List<T> items;
    private Next next;

    public WSGruparResult() {
        this.items = null;
    }

    public Next getNext() {
        return this.next;
    }

    public void setNext(Next next) {
        this.next = next;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
