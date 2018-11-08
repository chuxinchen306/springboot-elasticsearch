package com.chuxin.springboot;

import com.chuxin.springboot.component.EsComponent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by chenchx on 2018/11/5.
 */
@SpringBootApplication
public class Application {
    @Value("${es.host}")
    private String esHost;
    @Value("${es.port:9200}")
    private Integer esPort;
    @Value("${es.scheme:http}")
    private String esScheme;
    public static void main(String[] args){
        SpringApplication.run(Application.class,args);
    }

    @Bean
    public EsComponent esComponent(){
        return new EsComponent(esHost,esPort,esScheme);
    }
}
