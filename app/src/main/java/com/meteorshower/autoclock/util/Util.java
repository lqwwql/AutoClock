package com.meteorshower.autoclock.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.meteorshower.autoclock.constant.AppConstant;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	static Random random = new Random(System.currentTimeMillis());
	static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String subBytes(String src, int beginIndex, int endIndex) {
		if(src == null || beginIndex < 0 || endIndex < 0)
			return null;
		
		byte[] srcBytes = src.getBytes();
		byte[] dst = new byte[endIndex - beginIndex];
		for (int i = 0; i < (endIndex - beginIndex); i++) {
			dst[i] = srcBytes[beginIndex + i];
		}
		return new String(dst);
	}
	
	public static String md5(String key) { 
		String cacheKey; 
		try{ 
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes()); 
			cacheKey = bytesToHexString(mDigest.digest()); 
		} catch (NoSuchAlgorithmException e) { 
			cacheKey = String. valueOf(key. hashCode()); 
		} 
		return cacheKey; 
	}	
	
	private static String bytesToHexString( byte[] bytes) { 
		StringBuilder sb = new StringBuilder(); 
		for (int i = 0; i < bytes.length; i++) { 
			String hex = Integer.toHexString(0xFF & bytes[i]); 
			if (hex.length() == 1) { 
				sb.append('0'); 
			} 
			sb.append( hex); 
		} 
		return sb.toString(); 
	}
	


	
	/**
	 * ??????С???????????????????????????????
	 */
	public static int getCurHour() {
		int curHour = getHour24();
		String HH = getNetTime("HH");
		if(null != HH)
			curHour = Integer.valueOf(HH);
		
		return curHour;
	}
	
	/**
	 * ?????yyyy-MM-dd HH:mm:ss???????????? 
	 */
	public static String getNetTime() {
		return getNetTime("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * ???????????????? 
	 */
    public static String getNetTime(String pattern) {
        URL url = null;//??????????
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//???????????
            uc.setConnectTimeout(30*1000);
            uc.connect(); //????????
            long ld = uc.getDate(); //?????????????
            DateFormat formatter = new SimpleDateFormat(pattern);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            String format = formatter.format(calendar.getTime());
            return format;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
	/**
	 * ?????????????С???24С????
	 */
	public static int getHour24() {
		long time = System.currentTimeMillis();
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		return hour;
	}
	
	/**
	 * ?????????
	 */
	public static String getCurrentTime() {
		return formatter.format(new Date());
	}

	/**
	 * ???time?????
	 */
	public static String getCurrentTime(long time) {
		return formatter.format(new Date(time));
	}

	public static boolean isFileOrDirExist(String path) {
		try {
			File file = new File(path);
			return file.exists();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}


	/**
	 * ?????????
	 * 
	 * @param n
	 * @return
	 */
	public static int random(int n) {
		return random.nextInt(n);
	}

	/**
	 * ????????src?л??????reg?????????????????????????src????????ж?????????????reg???????????????????????
	 * 
	 * @param src
	 *            ??????
	 * @param reg
	 *            ???????
	 * @return
	 */
	public static String reg(String src, String reg) {
		Pattern p = Pattern.compile(reg);// ??????????????????
		Matcher m = p.matcher(src);// ??????
		String res = "";
		while (m.find()) { // ?????????while????if
			res = m.group();
			break;
		}
		return res;
	}

	/**
	 * ???????""?????????
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s) {
		return "\"" + s + "\"";
	}

	/**
	 * ?ж?data??????????JSON???
	 * 
	 * @param data
	 * @return
	 */
	public static boolean isJSONString(String data) {
		try {
			new JSONObject(data);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}

	/**
	 * ???????????
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getResolution(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm;
	}

	/**
	 * ????汾??
	 * 
	 * @return ??????packageName????汾??
	 */
	public static String getVersion(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getThrowableWholeInfo(Throwable e) {
		Writer writer = null;
		PrintWriter printWriter = null;
		try {
			writer = new StringWriter();
			printWriter = new PrintWriter(writer);
			e.printStackTrace(printWriter);
			Throwable cause = e.getCause();
			while (cause != null) {
				cause.printStackTrace(printWriter);
				cause = cause.getCause();
			}
			String result = writer.toString();
			return result;
		} catch (Exception e2) {
			Util.saveThrowableInfo(e2);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			if (printWriter != null)
				printWriter.close();
		}

		return null;
	}

	/**
	 * ??????????????
	 */
	public static void saveThrowableInfo(Throwable e) {
		if (e == null)
			return;

		StringBuffer sb = new StringBuffer();
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String time = formatter.format(new Date());
		String result = writer.toString();
		sb.append(time + "\n" + result + "\n");
		String fileName = "catch.log";
		String path = AppConstant.ERROR;
		File dir = new File(path);
		if (dir.exists() == false) {
			dir.mkdirs();
		}
		try {
			FileOutputStream fos = new FileOutputStream(path + fileName, true);
			fos.write(sb.toString().getBytes());
			fos.close();
		} catch (Exception e2) {
			// TODO: handle exception
		}
	}

	/**
	 * ???????
	 */
	public static void log(String i) {
		try {
			if (i == null)
				return;

			String time = formatter.format(new Date());
			String fileName = "log.txt";
			String path = AppConstant.ERROR;
			File dir = new File(path);
			if (dir.exists() == false)
				dir.mkdirs();
			File log = new File(path + fileName);
			if (log.exists() == false)
				log.createNewFile();

			FileUtils.writeStringToFile(log, time + ": " + i + "\n", "utf8", true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	
	/**
	 * ??????
	 */
	public static void inviteQunLog(String i) {
		try {
			if (i == null)
				return;

			String time = formatter.format(new Date());
			String fileName = "inviteQunLog.txt";
			String path = AppConstant.ERROR;
			File dir = new File(path);
			if (dir.exists() == false)
				dir.mkdirs();
			File log = new File(path + fileName);
			if (log.exists() == false)
				log.createNewFile();

			FileUtils.writeStringToFile(log, time + ": " + i + "\n", "utf8", true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * ?????????????: {"nickname":"xxxx", "phoneNum":"xxxx"}
	 */
	public static void addFriendsLog(String i) {
		try {
			if (i == null)
				return;

			String time = formatter.format(new Date());
			String fileName = "addFriendsLog.txt";
			String path = AppConstant.ERROR;
			File dir = new File(path);
			if (dir.exists() == false)
				dir.mkdirs();
			File log = new File(path + fileName);
			if (log.exists() == false)
				log.createNewFile();

			FileUtils.writeStringToFile(log, time + ": " + i + "\n", "utf8", true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}



	/**
	 * ????????л???????????л?obj
	 * 
	 * @return
	 */
	public static Object readObjectFromFile(String path) {
		Object temp = null;

		File file = new File(path);
		if (file.exists() == false) {
			Log.i("appData", "file not exist!");
			return null;
		}

		FileInputStream in;
		try {
			in = new FileInputStream(file);
			ObjectInputStream objIn = new ObjectInputStream(in);
			temp = objIn.readObject();
			objIn.close();
			Log.i("appData", "read object success!");
		} catch (Exception e) {
			Log.i("appData", "read object failed!");
			Util.saveThrowableInfo(e);
			e.printStackTrace();
		}

		return temp;
	}

	public static boolean isFileExist(String filePath) {
		try {
			File file = new File(filePath);
			return file.exists();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean isNumericZidai(String str) {
		for (int i = 0; i < str.length(); i++) {
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ????scan.sh???????????????/data/data/packageName???????????????????
	 * @param packageName
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static boolean runScanSh(String packageName) throws IOException, InterruptedException {
		ShellUtils.performSuCommand("chmod -R 777 /data/local/tmp");
		releaseSh();
		// ??????
		ShellUtils.performSuCommand("chmod 777 /data/local/tmp/scanDir.sh");
		// ???? scan.sh ???
		
		return ShellUtils.performSuCommand("/data/local/tmp/scanDir.sh /data/data/" + packageName + " " + getGroup(packageName));
	}

	/**
	 * ????scan.sh???????????????path???????????????????
	 * @param packageName
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public static boolean runScanSh(String path, String packageName) throws IOException, InterruptedException {
		ShellUtils.performSuCommand("chmod -R 777 /data/local/tmp");
		releaseSh();
		// ??????
		ShellUtils.performSuCommand("chmod 777 /data/local/tmp/scanDir.sh");
		// ???? su ???
		return ShellUtils.performSuCommand("/data/local/tmp/scanDir.sh " + path + " " + getGroup(packageName));
	}

	public static String getGroup(String packageName) throws InterruptedException {
		String group = null;
		String result = ShellUtils.performSuCommandAndGetRes("ls -l /data/data/");
		if (result != null) {
			result = reg(result, ".*?" + packageName);
			String[] lines = result.split("drw");
			Log.i("switch", "lines.length = " + lines.length);
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				if (line.contains(packageName)) {
					String[] lineSplit = line.split(" ");
					for (int j = 0; j < lineSplit.length; j++) {
						if (lineSplit[j].contains("_")) {
							System.out.println(lineSplit[j]);
							group = lineSplit[j];
							break;
						}
					}
				}
			}
		}
		return group;
	}

	/**
	 * ????sh???
	 */
	private static void releaseSh() throws IOException, InterruptedException {
		StringBuilder content = new StringBuilder();
		content.append("#!/system/bin/sh\n").append("function scandir() {\n")
				.append(" IFS=$(echo -en \"\\n\\b\")\n")
				.append(" local cur_dir parent_dir workdir\n")
				.append(" workdir=$1\n").append(" user=$2\n")
				.append(" cd ${workdir}\n")
				.append(" if [ ${workdir} = \"/\" ]\n").append(" then\n")
				.append("  cur_dir=\"\"\n").append(" else\n")
				.append("  cur_dir=$(pwd)\n").append(" fi\n")
				.append(" chown ${user}:${user} ${cur_dir}/*\n")
				.append(" for dirlist in $(ls ${cur_dir})\n").append(" do\n")
				.append("  if test -d ${dirlist};then\n")
				.append("   cd ${dirlist}\n")
				.append("   if [ ${dirlist} != \"lib\" ];then\n")
				.append("    scandir ${cur_dir}/${dirlist} ${user}\n")
				.append("   fi\n").append("   cd ..\n").append("  fi\n")
				.append(" done\n").append("}\n").append("scandir $1 $2\n");
		Thread.sleep(1000);
		File file = new File("/data/local/tmp/scanDir.sh");
		if (!file.exists()) {
			file.createNewFile();
			FileUtils.write(file, content.toString(), "utf8", false);
			//FileUtils.write(file, content.toString());
		}
	}

}
