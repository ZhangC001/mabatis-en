package com.controller;

import base.en.mybatis.common.entity.QueryObj;
import com.entity.SceneWorkCheck;
import com.entity.User;
import com.github.pagehelper.PageInfo;
import com.service.SceneWorkCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/sceneWorkCheck")
public class SceneWorkCheckController {

    @Autowired
    private SceneWorkCheckService sceneWorkCheckService;

    @RequestMapping("/save")
    @ResponseBody
    public Long save() {
        SceneWorkCheck sceneWorkCheck = new SceneWorkCheck();
        sceneWorkCheck.setCheckTime(new Date());
        sceneWorkCheck.setCheckUser("admin");
        sceneWorkCheck.setIsReform(1L);
        sceneWorkCheck.setMaterialIsOk(1L);
        sceneWorkCheckService.save(sceneWorkCheck);
        return sceneWorkCheck.getId();
    }

    @RequestMapping("/saveOrUpdate")
    @ResponseBody
    public Long saveOrUpdate() {
        SceneWorkCheck workCheck = new SceneWorkCheck();
        workCheck.setCheckTime(new Date());
        workCheck.setCheckUser("admin");
        workCheck.setIsReform(2L);
        workCheck.setMaterialIsOk(2L);
        sceneWorkCheckService.saveOrUpdate(workCheck);
        workCheck.setPipeIsOk(1L);
        workCheck.setSceneId(1L);
        sceneWorkCheckService.saveOrUpdate(workCheck);
        return workCheck.getId();
    }

    @RequestMapping("listQuery")
    @ResponseBody
    public List<SceneWorkCheck> listQuery() {
        //构建QueryObj,默认使用对象属性，在Service中查询时，会进行列转换，也可自行调用转换方法，或者设置转换标志位false
        QueryObj query = new QueryObj();
        //添加查询列，可以是函数等，不添加则查询所有列
        query.pushField("id")
                .pushField("sceneId")
                .pushField("checkTime")
                .pushField("sum(id)");
        //添加查询条件
        query.pushQuery("rownum", 10, QueryObj.LT)//进行属性到列的转换时，无对应关系的会被忽略保留为原值
                .pushQuery("sceneId", 1, QueryObj.EQ)
                .pushQuery("checkUser", "admin", QueryObj.EQ, true);
        //添加分组条件
        query.pushGroup("id")
                .pushGroup("sceneId")
                .pushGroup("checkTime");
        //添加排序条件
        query.pushOrder("id", QueryObj.DESC);
        return this.sceneWorkCheckService.listEntityQuery(query);
    }

    @RequestMapping("idQuery")
    @ResponseBody
    public SceneWorkCheck idQuery() {
        return this.sceneWorkCheckService.uniqueIdQuery(1L);
    }

    @RequestMapping("update")
    @ResponseBody
    public int update() {
        QueryObj queryObj = new QueryObj();
        queryObj.pushEntity("checkUser","aaaa", true)
                .pushEntity("isReform",2)
                .pushQuery("id", 44403894, QueryObj.EQ);
        return this.sceneWorkCheckService.updateBatch(queryObj);
    }

    @RequestMapping("idDelete")
    @ResponseBody
    public int idDelete() {
        return this.sceneWorkCheckService.deleteById(44403893L);
    }

    @RequestMapping("pageInfo")
    @ResponseBody
    public PageInfo<SceneWorkCheck> pageInfo() {
        return this.sceneWorkCheckService.getPageInfo(1,2, new QueryObj());
    }

    @RequestMapping("doQuery")
    @ResponseBody
    public List<SceneWorkCheck> doQuery() {
        String sql = "select * from Scene_Work_Check ";
        QueryObj query = new QueryObj(false);
        query.pushQuery("id", 10, QueryObj.GT)
                .pushQuery("firstname", "aaa", QueryObj.LIKE)
                .pushGroup("firstname")
                .pushGroup("age")
                .pushOrder("id", QueryObj.DESC);
        String querySql = query.completeSql(sql);
        return this.sceneWorkCheckService.listEntityQuery(querySql);
    }
}
