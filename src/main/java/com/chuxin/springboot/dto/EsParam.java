package com.chuxin.springboot.dto;

/**
 * Created by chenchx on 2018/11/5.
 */
public class EsParam {
    private String fieldName;
    private Object fieldValue;
    private Condition condition;
    public EsParam() {
    }

    public EsParam(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.condition = Condition.EQUALS;
    }

    public EsParam(String fieldName, Object fieldValue, Condition condition) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.condition = condition;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setFieldValue(Object fieldValue) {
        this.fieldValue = fieldValue;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }

    public Condition getCondition() {
        return condition;
    }

    public enum  Condition{
        EQUALS,
        LT,
        LTE,
        GT,
        GTE,
        NOT_EQUALS,
        /**
         * 距离 多少公里范围内
         * */
        DISTINCE;
    }
}
