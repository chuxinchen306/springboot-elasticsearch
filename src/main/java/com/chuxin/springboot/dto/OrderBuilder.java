package com.chuxin.springboot.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchx on 2018/11/5.
 */
public class OrderBuilder {
    private String locationField;
    private List<SortField> fieldOrderList = new ArrayList<>();
    public String getLocationField() {
        return locationField;
    }

    public void setLocationField(String locationField) {
        this.locationField = locationField;
    }

    public List<SortField> getFieldOrderList() {
        return fieldOrderList;
    }

    private OrderBuilder() {
    }

    public static OrderBuilder builder(){
        return new OrderBuilder();
    }

    /**
     * 精确检索
     * @param fieldName 字段名称 location
     * */
    public OrderBuilder locationOrder(String fieldName){
        this.locationField = fieldName;
        return this;
    }

    /**
     * 分词检索
     * */
    public OrderBuilder fieldOrder(String fieldName, Direction direction){
        fieldOrderList.add(new SortField(fieldName, direction));
        return this;
    }
    public class SortField{
        private String fieldName;
        private Direction direction;
        public SortField() {
        }

        public SortField(String fieldName) {
            this.fieldName = fieldName;
        }

        public SortField(String fieldName, Direction direction) {
            this.fieldName = fieldName;
            this.direction = direction;
        }
        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public Direction getDirection() {
            return direction;
        }

        public void setDirection(Direction direction) {
            this.direction = direction;
        }
    }

    public enum Direction{
        ASC("asc"),
        DESC("desc");
        private String value;
        Direction(String value){
            this.value = value;
        }
        public String getValue(){
            return value;
        }
    }
}
