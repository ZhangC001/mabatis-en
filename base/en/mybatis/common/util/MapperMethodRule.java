package base.en.mybatis.common.util;

import base.en.mybatis.common.entity.FieldInfo;

import java.lang.reflect.Field;

/**
 * 根据set方法名称，转换对应的列明
 */
public interface MapperMethodRule {

    /**
     * 属性转换表列名
     * @param field
     * @return
     */
    String columnFmt(Field field);

    /**
     * 属性转换出包含setter方法和参数类型的对象
     * @param field
     * @param entity
     * @return
     */
    FieldInfo fieldInfoFmt(Field field, Class entity);

    /**
     *  获取实体类类名，转换为表名
     * @param entity
     * @return
     */
    String tableNameFmt(Class entity);

}
