package com.chuxin.springboot.web.controller;

import com.chuxin.springboot.component.EsComponent;
import com.chuxin.springboot.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



/**
 * Created by chenchx on 2018/11/6.
 */
@RestController
public class TestController {
    public final static String BOOK_INDEX = "books";
    public final static String BOOK_TYPE = "doc";
    @Autowired
    private EsComponent esComponent;
    @RequestMapping(value = "/addBook",method = RequestMethod.POST)
    public void addBook(@RequestBody BookDto book){
        try {
            esComponent.insertOrUpdate(BOOK_INDEX,BOOK_TYPE,book,book.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @RequestMapping(value = "/searchBook",method = RequestMethod.GET)
    public PageDto<BookDto> searchBook(@RequestParam String bookName){
        PageRequestBuilder pageRequestBuilder = PageRequestBuilder.builder()
                .filterBuilder( FilterBuilder.builder().match(new EsParam("name",bookName)))
                .orderBuilder(OrderBuilder.builder().fieldOrder("id",OrderBuilder.Direction.ASC));
        return esComponent.searchPage(BOOK_INDEX,BOOK_TYPE,pageRequestBuilder,BookDto.class);
    }
}
