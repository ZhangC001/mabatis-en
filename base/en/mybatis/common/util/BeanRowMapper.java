package base.en.mybatis.common.util;

import base.en.mybatis.common.entity.FieldInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 用于将查询结果的Map数据集转化为指定类型对象
 * @param <T> 指定转换对此的类型
 */
public class BeanRowMapper<T> {

    /** 转换结果类的class对象 */
    private Class<T> entityCalss;

    /** 数据列与对象属性和setter方法的映射规则 */
    private MapperMethodRule methodRule = new DefaultMapperMethodRule();

    /** 表名 */
    private String tableName;

    /** 数据列与setter方法 缓存集合 */
    private Map<String, FieldInfo> fieldMapper = new HashMap<>(16);

    /** 属性与列名对应关系 缓存集合 */
    private Map<String, String> columnMapper = new HashMap<>(16);

    /**
     * 构造函数，映射规则类使用默认的DefaultMapperMethodRule
     * @param entityClass
     */
    public BeanRowMapper(Class<T> entityClass)  {
        this.entityCalss = entityClass;
        initMethod();
    }

    /**
     * 构造
     * @param entityClass
     * @param methodRule 自定义映射规则类，实现MapperMethodRule接口的类
     * @throws NoSuchMethodException
     */
    public BeanRowMapper(Class<T> entityClass, MapperMethodRule methodRule) {
        this.entityCalss = entityClass;
        this.methodRule = methodRule;
        initMethod();
    }

    /**
     * get表名
     * @return
     */
    public String getTableName() {
        return this.tableName;
    }

    /**
     * map结果集到对象转换方法
     * @param map
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     * @throws ParseException
     */
    public T mapToBean(Map<String, Object> map) throws IllegalAccessException, InstantiationException, ParseException {
        T bean = entityCalss.newInstance();
        for(Entry<String, Object> item : map.entrySet()) {
            FieldInfo fieldInfo = fieldMapper.get(item.getKey().toUpperCase());
            if(fieldInfo != null) {
                fieldInfo.getField().set(bean, fieldInfo.columnValue(item.getValue()));
            }
        }
        return bean;
    }

    /**
     * 构建映射关系缓存和表名
     */
    private void initMethod() {
        FieldInfo fieldInfo = null;
        for (Field field:entityCalss.getDeclaredFields()) {
            fieldInfo = methodRule.fieldInfoFmt(field, entityCalss);
            if(null != fieldInfo) {
                fieldMapper.put(methodRule.columnFmt(field), fieldInfo);
                columnMapper.put(field.getName(), methodRule.columnFmt(field));
            }
        }
        this.tableName = methodRule.tableNameFmt(entityCalss);
    }

    /**
     * 获取方法映射缓存
     * @return
     */
    public Map<String, FieldInfo> getFieldMapper() {
        return fieldMapper;
    }

    /**
     * 获取属性与列映射缓存
     * @return
     */
    public Map<String, String> getColumnMapper() {
        return columnMapper;
    }
}
