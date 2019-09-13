package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

/**
 * @ClassName EsCourseService
 * @Author 邓元粮
 * @Date 2019/8/2 7:57
 * @Version 1.0
 **/
@Service
public class EsCourseService {

    Logger LOGGER = LogManager.getLogger(EsCourseService.class);
    // 索引库指定
    @Value("${xuecheng.course.index}")
    private String index;

    //type指定
    @Value("${xuecheng.course.type}")
    private String type;

    // 索引库指定
    @Value("${xuecheng.media.index}")
    private String mediaIndex;

    //type指定
    @Value("${xuecheng.media.type}")
    private String mediaType;

    //显示字段指定
    @Value("${xuecheng.course.source_field}")
    private String source_field;

    //显示字段指定
    @Value("${xuecheng.media.source_field}")
    private String mediaSourceField;



    @Autowired
    RestHighLevelClient highLevelClient;

    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam)  {
        if (courseSearchParam == null ){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        QueryResult<CoursePub> queryResult = new QueryResult<CoursePub>();
        //创建searchRequest指定索引库
        SearchRequest searchRequest = new SearchRequest(index);
        //指定type
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        if (StringUtils.isNotBlank(source_field)){
            searchSourceBuilder.fetchSource(source_field.split(","),new String[0]);
        }else{//没有配置
            searchSourceBuilder.fetchSource(new String[0],new String[0]);
        }
        //条件构建--总
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        addSearchConditions(courseSearchParam, boolQueryBuilder);
        //分页参数构建
        page = page == 0 ? 1 : page ;
        size = size == 0 ? 10 : size;
        int from = page - 1;
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);
        //建立关系
        searchSourceBuilder.query(boolQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;
        //高亮處理
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font class = 'eslint'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));

        try {
            searchResponse = highLevelClient.search(searchRequest);
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("xuecheng search error..{}",e.getMessage());
            return new QueryResponseResult(CommonCode.SUCCESS,new QueryResult<CoursePub>());
        }
        handleResult(queryResult, searchResponse);

        //响应结果集
        QueryResponseResult<CoursePub> queryResponseResult = new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    /*
     *@Description 查询条件处理
     * @Author 邓元粮
     * @MethodName addSearchConditions
     * @Datetime 10:46 2019/8/4
     * @Param [courseSearchParam, boolQueryBuilder]
     * @return void
     **/
    private void addSearchConditions(CourseSearchParam courseSearchParam, BoolQueryBuilder boolQueryBuilder) {
        String keyword = courseSearchParam.getKeyword();
        if (StringUtils.isNotBlank(keyword)){//关键字查询
            String[] searchFieldsArr = new String[]{"name","description","teachplan"};
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, searchFieldsArr);
            multiMatchQueryBuilder.minimumShouldMatch("70%");
            multiMatchQueryBuilder.field("name",10);
           boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        //根据等级查询
        if (StringUtils.isNotBlank(courseSearchParam.getMt())){
            //一级分类
            boolQueryBuilder.must(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        if (StringUtils.isNotBlank(courseSearchParam.getSt())){
            //一级分类
            boolQueryBuilder.must(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        if (StringUtils.isNotBlank(courseSearchParam.getGrade())){
            //一级分类
            boolQueryBuilder.must(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }


    }

    /*
     *@Description 结果集处理
     * @Author 邓元粮
     * @MethodName handleResult
     * @Datetime 10:46 2019/8/4
     * @Param [queryResult, searchResponse]
     * @return void
     **/
    private void handleResult(QueryResult<CoursePub> queryResult, SearchResponse searchResponse) {
        //处理结果集
        if (null != searchResponse){
            SearchHits searchHits = searchResponse.getHits();
            if (null != searchHits){
                long totalHits = searchHits.getTotalHits();
                queryResult.setTotal(totalHits);
                SearchHit[] hits = searchHits.getHits();
                if (null != hits && hits.length > 0){
                    List<CoursePub> list = new LinkedList<CoursePub>();
                    for (int i = 0; i < hits.length; i++) {
                        SearchHit hit = hits[i];
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                        if (!CollectionUtils.isEmpty(sourceAsMap)){
                            CoursePub coursePub = new CoursePub();
                            String name = (String) sourceAsMap.get("name");
                            coursePub.setName(name);
                            //图片
                            String pic = (String) sourceAsMap.get("pic");
                            coursePub.setPic(pic);
                            //价格
                            Double price = null;
                            try {
                                if(sourceAsMap.get("price")!=null ){
                                    price = (Double) sourceAsMap.get("price");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            coursePub.setPrice(price);
                            Double price_old = null;
                            try {
                                if(sourceAsMap.get("price_old")!=null ){
                                    price_old = (Double) sourceAsMap.get("price_old");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            coursePub.setPrice_old(price_old);
                            list.add(coursePub);
                        }
                    }
                    queryResult.setList(list);
                }else{//结果集为空
                }
            }
        }else{//结果集为空
            queryResult.setTotal(0L);
            queryResult.setList(new ArrayList<CoursePub>());
        }
    }

    /*
     *@Description 根据课程id获取课程计划信息
     * @Author 邓元粮
     * @MethodName getall
     * @Datetime 11:44 2019/9/7
     * @Param [id]
     * @return java.util.Map<java.lang.String,com.xuecheng.framework.domain.course.CoursePub>
     **/
    public Map<String, CoursePub> getall(String id) {
        Map<String, CoursePub> resultMap = new HashMap<>(5);
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("id", id));
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = highLevelClient.search(searchRequest);
            if (null != searchRequest){
                SearchHits searchHits = searchResponse.getHits();
                SearchHit[] hits = searchHits.getHits();
                if (null != hits && hits.length > 0){
                    for (SearchHit hit : hits) {
                        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                        if (null != sourceAsMap & sourceAsMap.size() > 0){
                            CoursePub coursePub = new CoursePub();
                            String courseId = (String) sourceAsMap.get("id");
                            String name = (String) sourceAsMap.get("name");
                            String grade = (String) sourceAsMap.get("grade");
                            String charge = (String) sourceAsMap.get("charge");
                            String pic = (String) sourceAsMap.get("pic");
                            String description = (String) sourceAsMap.get("description");
                            String teachplan = (String) sourceAsMap.get("teachplan");
                            coursePub.setId(courseId);
                            coursePub.setName(name);
                            coursePub.setPic(pic);
                            coursePub.setGrade(grade);
                            coursePub.setTeachplan(teachplan);
                            coursePub.setDescription(description);
                            resultMap.put(courseId,coursePub);
                        }
                    }
                }
            }

        } catch (IOException e) {
            LOGGER.error(e.getMessage(),e);
            e.printStackTrace();
        }
        return resultMap;
    }

    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds) {
        if (teachplanIds == null && teachplanIds.length <= 0) {
            return null;
        }
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(mediaIndex);
        searchRequest.types(mediaType);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id", teachplanIds));
        String[] includeFields = mediaSourceField.split(",");
        searchSourceBuilder.fetchSource(includeFields, new String[]{});
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = highLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            long totalHits = searchHits.getTotalHits();
            queryResult.setTotal(totalHits);
            SearchHit[] hits = searchHits.getHits();
            List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
            if(hits != null && hits.length > 0){
                for (SearchHit hit: hits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    TeachplanMediaPub teachplanMediaPub =new TeachplanMediaPub();
                     //取出课程计划媒资信息
                    String courseid = (String) sourceAsMap.get("courseid");
                    String media_id = (String) sourceAsMap.get("media_id");
                    String media_url = (String) sourceAsMap.get("media_url");
                    String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                    String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                    teachplanMediaPub.setCourseId(courseid);
                    teachplanMediaPub.setMediaUrl(media_url);
                    teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                    teachplanMediaPub.setMediaId(media_id);
                    teachplanMediaPub.setTeachplanId(teachplan_id);
                    //将数据加入列表
                    teachplanMediaPubList.add(teachplanMediaPub);
                }
            }
            queryResult.setList(teachplanMediaPubList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResponseResult<TeachplanMediaPub> responseResult = new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS, queryResult);
        return responseResult;
    }

}
