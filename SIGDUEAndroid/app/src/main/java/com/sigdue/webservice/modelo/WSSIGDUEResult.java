package com.sigdue.webservice.modelo;

import com.sigdue.db.Parametro;

import java.util.List;

public class WSSIGDUEResult {
    private List<Parametro> items;
    private boolean hasMore;
    private long limit;
    private long offset;
    private long count;
    private List<Link> links;
    private Link first;

    public List<Parametro> getItems() {
        return items;
    }

    public void setItems(List<Parametro> items) {
        this.items = items;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public Link getFirst() {
        return first;
    }

    public void setFirst(Link first) {
        this.first = first;
    }
}
