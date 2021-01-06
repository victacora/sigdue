package com.sigdue.webservice.modelo;

public class Link {
    private String rel;
    private String href;
    private String $ref;

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String get$ref() {
        return $ref;
    }

    public void set$ref(String $ref) {
        this.$ref = $ref;
    }
}
