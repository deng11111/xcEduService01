package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CourseCode;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.DengBeanUtils;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @ClassName CourseService
 * @Author 邓元粮
 * @Date 2019/2/12 17:45
 * @Version 1.0
 **/
@Service
public class CourseService {
    @Autowired
    private TeachplanMapper teachplanMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private TeachplanRepository teachplanRepository;

    @Autowired
    private CourseBaseRepository courseBaseRepository;

    @Autowired
    private CourseMarketRepository courseMarketRepository;

    @Autowired
    private CoursePicRepository coursePicRepository;

    @Autowired
    private CmsPageClient cmsPageClient;

    @Autowired
    private TeachplanMediaRepository teachplanMediaRepository;

    @Autowired
    private TeachplanMediaPubRepository teachplanMediaPubRepository;

    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;

    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;

    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;

    @Value("${course-publish.siteId}")
    private String publish_siteId;

    @Value("${course-publish.templateId}")
    private String publish_templateId;

    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    @Autowired
    private CoursePubRepository coursePubRepository;

    /**
     * 查询课程计划
     *
     * @return com.xuecheng.framework.domain.course.ext.TeachplanNode
     * @Author 邓元粮
     * @MethodName findTeachplanList
     * @Date 20:08 2019/2/12
     * @Param [courseId]
     **/
    public TeachplanNode findTeachplanList(String courseId) {
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        return teachplanNode;
    }

