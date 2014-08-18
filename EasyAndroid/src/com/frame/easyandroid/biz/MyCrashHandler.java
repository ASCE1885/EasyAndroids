package com.frame.easyandroid.biz;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.frame.easyandroid.MainActivity;
import com.frame.easyandroid.util.Logger;

/**
 * 对应用全局未捕获异常的处理核心类，在这里实现优雅的关闭以及应用程序的重启，获得良好的用户体验！
 * 还可以收集用户的使用版本信息、机型信息等
 * 
 * @author zhao.liu
 * 
 */
public class MyCrashHandler implements UncaughtExceptionHandler {
	private Context context;
	private static MyCrashHandler handler;

	/**
	 * 构造方法私有化
	 */
	private MyCrashHandler() {
	}

	public static MyCrashHandler getMyCrashHandler() {
		if (handler == null) {
			handler = new MyCrashHandler();
		}
		return handler;
	}

	public void init(Context context) {
		this.context = context;
	}

	/**
	 * 程序出错的话调用方法！ 实现将
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringBuilder sb = new StringBuilder();
		PackageManager pm = context.getPackageManager();
		PackageInfo packageInfo = null;
		try {
			packageInfo = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_ACTIVITIES);
			sb.append("当前程序版本号为：" + packageInfo.versionName);
			sb.append("\n");
			// 然后获取用户的手机硬件信息
			Field[] fields = Build.class.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				String name = fields[i].getName();
				sb.append(name + " = ");
				String value = fields[i].get(null).toString();
				sb.append(value);
				sb.append("\n");
			}
			// 获取错误的堆栈信息
			StringWriter writer = new StringWriter();
			PrintWriter pw = new PrintWriter(writer);
			ex.printStackTrace(pw);
			String string = writer.toString();
			sb.append(string);
			String msg = sb.toString();
			// 接下来是提交服务器！或者是保存到本地（默认保存到本地）
			Logger.save2Sd(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 然后重启应用程序：

		Intent intent = new Intent(context, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		// 最后完成自杀的操作
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
