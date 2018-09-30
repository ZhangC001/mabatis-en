package base.en.mybatis.common.util;

import base.en.mybatis.common.entity.Contants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    /**
     * 小驼峰转下划线大写 firstName->FIRST_NAME
     * @param name
     * @return
     */
    public static String underscore(String name) {
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(name);
        while(matcher.find()){
            name = name.replace(matcher.group(), "_"+matcher.group());
        }
        return name.toUpperCase();
    }

    /**
     * 下划线转小驼峰 FIRST_NAME->firstName
     * @param column
     * @return
     */
    public static String camel(String column) {
        Pattern pattern = Pattern.compile("_(\\w)");
        column = column.toLowerCase();
        Matcher matcher = pattern.matcher(column);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()){
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String dateFmt(Date day, String pattern) {
        DateFormat fmt = new SimpleDateFormat(pattern);
        return  fmt.format(day);
    }

    public static String dbDateFmt(Object obj) {
        String fmtStr;
        String dbPatter;
        switch (Contants.DB_TYPE.toUpperCase()) {
            case "ORACLE":
                fmtStr = "to_date('%s','%s')";
                dbPatter = "yyyy-MM-dd HH24:mi:ss";
                break;
            case "MYSQL":
                fmtStr = "date_format('%s','%s')";
                dbPatter = "%Y-%m-%d %H:%I:%S";
                break;
            default:
                fmtStr = "to_date('%s','%s')";
                dbPatter = "yyyy-MM-dd HH24:mi:ss";
                break;
        }
        return String.format(fmtStr, CommonUtils.dateFmt((Date)obj, "yyyy-MM-dd HH:mm:ss"), dbPatter);
    }
}
