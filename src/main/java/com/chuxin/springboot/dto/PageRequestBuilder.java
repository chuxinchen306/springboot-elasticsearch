package com.chuxin.springboot.dto;

/**
 * Created by chenchx on 2018/11/5.
 */
public class PageRequestBuilder {
    private int page = 1;
    private int rows = 10;

    private Double lat;
    private Double lon;
    private FilterBuilder filterBuilder = null;
    private OrderBuilder orderBuilder = null;

    public static PageRequestBuilder builder(){
        return new PageRequestBuilder();
    }

    /**
     * 过滤条件
     * */
    public PageRequestBuilder filterBuilder(FilterBuilder filterBuilder){
        this.filterBuilder = filterBuilder;
        return this;
    }
    /**
     * 排序条件
     * */
    public PageRequestBuilder orderBuilder(OrderBuilder orderBuilder){
        this.orderBuilder = orderBuilder;
        return this;
    }

    /**
     * 页号  默认第一页=》1
     * */
    public PageRequestBuilder page(int page){
        this.page = page;
        return this;
    }


    /**
     * 页大小  默认10
     * */
    public PageRequestBuilder rows(int rows){
        this.rows = rows;
        return this;
    }

    /**
     * 经纬度
     * @param lat 纬度
     * @param lon 经度
     * */
    public PageRequestBuilder location(Double lat, Double lon){
        this.lat = lat;
        this.lon = lon;
        return this;
    }

    public int getPage() {
        return page;
    }

    public int getRows() {
        return rows;
    }

    public FilterBuilder getFilterBuilder() {
        return filterBuilder;
    }

    public OrderBuilder getOrderBuilder() {
        return orderBuilder;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setFilterBuilder(FilterBuilder filterBuilder) {
        this.filterBuilder = filterBuilder;
    }

    public void setOrderBuilder(OrderBuilder orderBuilder) {
        this.orderBuilder = orderBuilder;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}
