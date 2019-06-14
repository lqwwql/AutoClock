package com.meteorshower.autoclock.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;

import com.meteorshower.autoclock.constant.Constant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IOUtils {

    public static String getRootStoragePath(Context context) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + File.separator + Constant.APP_PATH + File.separator;
            File file = new File(savePath);
            if (!file.exists()) {
                boolean bResult = file.mkdirs();
            }
            return savePath;
        } else {
            String tmp = context.getFilesDir().getAbsolutePath();
            if (tmp.endsWith(File.separator)) {
                return tmp;
            } else {
                return tmp + File.separator;
            }
        }
    }

    /**
     * 写文本文�?     *
     * @param fileFullName
     * @param content
     */
    public static void writeTxtFile(String fileFullName, String content) {
        FileWriter fw = null;
        try {
            File file = new File(fileFullName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file, true);
            fw.write(content);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getFromAssets(Context context, String fileName) {
        try {
            InputStreamReader inputReader = new InputStreamReader(context.getResources().getAssets().open(fileName));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String Result = "";
            while ((line = bufReader.readLine()) != null)
                Result += line;
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void writeFileSdcard(String fileName, String message) {
        try {
            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = message.getBytes();
            fout.write(bytes);
            fout.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * ��ȡandroid��ǰ�����ڴ��С 
     * @return
     */
    public static String getAvailMemory(Activity activity) {// ��ȡandroid��ǰ�����ڴ��С
    	  
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);  
        //mi.availMem; ��ǰϵͳ�Ŀ����ڴ�  
  
        return Formatter.formatFileSize(activity.getBaseContext(), mi.availMem);// ����ȡ���ڴ��С���
    }  
  
    /**
     * ϵͳ�ڴ��ܴ�С
     * @param activity
     * @return
     */
    public static String getTotalMemory(Activity activity) {
        String str1 = "/proc/meminfo";// ϵͳ�ڴ���Ϣ�ļ�
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;  
  
        try {  
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);  
            str2 = localBufferedReader.readLine();// ��ȡmeminfo��һ�У�ϵͳ���ڴ��С  
  
            arrayOfString = str2.split("\\s+");  
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }  
  
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// ���ϵͳ���ڴ棬��λ��KB������1024ת��ΪByte
            localBufferedReader.close();  
  
        } catch (IOException e) {
        }  
        return Formatter.formatFileSize(activity.getBaseContext(), initial_memory);// Byteת��ΪKB����MB���ڴ��С���
    }
    
    /**      
    * ȡ�ÿ���sd���ռ��С      
    * @return      
    */    
    public static double getAvailaleSize(){     
	    File path = Environment.getExternalStorageDirectory();
	    //ȡ��sdcard�ļ�·��     
	    StatFs stat = new StatFs(path.getPath());

	    //��ȡblock��SIZE    
	    long blockSize = stat.getBlockSize();      
	    /*���е�Block������*/    
	    long availableBlocks = stat.getAvailableBlocks();     
	    /* ����bit��СֵGB*/    
	    return availableBlocks * blockSize * 1.0/1024/1024/1024;      
	    //(availableBlocks * blockSize)/1024      KIB ��λ     
	    //(availableBlocks * blockSize)/1024 /1024  MIB��λ              
    }
    
    /**      
    * SD����С      
    * @return      
    */    
    public static double getAllSize(){     
	    File path = Environment.getExternalStorageDirectory();
	    StatFs stat = new StatFs(path.getPath());
	    /*��ȡblock��SIZE*/    
	    long blockSize = stat.getBlockSize();      
	    /*������*/    
	    long availableBlocks = stat.getBlockCount();     
	    /* ����bit��Сֵ*/    
	    return availableBlocks * blockSize*1.0/1024/1024;      
    }


    public static boolean writeFile(String file, InputStream inputStream){
        try {
        OutputStream outputStream = null;
        File futureStudioIconFile = new File(file);

        try {
            byte[] fileReader = new byte[4096];

           // long fileSize = body.contentLength();
           // long fileSizeDownloaded = 0;
            outputStream = new FileOutputStream(futureStudioIconFile);

            while (true) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }

                outputStream.write(fileReader, 0, read);

             //   fileSizeDownloaded += read;
             //   Log.d("IOUtils", "file download: " + fileSizeDownloaded + " of " + fileSize);
            }

            outputStream.flush();

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        }
    } catch (IOException e) {
        return false;
    }
    }

    /**
     * ɾ���ļ�
     */
    public static void delete(String filePathName) {
        if(TextUtils.isEmpty(filePathName)) return ;
        File file = new File(filePathName);
        if (file.isFile() && file.exists()) {
            boolean flag = file.delete();
        }
    }



    /**
     * �����ļ�
     * @param fromPathName
     * @param toPathName
     * @return
     */
    public static int copy(String fromPathName, String toPathName) {
        try {
            InputStream from = new FileInputStream(fromPathName);
            return copy(from, toPathName);
        } catch (FileNotFoundException e) {
            return -1;
        }
    }

    /**
     * �����ļ�
     * @param from
     * @param toPathName
     * @return
     */
    public static int copy(InputStream from, String toPathName) {
        try {
            delete(toPathName); // ��ɾ��
            OutputStream to = new FileOutputStream(toPathName);
            byte buf[] = new byte[1024];
            int c;
            while ((c = from.read(buf)) > 0) {
                to.write(buf, 0, c);
            }
            from.close();
            to.close();
            return 0;
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * �ļ��Ƿ����
     * @param filePathName
     * @return
     */
    public static boolean isExist(String filePathName) {
        File file = new File(filePathName);
        return (!file.isDirectory() && file.exists());
    }

    public static  boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

}
