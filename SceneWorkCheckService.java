package com.service;

import com.entity.SceneWorkCheck;
import base.en.mybatis.common.service.BaseService;
import org.springframework.stereotype.Service;

@Service
public class SceneWorkCheckService extends BaseService<SceneWorkCheck> {

    /*使用自定义的字段与数据库列映射规则对象时通过构造函数注入，使用默认的规则，无需此方法
    public SceneWorkCheckService() {
        this.setBeanRowMapper(new DefaultMapperMethodRule());
    }*/


}
