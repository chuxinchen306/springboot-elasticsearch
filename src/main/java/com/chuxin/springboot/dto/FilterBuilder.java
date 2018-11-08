package com.chuxin.springboot.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchx on 2018/11/5.
 */
public class FilterBuilder {

    /**
     *精确值 &&
     * */
    private List<EsParam> mustList = new ArrayList<EsParam>();

    /**
     * 分词检索 ||
     * */
    private List<EsParam> matchList = new ArrayList<EsParam>();

    public List<EsParam> getMustList() {
        return mustList;
    }

    public List<EsParam> getMatchList() {
        return matchList;
    }

    private FilterBuilder() {
    }

    public static FilterBuilder builder(){
        return new FilterBuilder();
    }

    /**
     * 精确检索
     * */
    public FilterBuilder must(EsParam esParam){
        mustList.add(esParam);
        return this;
    }

    /**
     * 分词检索
     * */
    public FilterBuilder match(EsParam esParam){
        matchList.add(esParam);
        return this;
    }
}
