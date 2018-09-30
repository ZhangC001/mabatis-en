package base.en.mybatis.common.service;

import base.en.mybatis.common.util.MapperMethodRule;
import base.en.mybatis.common.dao.CurrencyMapper;
import base.en.mybatis.common.entity.Contants;
import base.en.mybatis.common.entity.FieldInfo;
import base.en.mybatis.common.util.BeanRowMapper;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 数据查询的基础功能Service
 * 1、通过调用公用Mapper查询结果集为Map集合的数据集
 * 2、通过beanRowMapper将Map结果集转化为泛型指定对象结果集
 *      注：默认的映射规则类似DefaultMapperMethodRule，使用实现了MapperMethodRule规则的自定义映射规则时，只需在继承自此
 *      类的无参构造中调用setBeanRowMapper传入自定义的规则类
 * 3、
 *
 * @param <T>
 */
public class CommonService<T> {

    @Resource
    private CurrencyMapper currencyMapper;

    /**
     * 泛型T的class对象
     */
    protected Class <T> entityClass = (Class <T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    /**
     * 默认的对象转换器
     */
    protected BeanRowMapper<T> beanRowMapper = new BeanRowMapper(entityClass);

    /**
     * 根据规则实现类，得到的表名
     */
    protected String tableName = beanRowMapper.getTableName();

    /**
     * 直接返回map集合单查询
     * @param sql
     * @return
     */
    public Map<String, Object> uniqueQuery(String sql){
        return currencyMapper.uniqueQuery(sql);
    }

    /**
     * 直接返回map集合多查询
     * @param sql
     * @return
     */
    public List<Map<String, Object>> listQuery(String sql){
        return currencyMapper.listQuery(sql);
    }

    /**
     * String结果查询，用于单条单列查询结果获取后构建Integer等需要数据类型
     * @param sql
     * @return
     */
    public String stringQuery(String sql) {
        return currencyMapper.stringQuery(sql);
    }

    /**
     *执行存储过程，返回结果固定为map
     * @param map 必须包含执行存储过程的sql，例：map.put("sql","test_pro(1,2,#{p3,mode=OUT,jdbcType=INTEGER})")
     * @return
     */
    public Map<String, Object> callableBatch(Map<String, Object> map) {
        this.currencyMapper.callableBatch(map);
        return map;
    }

    /**
     * 返回对象集合
      * @param sql
     * @return
     */
   public List<T> listEntityQuery(String sql) {
        List<T> list = new ArrayList<>();
        for(Map<String, Object> map:listQuery(sql)){
            try {
                list.add(beanRowMapper.mapToBean(map));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 返回单个对象
     * @param sql
     * @return
     */
    public T uniqueEntityQuery(String sql) {
        try {
            return beanRowMapper.mapToBean(uniqueQuery(sql));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 执行删除语句
     * @param sql
     * @return
     */
    public int deleteBatch(String sql) {
        return this.currencyMapper.deleteBatch(sql);
    }

    /**
     * 执行update语句
     * @param sql
     * @return
     */
    public int updateBatch(String sql) {
        return this.currencyMapper.updateBatch(sql);
    }

    /**
     * 执行insert语句
     * @param sql
     * @return
     */
    public int insertBatch(String sql) {
        return this.currencyMapper.insertBatch(sql);
    }

    /**
     * 构建自定义映射规则转换器
     * @param methodRule
     */
    protected void setBeanRowMapper(MapperMethodRule methodRule) {
        this.beanRowMapper = new BeanRowMapper(entityClass,methodRule);
        this.tableName = beanRowMapper.getTableName();
    }

    /**
     * 获取属性列映射
     * @return
     */
    public Map<String, String> columnMapper() {
        return this.beanRowMapper.getColumnMapper();
    }

    /**
     * 获取映射缓存map
     * @return
     */
    public Map<String, FieldInfo>fieldMapper() {
        return this.beanRowMapper.getFieldMapper();
    }

    /**
     * 保存对象
     * @param entity
     * @return
     */
    public int save(T entity) {
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(this.tableName);
        sb.append(" (");
        StringBuilder vb =  new StringBuilder();
        for(Map.Entry<String, FieldInfo> item : fieldMapper().entrySet()){
            try {
                if(item.getKey().equals(Contants.PRIMARY)){
                    switch (Contants.DB_TYPE.toUpperCase()) {
                        case "ORACLE":
                            Long sequence = sequenceNextVal();
                            item.getValue().getField().set(entity, sequence);
                            sb.append(item.getKey());
                            sb.append(",");
                            vb.append(sequence);
                            vb.append(",");
                            break;
                        case "MYSQL":

                            break;
                        default:
                    }
                } else {
                    sb.append(item.getKey());
                    sb.append(",");
                    vb.append(item.getValue().fieldToString(entity));
                    vb.append(",");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        sb.setLength(sb.length()-1);
        sb.append(") VALUES (");
        vb.setLength(vb.length()-1);
        sb.append(vb.toString());
        sb.append(")");
        int res = insertBatch(sb.toString());
        if(Contants.DB_TYPE.toUpperCase().equals("MYSQL")) {
            try {
                fieldMapper().get(Contants.PRIMARY).getField().set(entity, lastInsertId());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 根据对象新增或修改，主键无值修改，有值则编辑
     * @param entity
     * @return
     */
    public int saveOrUpdate(T entity) {
        FieldInfo idFiled = fieldMapper().get(Contants.PRIMARY);
        try {
            if (idFiled.getField().get(entity) == null){
                save(entity);
            } else {
                StringBuilder sb = new StringBuilder("UPDATE ");
                sb.append(this.tableName);
                sb.append(" SET ");
                for(Map.Entry<String, FieldInfo> item : fieldMapper().entrySet()){
                    if(!item.getKey().equals(Contants.PRIMARY)) {
                        sb.append(item.getKey());
                        sb.append(" = ");
                        sb.append(item.getValue().fieldToString(entity));
                        sb.append(",");
                    }
                }
                sb.setLength(sb.length()-1);
                sb.append(" WHERE ");
                sb.append(Contants.PRIMARY);
                sb.append(" = ");
                sb.append(fieldMapper().get(Contants.PRIMARY).fieldToString(entity));
                return updateBatch(sb.toString());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace(System.out);
        } catch (ParseException e) {
            e.printStackTrace(System.out);
        }
        return 0;
    }

    /**
     * ORACLE数据库时提取下一个序列
     * @return
     */
    public Long sequenceNextVal() {
        String sql = "SELECT "+ Contants.SEQUENCE + ".NEXTVAL FROM DUAL";
        String val = stringQuery(sql);
        return Long.valueOf(val);
    }

    /**
     * 读取mysql最后一次插入的自增主键的值
     * @return
     */
    public Long lastInsertId() {
        String sql = "SELECT LAST_INSERT_ID()";
        String val = stringQuery(sql);
        return Long.valueOf(val);
    }
}
