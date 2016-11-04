package ByteTool;

import java.util.Iterator;

/**
 * Created by root on 16-11-2.
 * 用于处理一些字节流的转化
 */
public class ByteUtil {

    /**
     * 从一个数据流中读取指定的数据，返回int值
     *
     * @param iterator 数据流
     * @param length   读取的数据流的长度
     * @param lowAndHigh  是否低位在前
     * @return 返回转化成的int值
     * @throws Exception 如果流的长度不够，则返回异常
     */
    public  static int convertToInt(Iterator iterator, int length,boolean lowAndHigh) throws Exception {
        int num = 0;
        int pow = 0;
        while (length-- > 0) {
            if (iterator.hasNext()) {
                byte data = (byte) iterator.next();
                num += Math.pow(256, lowAndHigh?pow++:length) * (data & 0xff);
            } else {
                throw new Exception("convert fail,required length is " + length + ",real length is" + (pow + 1));
            }
        }
        return num;
    }


    /**
     * 从一个数据流中读取指定的数据，返回int值
     *
     * @param iterator 数据流
     * @param length   读取的数据流的长度
     * @param lowAndHigh  是否低位在前
     * @return 返回转化成的int值
     * @throws Exception 如果流的长度不够，则返回异常
     */
    public  static long convertToLong(Iterator iterator, int length,boolean lowAndHigh) throws Exception {
        long num = 0;
        int pow = 0;
        while (length-- > 0) {
            if (iterator.hasNext()) {
                byte data = (byte) iterator.next();
                num += Math.pow(256, lowAndHigh?pow++:length) * (data & 0xff);
            } else {
                throw new Exception("convert fail,required length is " + length + ",real length is" + (pow + 1));
            }
        }
        return num;
    }

    public static float convertToFloat(Iterator iterator, int length,boolean lowAndHigh) throws Exception {
        return Float.intBitsToFloat(convertToInt(iterator,length,lowAndHigh));
    }

    public static double convertToDouble(Iterator iterator, int length,boolean lowAndHigh) throws Exception {
        return Double.longBitsToDouble(convertToLong(iterator,length,lowAndHigh));
    }

}