    /**
     * 添加课程计划
     *
     * @return com.xuecheng.framework.model.response.ResponseResult
     * @Author 邓元粮
     * @MethodName addTeachplan
     * @Date 21:27 2019/2/12
     * @Param [teachplan]
     **/
    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {
        //参数校验
        if (teachplan == null
                || StringUtils.isBlank(teachplan.getCourseid())
                || StringUtils.isBlank(teachplan.getPname())
        ) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //获取课程id
        String courseid = teachplan.getCourseid();
        //获取根节点id
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            parentid = getTeachplanRoot(courseid);
        }
        Optional<Teachplan> parentOptional = teachplanRepository.findById(parentid);
        if (!parentOptional.isPresent()) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        Teachplan parentTeachPlan = parentOptional.get();
        //添加课程计划
        Teachplan teachplanNew = new Teachplan();
        BeanUtils.copyProperties(teachplan, teachplanNew);
        teachplanNew.setParentid(parentid);
        teachplanNew.setStatus("0");//未发布
        String parentGrade = parentTeachPlan.getGrade();
        if ("1".equals(parentGrade)) {
            teachplanNew.setGrade("2");
        } else if ("2".equals(parentGrade)) {
            teachplanNew.setGrade("3");
        }
        teachplanRepository.save(teachplanNew);
        //返回结果
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 获取上级节点id
     *
     * @return java.lang.String
     * @Author 邓元粮
     * @MethodName getTeachplanRoot
     * @Date 21:37 2019/2/12
     * @Param [courseid]
     **/
    private String getTeachplanRoot(String courseid) {
        //根据课程id获取根节点id
        List<Teachplan> parentList = teachplanRepository.findByCourseidAndParentid(courseid, "0");
        if (parentList == null || parentList.size() <= 0) {//没有根节点，创建根节点
            Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseid);
            if (!courseBaseOptional.isPresent()) {
                return null;
            }
            CourseBase courseBase = courseBaseOptional.get();
            Teachplan teachplan = new Teachplan();
            teachplan.setParentid("0");
            teachplan.setCourseid(courseid);
            teachplan.setGrade("1");
            teachplan.setPname(courseBase.getName());
            teachplan.setStatus("0");
            teachplanRepository.save(teachplan);
            return teachplan.getId();
        }
        Teachplan parent = parentList.get(0);
        return parent.getId();

    }

    /**
     * 查询我的课程
     *
     * @return com.xuecheng.framework.model.response.QueryResponseResult<com.xuecheng.framework.domain.course.ext.CourseInfo>
     * @Author 邓元粮
     * @MethodName findCourseList
     * @Date 15:48 2019/2/17
     * @Param [page, size, courseListRequest]
     **/
    public QueryResponseResult<CourseInfo> findCourseList(Integer page, Integer size, CourseListRequest courseListRequest) {
        //分页数据处理
        page = page == null ? new Integer(0) : page;
        size = size == null ? new Integer(10) : size;
        PageHelper.startPage(page, size);
        try {
            Page<CourseInfo> coursePage = courseMapper.findCourseListPage(courseListRequest);
            if (null != coursePage) {
                QueryResult queryResult = new QueryResult();
                queryResult.setList(coursePage.getResult());
                queryResult.setTotal(coursePage.getTotal());

                return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS, queryResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new QueryResponseResult<CourseInfo>(CommonCode.FAIL, null);
    }

    @Transactional
    public ResponseResult addCourseBase(CourseBase courseBase) {
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }

        courseBaseRepository.save(courseBase);

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据课程id获取课程基本信息
     *
     * @return com.xuecheng.framework.domain.course.CourseBase
     * @Author 邓元粮
     * @MethodName getCourseBaseById
     * @Date 22:32 2019/2/22
     * @Param [courseId]
     **/
    public CourseBase getCourseBaseById(String courseId) {
        if (StringUtils.isBlank(courseId)) {
            return null;
        }
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    /**
     * 更新课程基本信息
     *
     * @return com.xuecheng.framework.model.response.ResponseResult
     * @Author 邓元粮
     * @MethodName updateCourseBase
     * @Date 22:37 2019/2/22
     * @Param [id, courseBase]
     **/
    @Transactional
    public ResponseResult updateCourseBase(String id, CourseBase courseBase) {
        Optional<CourseBase> optional = courseBaseRepository.findById(id);
        if (StringUtils.isBlank(id)) {
            ExceptionCast.cast(CourseCode.COURSE_PUBLISH_COURSEIDISNULL);
        }
        if (!optional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOTEXIST);
        }
        if (courseBase == null) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        CourseBase result = optional.get();
        BeanUtils.copyProperties(courseBase, result);
        CourseBase courseBaseResult = courseBaseRepository.save(result);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据课程id获取课程相关信息
     *
     * @return com.xuecheng.framework.domain.course.ext.CourseView
     * @Author 邓元粮
     * @MethodName courseview
     * @Date 16:19 2019/6/9
     * @Param [id]
     **/
    public CourseView courseview(String id) {
        CourseView courseView = new CourseView();
        //课程基本信息
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (courseBaseOptional.isPresent()) {
            courseView.setCourseBase(courseBaseOptional.get());
        }
        //课程营销信息
        Optional<CourseMarket> courseMarketOptional = courseMarketRepository.findById(id);
        if (courseMarketOptional.isPresent()) {
            courseView.setCourseMarket(courseMarketOptional.get());
        }
        //课程图片
        Optional<CoursePic> coursePicOptional = coursePicRepository.findById(id);
        if (coursePicOptional.isPresent()) {
            courseView.setCoursePic(coursePicOptional.get());
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(id);
        courseView.setTeachplanNode(teachplanNode);
        return courseView;

    }

    /**
     * 获取课程预览url
     *
     * @return com.xuecheng.framework.domain.course.response.CoursePublishResult
     * @Author 邓元粮
     * @MethodName preview
     * @Date 8:04 2019/6/10
     * @Param [id : 课程id]
     **/
    public CoursePublishResult preview(String id) {
        CmsPage cmsPage = getCmsPage(id);
        CmsPageResult cmsPageResult = cmsPageClient.save(cmsPage);
        if (cmsPageResult == null || !cmsPageResult.isSuccess()) {
            return new CoursePublishResult(CommonCode.FAIL, null);
        }
        CmsPage pageReturn = cmsPageResult.getCmsPage();
        previewUrl = previewUrl + pageReturn.getPageId();
        //远程调用cmsPage服务,获取pageid
        return new CoursePublishResult(CommonCode.SUCCESS, previewUrl);
    }

    /**
     * 构建cmsPage信息
     *
     * @return com.xuecheng.framework.domain.cms.response.CmsPageResult
     * @Author 邓元粮
     * @MethodName getCmsPageResult
     * @Date 14:43 2019/6/10
     * @Param [id：课程id]
     **/
    private CmsPage getCmsPage(String id) {
        //获取课程信息
        CourseBase courseBase = getCourseBase(id);
        //填充cmsPage信息,用于保存
        CmsPage cmsPage = new CmsPage();
        //dataUrl
        cmsPage.setDataUrl(publish_dataUrlPre + id);
        //课程别名
        cmsPage.setPageAliase(courseBase.getName());
        //模板id
        cmsPage.setTemplateId(publish_templateId);
        //页面名称 课程id+.html
        cmsPage.setPageName(id + ".html");
        //页面创建时间
        cmsPage.setPageCreateTime(new Date());
        //页面物理路径
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);
        //pageWebPath
        cmsPage.setPageWebPath(publish_page_webpath);
        //站点id
        cmsPage.setSiteId(publish_siteId);
        return cmsPage;
    }

    /**
     * 根据课程id获取课程信息
     *
     * @return com.xuecheng.framework.domain.course.CourseBase
     * @Author 邓元粮
     * @MethodName getCourseBase
     * @Date 8:44 2019/6/10
     * @Param [id]
     **/
    private CourseBase getCourseBase(String id) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(id);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOTEXIST);
        }
        return courseBaseOptional.get();
    }

    /**
     * 发布页面
     *
     * @return com.xuecheng.framework.domain.course.response.CoursePublishResult
     * @Author 邓元粮
     * @MethodName publish
     * @Date 14:40 2019/6/10
     * @Param [courseId]
     **/
    @Transactional
    public CoursePublishResult publish(String courseId) {
        //保存cmsPage获取页面id
        CmsPage cmsPage = getCmsPage(courseId);
        if (cmsPage == null) {
            return new CoursePublishResult(CmsCode.CMS_PAGE_NOTEXIST, null);
        }
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (cmsPostPageResult == null || !cmsPostPageResult.isSuccess()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_PUBLISHED_FAILED);
        }
        //修改课程状态为已发布
        CourseBase courseBase = saveCoursePubState(courseId);
        //返回操作成功和页面访问路径
        String pageUrl = cmsPostPageResult.getPageUrl();
        //生成coursePub对象供logstach使用
        CoursePub coursePub = createCoursePub(courseId);
        CoursePub coursePubResult = saveCoursePub(courseId, coursePub);
        //保存索引。。。
        saveTeachPlanMediaPub(courseId);


        return new CoursePublishResult(CommonCode.SUCCESS, pageUrl);
    }

    /*
     *@Description 根据课程id保存媒资文件-课程计划信息
     * @Author 邓元粮
     * @MethodName saveTeachPlanMediaPub
     * @Datetime 10:26 2019/9/8
     * @Param [courseId]
     * @return void
     **/
    private void saveTeachPlanMediaPub(String courseId) {
        teachplanMediaPubRepository.deleteByCourseId(courseId);
        List<TeachplanMedia> teachplanMediaList = teachplanMediaRepository.findByCourseId(courseId);
        if (!CollectionUtils.isEmpty(teachplanMediaList)){
            for (TeachplanMedia teachplanMedia :teachplanMediaList) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                BeanUtils.copyProperties(teachplanMedia,teachplanMediaPub);
                Date now = new Date();
                teachplanMediaPub.setTimestamp(now);
                teachplanMediaPubRepository.save(teachplanMediaPub);
            }
        }
    }

    /**
     * 保存coursePub
     *
     * @return com.xuecheng.framework.domain.course.CoursePub
     * @Author 邓元粮
     * @MethodName saveCoursePub
     * @Date 7:08 2019/6/28
     * @Param [courseId, coursePub]
     **/
    private CoursePub saveCoursePub(String courseId, CoursePub coursePub) {
        Optional<CoursePub> coursePubOptional = coursePubRepository.findById(courseId);
        CoursePub coursePubNew = null;
        if (coursePubOptional.isPresent()) {
            coursePubNew = coursePubOptional.get();
        } else {
            coursePubNew = new CoursePub();
        }
        DengBeanUtils.copyProperties(coursePub, coursePubNew);
        //设置课程id
        coursePubNew.setId(courseId);
        //设置时间戳
        coursePubNew.setTimestamp(new Date());
        //设置发布时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String pubTime = sdf.format(new Date());
        coursePubNew.setPubTime(pubTime);
        coursePubRepository.save(coursePubNew);
        return coursePubNew;
    }

    /*
     *
     *@Description 创建coursePub
     * @Author 邓元粮
     * @MethodName createCoursePub
     * @Datetime 7:16 2019/6/28
     * @Param [courseId]
     * @return com.xuecheng.framework.domain.course.CoursePub
     **/
    private CoursePub createCoursePub(String courseId) {
        CoursePub coursePub = new CoursePub();
        //课程基础信息
        Optional<CourseBase> baseOptional = courseBaseRepository.findById(courseId);
        if (baseOptional.isPresent()) {
            CourseBase courseBase = baseOptional.get();
            BeanUtils.copyProperties(courseBase, coursePub);
        }
        //课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            BeanUtils.copyProperties(picOptional.get(), coursePub);
        }
        //课程营销
        Optional<CourseMarket> marketOptional = courseMarketRepository.findById(courseId);
        if (marketOptional.isPresent()) {
            BeanUtils.copyProperties(marketOptional.get(), coursePub);
        }
        //课程计划
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        if (null != teachplanNode) {
            String planStr = JSON.toJSONString(teachplanNode);
            coursePub.setTeachplan(planStr);
        }
        return coursePub;
    }

    /**
     * 修改课程状态为已发布
     *
     * @return com.xuecheng.framework.domain.course.CourseBase
     * @Author 邓元粮
     * @MethodName saveCoursePubState
     * @Date 14:49 2019/6/10
     * @Param [courseId]
     **/
    private CourseBase saveCoursePubState(String courseId) {
        Optional<CourseBase> courseBaseOptional = courseBaseRepository.findById(courseId);
        if (!courseBaseOptional.isPresent()) {
            ExceptionCast.cast(CourseCode.COURSE_NOTEXIST);
        }


        CourseBase courseBase = courseBaseOptional.get();
        courseBase.setStatus("202002");//202002 -已发布
        return courseBaseRepository.save(courseBase);
    }

    /*
     *@Description 保存课程计划以及媒资文件绑定关系
     * @Author 邓元粮
     * @MethodName savemedia
     * @Datetime 8:41 2019/9/7
     * @Param [teachplanMedia]
     * @return com.xuecheng.framework.model.response.ResponseResult
     **/

    @Transactional
    public ResponseResult savemedia(TeachplanMedia teachplanMedia) {
        if (teachplanMedia == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        String teachplanId = teachplanMedia.getTeachplanId();
        Optional<Teachplan> teachplanOptional = teachplanRepository.findById(teachplanId);
        if (!teachplanOptional.isPresent()){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_ISNULL);
        }
        Teachplan teachplan = teachplanOptional.get();
        String grade = teachplan.getGrade();
        if (StringUtils.isEmpty(grade) || !"3".equals(grade)){
            ExceptionCast.cast(CourseCode.COURSE_MEDIA_TEACHPLAN_GRADEERROR);
        }
        Optional<TeachplanMedia> teachplanMediaOptional = teachplanMediaRepository.findById(teachplanId);
        TeachplanMedia teachplanMediaResult = null;
        if (teachplanMediaOptional.isPresent()){
            teachplanMediaResult = teachplanMediaOptional.get();
        }else{
            teachplanMediaResult = new TeachplanMedia();
        }
        teachplanMediaResult.setTeachplanId(teachplanId);
        teachplanMediaResult.setCourseId(teachplanMedia.getCourseId());
        teachplanMediaResult.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
        teachplanMediaResult.setMediaId(teachplanMedia.getMediaId());
        teachplanMediaResult.setMediaUrl(teachplanMedia.getMediaUrl());
        teachplanMediaRepository.save(teachplanMediaResult);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
