package base.en.mybatis.common.entity;

import java.util.ResourceBundle;

public class Contants {
    private static ResourceBundle bdl= ResourceBundle.getBundle("contants");
    /** 获取配置文件  */
    public static final String getProperties(String key){
        return bdl.getString(key);
    }
    public static String DB_TYPE = getProperties("DB_TYPE");
    public static String SEQUENCE = getProperties("SEQUENCE");
    public static String PRIMARY = getProperties("PRIMARY");
}
