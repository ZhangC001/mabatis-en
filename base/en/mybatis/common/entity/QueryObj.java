package base.en.mybatis.common.entity;

import base.en.mybatis.common.util.CommonUtils;

import java.util.*;

/**
 * sql 拼接类
 */
public class QueryObj {

    public static String NBSP = " ";

    public static String NULL = "null";

    public static String EQ = "=";

    public static String NOTEQ = "!=";

    public static String GT = ">";

    public static  String LT = "<";

    public static String GTEQ = ">=";

    public static String LTEQ = "<=";

    public static String LIKE = "like '%val%'";

    public static String LLIKE = "like '%val'";

    public static String RLIKE = "like 'val%'";

    public static String IN = "in (val)";

    public static String NOTIN = "not in (val)";

    public static String IS =  "is";

    public static String ISNOT = "is not";

    public static String DESC = "desc";

    public static String ASC = "asc";

    /** where 条件集合 */
    private Set<Query> querys = new HashSet<>(16);

    /** 排序条件集合 */
    private Set<Order> orders = new HashSet<>(8);

    /** 分组列集合 */
    private Set<String> groups = new HashSet<>(8);

    /** 查询列 */
     private Set<String> fields = new HashSet<>(16);

    /** update 语句 列值集合 */
    private Set<Query> updater = new HashSet<>(8);

    /** 表名 */
    private String table;

    /** 是否需要对key进行列转换 */
    private Boolean transference = true;

    /** 属性与表列映射关系 */
    private Map<String, String> columnMapper;

    public QueryObj() {

    }

    public QueryObj(Boolean transference) {
        this.transference = transference;
    }
    /**
     * 设置表名
     * @param table
     * @return
     */
    public QueryObj setTable(String table) {
        this.table = table;
        return this;
    }

    /**
     * 获取表名
     * @return
     */
    public String getTable() {
        return table;
    }

    /**
     *  获取转换标记
     * @return
     */
    public Boolean isTransference() {
        return transference;
    }

    /**
     * 设置转换标记
     * @param transference
     */
    public void setTransference(Boolean transference) {
        this.transference = transference;
    }

    /**
     * 设置映射关系集合
     * @param columnMapper
     */
    public QueryObj columnMapper(Map<String, String> columnMapper) {
        this.columnMapper = columnMapper;
        return this;
    }

