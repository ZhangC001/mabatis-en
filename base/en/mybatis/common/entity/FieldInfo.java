package base.en.mybatis.common.entity;

import base.en.mybatis.common.util.CommonUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 类属性信息缓存对象
 */
public class FieldInfo {

    /** 日期格式 */
    private DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    /**  属性的Filed对象 */
    private Field field;

    /**  属性对应的表列名 */
    private String column;

    /**  属性名 */
    private String pname;

    /** 属性类型 */
    private Class pclass;

    /** setter方法*/
    private Method setMethod;

    /** getter方法 */
    private Method getMethod;

    /** 构造 */
    public FieldInfo() {

    }

    /** 构造 */
    public FieldInfo(Field field, String column, String pname, Class pclass, Method setMethod, Method getMethod) {
        this.field = field;
        this.column = column;
        this.pname = pname;
        this.pclass = pclass;
        this.setMethod = setMethod;
        this.getMethod = getMethod;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Class getPclass() {
        return pclass;
    }

    public void setPclass(Class pclass) {
        this.pclass = pclass;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    /** 根据类型装换对应的setter方法对象 */
    public Object columnValue(Object obj) throws ParseException {
        if(obj == null) {
            return null;
        }
        if(pclass.equals(Integer.class)) {
            return Integer.valueOf(obj.toString());
        } else if(pclass.equals(Long.class)){
            return Long.valueOf(obj.toString());
        } else if(pclass.equals(String.class)){
            return obj.toString();
        } else if(pclass.equals(Date.class)){
            return fmt.parse(obj.toString());
        } else if(pclass.equals(Float.class)){
            return Float.valueOf(obj.toString());
        }else if(pclass.equals(BigDecimal.class)){
            return obj;
        } else if(pclass.equals(Short.class)){
            return Short.valueOf(obj.toString());
        } else if(pclass.equals(Character.class)) {
            return obj.toString().charAt(0);
        } else if(pclass.equals(boolean.class)) {
            return Boolean.valueOf(obj.toString());
        } else if(pclass.equals(Byte.class)) {
            return Byte.valueOf(obj.toString());
        } else {
            return null;
        }
    }

    /** 根据类型装换对应的setter方法对象 */
    public Object fieldToString(Object bean) throws ParseException, IllegalAccessException {
        if(bean == null) {
            return "null";
        }
        Object fieldVal = this.field.get(bean);
        if(null == fieldVal) {
            return "null";
        }
       if(fieldVal instanceof  Date){
            return CommonUtils.dbDateFmt(fieldVal);
        } else if(fieldVal instanceof String){
            return "'" + fieldVal.toString() + "'";
        } else if(fieldVal instanceof Character){
           return "'" + fieldVal.toString() + "'";
        } else {
            return fieldVal.toString();
        }
    }
}
