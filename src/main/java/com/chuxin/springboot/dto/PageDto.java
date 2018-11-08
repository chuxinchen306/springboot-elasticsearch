package com.chuxin.springboot.dto;

import java.util.List;

/**
 * Created by chenchx on 2018/11/6.
 */
public class PageDto<T> {
    private List<T> rows;

    private Integer total = 1;

    private Boolean firstPage;

    private Boolean lastPage;

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Boolean getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(Boolean firstPage) {
        this.firstPage = firstPage;
    }

    public Boolean getLastPage() {
        return lastPage;
    }

    public void setLastPage(Boolean lastPage) {
        this.lastPage = lastPage;
    }
}
