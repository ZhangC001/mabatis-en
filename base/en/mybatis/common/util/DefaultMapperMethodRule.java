package base.en.mybatis.common.util;

import base.en.mybatis.common.entity.FieldInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 默认的映射规则实现类
 */
public class DefaultMapperMethodRule implements MapperMethodRule {

    @Override
    public String columnFmt(Field field) {
        return CommonUtils.underscore(field.getName());
    }

    @Override
    public FieldInfo fieldInfoFmt(Field field, Class entity) {
        String mName = field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
        Method setMethod = null;
        Method getMethod = null;
        try {
            setMethod = entity.getMethod("set"+mName, field.getType());
            getMethod = entity.getMethod("get"+mName);
            field.setAccessible(true);
            return new FieldInfo(field,columnFmt(field),field.getName(),field.getType(),setMethod,getMethod);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
           return null;
        }
    }

    @Override
    public String tableNameFmt(Class entity) {
        String cname = entity.getSimpleName();
        cname = cname.substring(0,1).toLowerCase()+cname.substring(1);
        return CommonUtils.underscore(cname);
    }

}
