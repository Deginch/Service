package Tool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;
/**
 * Created by root on 16-8-4.
 */
public class DateUtil {

    public static final SimpleDateFormat yyyyMMddHHmmss=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat yyyyMMdd=new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 时间加上指定的时间
     * @param date
     * @param filed
     * @param num
     * @return
     */
    public static synchronized Date addTime(Date date, int filed, int num){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(filed, num);
        return calendar.getTime();
    }

    /**
     * 获取指定日期的年月日部分，时分秒设置为0
     * @param date
     * @return
     */
    public static synchronized Date getDayTime(Date date){
        Date newDate=date;
        try {
            newDate=yyyyMMdd.parse(yyyyMMdd.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

}