    /**
     * group 部分组装
     * @return
     */
    private String toGroup(){
        if(groups.isEmpty()) return "";
        StringBuilder sb = new StringBuilder (" GROUP BY ");
        for(String g : groups){
            sb.append(g);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * group 部分组装
     * @return
     */
    private String toField(){
        if(fields.isEmpty()) return "*";
        StringBuilder sb = new StringBuilder ();
        for(String f : fields){
            sb.append(f);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 排序部分组装
     * */
    public String toOrder(){
        if(orders.isEmpty()) return "";
        StringBuilder  sb = new StringBuilder (" ORDER BY ");
        for(Order order : orders){
            sb.append(order.getFiled());
            sb.append(NBSP);
            sb.append(order.getOrder());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    /**
     * where 条件组装
     * @return
     */
    public String toQuery() {
        if (querys.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(" WHERE ");
        boolean tip = false;
        for(Query query:querys) {
            if (tip){
                sb.append(" and ");
            }
            if(query.getEq().equals(LIKE) || query.getEq().equals(LLIKE) || query.getEq().equals(RLIKE)
                    || query.getEq().equals(IN) || query.getEq().equals(NOTIN)){
                sb.append(query.getEq().replace("val", query.getStringValue()));
            } else {
                sb.append(query.getKey());
                sb.append(NBSP);
                sb.append(query.eq);
                sb.append(NBSP);
                if (query.getEq().equals(IS) || query.getEq().equals(ISNOT)) {
                    sb.append(NULL);
                } else {
                    sb.append(query.getStringValue());
                }
            }
            tip = true;
        }
        return sb.toString();
    }

    /**
     * 组装update语句
     * @return
     */
    public String toUpdate() {
        if (updater.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Query item : updater) {
            sb.append(item.getKey());
            sb.append("=");
            sb.append(item.getStringValue());
            sb.append(",");
        }
        sb.setLength(sb.length()-1);
        return sb.toString();
    }

    /**
     * 组装查询语句
     * @return
     */
    public String toQuerySql() {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(toField());
        sb.append(NBSP);
        sb.append("FROM");
        sb.append(NBSP);
        sb.append(this.table);
        sb.append(NBSP);
        sb.append(toQuery());
        sb.append(toGroup());
        sb.append(toOrder());
        return sb.toString();
    }

    /**
     * 完善sql，where条件，排序，分组部分
     * @param sql
     * @return
     */
    public String completeSql(String sql) {
        StringBuilder sb = new StringBuilder(sql);
        sb.append(NBSP);
        sb.append(toQuery());
        sb.append(toGroup());
        sb.append(toOrder());
        return sb.toString();
    }

    /**
     * 组装update语句
     * @return
     */
    public String toUpdateSql() {
        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(this.table);
        sb.append(NBSP);
        sb.append("SET ");
        sb.append(toUpdate());
        sb.append(toQuery());
        return sb.toString();
    }

    /**
     * 组装delete语句
     * @return
     */
    public String toDeleteSql() {
        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(this.table);
        sb.append(NBSP);
        sb.append(toQuery());
        return sb.toString();
    }

    /**
     * 添加where条件
     * @param key
     * @param value
     * @param eq
     * @return
     */
    public QueryObj pushQuery(String key, Object value, String eq) {
        querys.add(new Query(key, value, eq));
        return this;
    }

    /**
     * 添加where条件
     * @param
     * @return
     */
    public QueryObj pushQuery(String key, Object value, String eq, Boolean mark) {
        querys.add(new Query(key, value, eq, mark));
        return this;
    }

    /**
     * 添加排序条件
     * @param filed
     * @param order
     * @return
     */
    public QueryObj pushOrder(String filed,String order) {
        orders.add(new Order(filed, order));
        return this;
    }

    /**
     * 添加分组条件
     * @param filed
     * @return
     */
    public QueryObj pushGroup(String filed) {
        groups.add(filed);
        return this;
    }

    /**
     * 添加分组条件
     * @param filed
     * @return
     */
    public QueryObj pushField(String filed) {
        fields.add(filed);
        return this;
    }

    /**
     * 添加update和insert语句列值关系
     * @param filed
     * @param order
     * @return
     */
    public QueryObj pushEntity(String filed,Object order){
        updater.add(new Query(filed, order, null));
        return this;
    }

    /**
     * 添加update和insert语句列值关系
     * @param filed
     * @param order
     * @return
     */
    public QueryObj pushEntity(String filed,Object order, Boolean mark){
        updater.add(new Query(filed, order, null, mark));
        return this;
    }

    /**
     * 转换列
     * @return
     */
    public QueryObj transferenceField() {
        if (!querys.isEmpty()) {
            for(Query item : querys) {
                if(columnMapper.get(item.getKey()) != null ){
                    item.setKey(columnMapper.get(item.getKey()));
                }
            }
        }
        if (!orders.isEmpty()) {
            for(Order item : orders) {
                if(columnMapper.get(item.getFiled()) != null ){
                    item.setFiled(columnMapper.get(item.getFiled()));
                }
            }
        }
        if (!groups.isEmpty()) {
            Iterator<String> it = groups.iterator();
            String val;
            String nval;
            List<String> list = new ArrayList<>(16);
            while(it.hasNext()) {
                val = it.next();
                if(columnMapper.get(val) != null ){
                    nval = columnMapper.get(val);
                    if(nval != null) {
                        list.add(nval);
                        it.remove();
                    }
                }
            }
            groups.addAll(list);
        }
        if (!fields.isEmpty()) {
            String val;
            String nval;
            Iterator<String> it = fields.iterator();
            List<String> list = new ArrayList<>(16);
            while(it.hasNext()) {
                val = it.next();
                if(columnMapper.get(val) != null ){
                    nval = columnMapper.get(val);
                    if(nval != null) {
                        it.remove();
                        list.add(nval);
                    }
                }
            }
            fields.addAll(list);
        }
        if (!updater.isEmpty()) {
            for (Query item : updater) {
                if(columnMapper.get(item.getKey()) != null ){
                    item.setKey(columnMapper.get(item.getKey()));
                }
            }
        }
        return this;
    }

    /**
     * where条件内部内
     */
    private class Query implements Comparable<Query>{
        /** 列 */
         private String key;
        /** 值 */
         private Object value;
        /** 连接关系 */
         private String eq;
        /** 是否需要在两边添加单引号 */
         private Boolean mark = false;

        /**
         * 构造
         * @param key
         * @param value
         * @param eq
         */
         private Query(String key, Object value, String eq) {
             this.key = key;
             this.value = value;
             this.eq = eq;
         }

        /**
         * 构造
         * @param key
         * @param value
         * @param eq
         */
        private Query(String key, Object value, String eq, Boolean mark) {
            this.key = key;
            this.value = value;
            this.eq = eq;
            this.mark = mark;
        }


        /**
         * 获取可以
         * @return
         */
        public String getKey() {
            return key;
        }

        /**
         * 获取值
         * @return
         */
        public Object getValue() {
             return value;
        }

        /**
         * 获取关系
         * @return
         */
        public String getEq() {
             return eq;
        }

        /**
         * 标记判断
         * @return
         */
        public Boolean isMark() {
            return mark;
        }

        /**
         * 设置key
         * @return
         */
        public void setKey(String key) {
            this.key = key;
        }

        /**
         * 获取value的string
         * @return
         */
        public String getStringValue() {
             String val = "";
             if(value instanceof Date) {
                 val = CommonUtils.dbDateFmt(value);
             } else if (value instanceof List) {
                 for(Object obj : (List<?>)value){
                     val += ",'" + obj + "'";
                 }
                 val = val.substring(1);
             } else if(value.getClass().isArray()){
                 for(Object obj : (Object[]) value){
                     val += ",'" + obj + "'";
                 }
                 val = val.substring(1);
             } else if(value instanceof String){
                 val = this.getValue().toString().replace("'", "''");
             } else {
                 val = this.value.toString();
             }
            if(isMark()) {
                val = "'" + val + "'";
            }
            return val;
        }

        @Override
        public int compareTo(Query o) {
            return o.getKey().compareTo(this.key);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Query){
                Query query = (Query)obj;
                if(this.value.equals(query.getValue()) && this.key.equals(query.getKey()) && this.eq.equals(((Query) obj).getEq())){
                    return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            int hashcode = 1;
            try{
                hashcode = this.key.hashCode() + this.value.hashCode() + this.eq.hashCode();
            } catch(Exception e){}
            return hashcode;
        }
    }

    /**
     * 排序对象
     */
    public class Order implements Comparable<Order>{
        /** 列 */
        private String filed;
        /** 排序方式 */
        private String order;

        /**
         * 构造
         * @param filed
         * @param order
         */
        private Order(String filed,String order){
            this.filed = filed;
            this.order = order;
        }

        public String getFiled() {
            return filed;
        }

        public String getOrder() {
            return order;
        }

        public void setFiled(String filed) {
            this.filed = filed;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Order){
                Order order = (Order)obj;
                if(this.order.equals(order.getOrder()) && this.filed.equals(order.getFiled())){
                    return true;
                }
            }
            return super.equals(obj);
        }

        @Override
        public int hashCode() {
            int hashcode = 1;
            try{
                hashcode = this.getFiled().hashCode() + this.getOrder().hashCode();
            } catch(Exception e){}
            return hashcode;
        }

        @Override
        public int compareTo(Order o) {
            return this.getFiled().compareTo(o.getFiled()) ;
        }
    }

}
