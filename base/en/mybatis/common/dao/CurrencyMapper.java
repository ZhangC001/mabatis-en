package base.en.mybatis.common.dao;

import java.util.List;
import java.util.Map;

/***
 * 公用的数据查询Mapper
 */
public interface CurrencyMapper {

   Map<String, Object> uniqueQuery(String sql);

   List<Map<String, Object>> listQuery(String sql);

   String stringQuery(String sql);

   int updateBatch(String sql);

   int deleteBatch(String sql);

   int insertBatch(String sql);

   Object callableBatch(Map<String, Object> map);
}
