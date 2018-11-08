package com.chuxin.springboot.component;

import com.alibaba.fastjson.JSONArray;
import com.chuxin.springboot.dto.*;
import com.chuxin.springboot.util.BeanMapUtil;
import com.chuxin.springboot.util.MapConvert;
import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenchx on 2018/11/5.
 */
public class EsComponent {
    private String esHost;
    private Integer esPort;
    private String esScheme;
    private RestHighLevelClient client = null;
    private final static Logger Log = LoggerFactory.getLogger(EsComponent.class);
    private final static String DISTINCE_FIELD_NAME= "distince";
    public EsComponent(String esHost,Integer esPort, String esScheme){
        this.esHost = esHost;
        this.esPort = esPort;
        this.esScheme = esScheme;

        if (client == null){
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(esHost,esPort,esScheme)));

        }
    }

    public void add(String index, String type, Object obj, Long id){
        try {
            IndexRequest indexRequest = Requests.indexRequest(index).type(type).id(id.toString()).create(true);
            Map<String,Object> map = BeanMapUtil.objectToMap(obj);
            indexRequest.source(map);
            try{
                client.index(indexRequest);
            }catch (ElasticsearchStatusException e){
                if(e.status().getStatus()==409){
                    //已存在
                    Log.error("数据已存在");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void insertOrUpdate(String index, String type, Object obj, Long id) throws Exception {
        try {
            IndexRequest indexRequest = Requests.indexRequest(index).type(type).id(id.toString());
            Map<String,Object> map = BeanMapUtil.objectToMap(obj);
            indexRequest.source(map);
            client.index(indexRequest);
        }catch (Exception e){
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public void update(String index, String type, Object obj, Long id){
        try{
            GetRequest getRequest = new GetRequest(index, type, id.toString())
                    .fetchSourceContext(FetchSourceContext.DO_NOT_FETCH_SOURCE);
            GetResponse getResponse = client.get(getRequest);
            if (!getResponse.isExists()){
                Log.error("数据不存在");
            }
            UpdateRequest updateRequest = new UpdateRequest().index(index).type(type).id(id.toString());
            Map<String,Object> map = BeanMapUtil.objectToMap(obj);;
            updateRequest.doc(map);
            client.update(updateRequest);
        }catch (Exception e){
            e.printStackTrace();
            Log.error("未知异常");
        }
    }

    public <T> T getById(String index, String type, Long id, Class<T> tClass) throws Exception {
        try{
            GetRequest getRequest = new GetRequest(index,type,id.toString())
                    .fetchSourceContext(FetchSourceContext.FETCH_SOURCE);
            GetResponse getResponse = client.get(getRequest);
            if (!getResponse.isExists()){
                Log.error("数据不存在");
                throw new Exception("数据不存在");
            }else{
                Map<String,Object> map = getResponse.getSource();
                T t = MapConvert.mapToObject(map,tClass);
                return t;
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.error("未知异常");
            throw new Exception("未知异常");
        }
    }

    public void deleteById(String index, String type, Long id){
        try{
            DeleteRequest deleteRequest = new DeleteRequest(index,type,id.toString());
            DeleteResponse response = client.delete(deleteRequest);
            DocWriteResponse.Result result = response.getResult();
            if (DocWriteResponse.Result.NOT_FOUND == result){
                Log.error("数据不存在");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public <T> PageDto<T> searchPage(String index, String type, PageRequestBuilder pageRequestBuilder, Class<T> tClass){
        PageDto<T> pageDto = new PageDto<>();
        try{
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            filterParam(searchSourceBuilder,pageRequestBuilder.getFilterBuilder(),pageRequestBuilder.getLat(),pageRequestBuilder.getLon());
            orderParam(searchSourceBuilder,pageRequestBuilder.getOrderBuilder(),pageRequestBuilder.getLat(),pageRequestBuilder.getLon());
            int size = pageRequestBuilder.getRows();
            int from = (pageRequestBuilder.getPage() -1) <= 0 ? 0 : (pageRequestBuilder.getPage() - 1)*size;
            searchSourceBuilder.from(from);
            searchSourceBuilder.size(size);

            SearchRequest searchRequest = new SearchRequest(index);
            searchRequest.types(type);
            searchRequest.source(searchSourceBuilder);
            SearchResponse searchResponse = client.search(searchRequest);
            if (searchResponse.status().getStatus() == RestStatus.OK.getStatus()){
                Long total = searchResponse.getHits().getTotalHits();
                pageDto.setTotal(total==null?0:total.intValue());
                List<T> list = new ArrayList<>();
                SearchHit[] searchHitsHits = searchResponse.getHits().getHits();
                for (SearchHit searchHit : searchHitsHits){
                    Map<String,Object> map = searchHit.getSourceAsMap();
                    Object[] sortList = searchHit.getSortValues();
                    if (sortList != null && sortList.length>0){
                        for (int i = 0; i<sortList.length; i++){
                            if (sortList[i].getClass() == Double.class){
                                double dis = (double)sortList[i];
                                map.put(DISTINCE_FIELD_NAME,dis);
                            }
                        }
                    }
                    T t = MapConvert.mapToObject(map,tClass);
                    if (t != null){
                        list.add(t);
                    }
                }
                int totalPage = size == 0 ? 0 : (int) Math.ceil((double) pageDto.getTotal() / (double) size);
                boolean isLast = pageRequestBuilder.getPage() >= totalPage;
                boolean isFirst = pageRequestBuilder.getPage() <= 1;
                pageDto.setLastPage(isLast);
                pageDto.setFirstPage(isFirst);
                pageDto.setRows(list);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return pageDto;
    }

    private void orderParam(SearchSourceBuilder searchSourceBuilder, OrderBuilder sortBuilder, Double lat, Double lon) {
        if (searchSourceBuilder == null){
            return;
        }
        List<OrderBuilder.SortField> fieldOrderList = sortBuilder.getFieldOrderList();
        if (fieldOrderList != null && fieldOrderList.size()>0){
            for (OrderBuilder.SortField  sort: fieldOrderList){
                searchSourceBuilder.sort(SortBuilders.fieldSort(sort.getFieldName()).order(SortOrder.fromString(sort.getDirection().getValue())));
            }
        }
        if (lat != null && lon!= null && !StringUtils.isEmpty(sortBuilder.getLocationField())){
            searchSourceBuilder.sort(SortBuilders.geoDistanceSort(sortBuilder.getLocationField(),lat,lon).order(SortOrder.ASC));
        }
    }

    private void filterParam(SearchSourceBuilder searchSourceBuilder, FilterBuilder filterBuilder, Double lat, Double lon) {
        if (filterBuilder == null){
            return;
        }
        List<EsParam> matchList = filterBuilder.getMatchList();
        List<EsParam> mustList = filterBuilder.getMustList();
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (mustList != null && mustList.size()>0){
            for (EsParam param : mustList){
                if (param.getCondition() == EsParam.Condition.EQUALS){
                    if ("otherTag".equals(param.getFieldName())){
                        BoolQueryBuilder otherBuilder = QueryBuilders.boolQuery();
                        otherBuilder.should(QueryBuilders.termQuery("otherTag",param.getFieldValue()));
                        otherBuilder.should(QueryBuilders.termQuery("cornerTag",param.getFieldValue()));
                        boolQueryBuilder.must(otherBuilder);
                    }else {
                        if (param.getFieldValue().getClass() == JSONArray.class){
                            JSONArray p = (JSONArray) param.getFieldValue();
                            BoolQueryBuilder otherBuilder = QueryBuilders.boolQuery();
                            for (int i = 0; i<p.size(); i++){
                                otherBuilder.should(QueryBuilders.termQuery(param.getFieldName(),p.get(i)));
                            }
                            boolQueryBuilder.must(otherBuilder);
                        }else{
                            boolQueryBuilder.must(QueryBuilders.termQuery(param.getFieldName(),param.getFieldValue()));
                        }
                    }
                }else if(param.getCondition() == EsParam.Condition.NOT_EQUALS){
                    boolQueryBuilder.mustNot(QueryBuilders.termQuery(param.getFieldName(), param.getFieldValue()));
                }else if(param.getCondition() == EsParam.Condition.LT){
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(param.getFieldName()).lt(param.getFieldValue()));
                }else if(param.getCondition() == EsParam.Condition.LTE){
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(param.getFieldName()).lte(param.getFieldValue()));
                }else if(param.getCondition() == EsParam.Condition.GT){
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(param.getFieldName()).gt(param.getFieldValue()));
                }else if(param.getCondition() == EsParam.Condition.GTE){
                    boolQueryBuilder.must(QueryBuilders.rangeQuery(param.getFieldName()).gte(param.getFieldValue()));
                }else if(param.getCondition() == EsParam.Condition.DISTINCE){
                    Double value = null;
                    if(param.getFieldValue().getClass() == Double.class){
                        value = (Double)param.getFieldValue();
                    }else if(param.getFieldValue().getClass() == BigDecimal.class){
                        value = ((BigDecimal)param.getFieldValue()).doubleValue();
                    }
                    if(lat != null && lon != null && value != null){
                        boolQueryBuilder.must(QueryBuilders.geoDistanceQuery(param.getFieldName()).point(lat, lon).distance(value, DistanceUnit.KILOMETERS));
                    }
                }
            }
        }
        if (matchList != null && matchList.size()>0){
            BoolQueryBuilder childQueryBuilder = new BoolQueryBuilder();
            for (EsParam param : matchList){
                childQueryBuilder.should(new MatchQueryBuilder(param.getFieldName(),param.getFieldValue()));
                childQueryBuilder.should(QueryBuilders.wildcardQuery(param.getFieldName(),"*"+param.getFieldValue()+"*"));
            }
            boolQueryBuilder.must(childQueryBuilder);
        }
        searchSourceBuilder.query(boolQueryBuilder);
    }
}

