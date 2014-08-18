package com.frame.easyandroid.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.frame.easyandroid.biz.AppModel;
import com.frame.easyandroid.biz.Constant;

/**
 * 全局的Log管理：通过LOGLEVEL的设置实现Log的管理！
 * 
 * @author zhao.liu
 * 
 */
public class Logger {

	/**
	 * 控制是否将程序出错信息保存到手机SD卡！开发完成之后需要将这个值更改为false
	 */
	private static boolean flag = AppModel.getIfSave2Sd();
	private static int LOGLEVEL = AppModel.getLogLevel();
	private static int VERBISE = 1;
	private static int DEBUG = VERBISE + 1;
	private static int INFO = VERBISE + 2;
	private static int WARN = VERBISE + 3;
	private static int ERROR = VERBISE + 4;
	// 根据需要将Log存放到SD卡中
	private static String path;
	private static FileOutputStream outputStream;

	public static void v(String tag, String msg) {
		if (LOGLEVEL > VERBISE) {
			Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {
		if (LOGLEVEL > DEBUG) {
			Log.d(tag, msg);
		}
	}

	public static void i(String msg) {
		if (LOGLEVEL > INFO) {
			Log.i("lz", msg);
			// save2Sd(msg);// 保存所以Log信息，主要是看下友盟的统计是否正常！
		}
	}

	public static void i(String tag, String msg) {
		if (LOGLEVEL > INFO) {
			Log.i(tag, msg);
			// save2Sd(msg);// 保存所以Log信息，主要是看下友盟的统计是否正常！
		}
	}

	public static void w(String tag, String msg) {
		if (LOGLEVEL > WARN) {
			Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (LOGLEVEL > ERROR) {
			Log.e(tag, msg);
		}
	}

	static {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)
				&& AppModel.getIfDebug()) {
			path = Constant.App_Path + "/log/";
			File file_Path = new File(path);
			file_Path.mkdirs();
			File file = new File(path, "log.txt");
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				outputStream = new FileOutputStream(file, true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将错误信息保存到SD卡中去！可选的操作！ 这些并没有使用FileUtils，因为只是简单的，可选的操作；（路径已定）
	 * 
	 * @param msg
	 */
	@SuppressWarnings("deprecation")
	public static void save2Sd(String msg) {
		if (flag) {
			Date date = new Date();
			String time = date.toLocaleString();
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				if (outputStream != null) {
					try {
						outputStream.write(time.getBytes());
						outputStream.write(msg.getBytes());
						outputStream.write("\r\n".getBytes());
						outputStream.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
