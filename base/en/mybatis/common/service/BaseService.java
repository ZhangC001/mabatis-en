package base.en.mybatis.common.service;

import base.en.mybatis.common.entity.QueryObj;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * 转换QueryObj 进行查询
 * @param <T>
 */
public class BaseService<T> extends CommonService<T> {

    /**
     * 根据QueryObj 执行Map查询
     * @param queryObj
     * @return
     */
    public Map<String, Object> uniqueQuery(QueryObj queryObj) {
        if (null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        return this.uniqueQuery(queryObj.toQuerySql());
    }

    /**
     * 转换QueryObj进行List<Map>查询
     * @param queryObj
     * @return
     */
    public List<Map<String, Object>> listQuery(QueryObj queryObj){
        if (null == queryObj) {
            queryObj = new QueryObj().setTable(tableName);
        }
        if(null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        return this.listQuery(queryObj.toQuerySql());
    }

    /**
     * 转化QueryObj进行单对象查询
     * @param queryObj
     * @return
     */
    public T uniqueEntityQuery(QueryObj queryObj) {
        if (null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        return this.uniqueEntityQuery(queryObj.toQuerySql());
    }

    /**
     * 转化QueryObj进行对象集合查询
     * @param queryObj
     * @return
     */
    public List<T> listEntityQuery(QueryObj queryObj){
        if (null == queryObj) {
            queryObj = new QueryObj().setTable(tableName);
        }
        if(null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        return this.listEntityQuery(queryObj.toQuerySql());
    }

    /**
     * 根据主键查询数据
     * @param id
     * @return
     */
    public T uniqueIdQuery(Long id) {
        QueryObj query = new QueryObj()
                .setTable(tableName)
                .pushQuery("ID", id, QueryObj.EQ);
        return this.uniqueEntityQuery(query);
    }

    /**
     * 根据主键Id执行删除
     * @param id
     * @return
     */
    public int deleteById(Long id) {
        QueryObj query = new QueryObj()
                .setTable(tableName)
                .pushQuery("ID", id, QueryObj.EQ);
        return this.deleteBatch(query.toDeleteSql());
    }

    public Integer countQuery(QueryObj queryObj) {
        if (null == queryObj) {
            queryObj = new QueryObj().setTable(tableName);
        }
        if(null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        queryObj.pushField("COUNT(*)");
        String count = this.stringQuery(queryObj.toQuerySql());
        return Integer.valueOf(count);
    }

    /**
     * QueryObj 转update语句并执行
     * @param queryObj
     * @return
     */
    public int updateBatch(QueryObj queryObj) {
        if (null == queryObj.getTable()) {
            queryObj.setTable(tableName);
        }
        if (queryObj.isTransference()) {
            queryObj.columnMapper(this.columnMapper()).transferenceField();
        }
        return this.updateBatch(queryObj.toUpdateSql());
    }

    public PageInfo<T> getPageInfo(Integer pageNum, Integer pageSize, QueryObj queryObj) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list = listEntityQuery(queryObj);
        PageInfo<T> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
}
