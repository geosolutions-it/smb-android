package it.geosolutions.savemybike.model;

import java.util.List;

/**
 * Created by Lorenzo Pini on 19/07/2018.
 */
public class PaginatedResult<E> {

    List<E> results;
    int count;

    String next;
    String previous;

    public List<E> getResults() {
        return results;
    }

    public void setResults(List<E> results) {
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }
}
