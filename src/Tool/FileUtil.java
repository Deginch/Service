package Tool;

import Tool.ErrorLog;

import javax.xml.crypto.Data;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 16-8-4.
 */
public class FileUtil {

    /**
     * 获取文件指定行的内容
     *
     * @param file
     * @param startIndex 开始行，从1开始算起
     * @param endIndex   结束行，所得数据不包括此行
     * @return
     */
    public static String[] getFileData(int startIndex, int endIndex, File file) {
        if (startIndex >= endIndex) {
            return null;
        }
        BufferedReader reader = null;
        int lineNumber = 1;
        String line;
        String[] data = new String[endIndex - startIndex];
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (lineNumber >= startIndex && lineNumber < endIndex) {
                    data[lineNumber - startIndex] = line;
                } else if (lineNumber >= endIndex) {
                    break;
                }
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            ErrorLog.writeLog(e);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }
        }
        return data;
    }

    /**
     * 从指定文件读取指定行数后所有文本内容,包含行数本身
     *
     * @return
     */
    public static StringBuilder getFileData(int startIndex, File file) {
        int lineNumber = 1;
        String line;
        BufferedReader reader = null;
        StringBuilder fileData = new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((line = reader.readLine()) != null) {
                if (lineNumber++ >= startIndex) {
                    fileData.append(line);
                    fileData.append('\n');
                }
            }
        } catch (FileNotFoundException e) {
            ErrorLog.writeLog(e);
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }
        }
        return fileData;
    }

    /**
     * 将data覆盖到文件
     *
     * @param data
     * @param file
     */
    public static void updateFile(String data, File file) {
        if (file.exists()) {
            file.delete();
        }
        BufferedWriter writer = null;
        try {
            file.createNewFile();
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }
        }
    }

    /**
     * 将原文件指定行增加到目的文件后面,[startIndex,endIndex),
     * @param desFile
     * @param srcFile
     * @param startIndex
     * @param endIndex
     */
    public static void appendFile(File desFile,File srcFile,int startIndex,int endIndex){
        if(!desFile.exists()){
            try {
                desFile.createNewFile();
            } catch (IOException e) {
                ErrorLog.writeLog(e);
            }
        }
        if(!srcFile.exists()){
            return;
        }
        BufferedReader reader=null;
        BufferedWriter writer=null;
        try {
            writer=new BufferedWriter(new FileWriter(desFile,true));
            reader=new BufferedReader(new FileReader(srcFile));
            int lineNumber=1;
            String line;
            while ((line=reader.readLine())!=null){
                if(lineNumber>=startIndex&&lineNumber<endIndex){
                    writer.write(line);
                    writer.flush();
                }
            }
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }
        }
    }

    /**
     * 获取最后一行
     *
     * @param file
     * @return
     */
    public static String getLastLine(File file) {
        String result = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                result = line;
            }
        } catch (FileNotFoundException e) {
            ErrorLog.writeLog(file.getName(), e);
        } catch (IOException e) {
            ErrorLog.writeLog(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    ErrorLog.writeLog(e);
                }
            }
        }
        return result;
    }

}
