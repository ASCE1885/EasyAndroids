package com.frame.easyandroid.biz;

/**
 * 程序在开发阶段与测试阶段许多参数设置是不一样的；例如：Log是否打印，错误文件是否保存在手机SD卡中，正式测试服务器切换等！
 * 将需要修改的东西放在这里统一修改 备注：使用方法，只需要设置这一行；private static boolean ifDebug = true;
 * 开发阶段设置ifDebug为true;上线阶段设置ifDebug为false。
 * 
 * 备注：这是个简单的，实际开发根据不同的业务需要在这里统一的配置上线、开发等不同的选项。
 * 
 * @author zhao.liu
 * 
 */
public class AppModel {
	/**
	 * 用来标示当前应用开发状态；true代表处于开发状态，Log保存等；false代表上线；
	 */
	private static boolean ifDebug = true;

	/**
	 * 打印Log的等级，0就是不打印；大于等于6全部打印
	 */
	private static int logLevel = 6;

	private static boolean ifSave2Sd = true;

	static {
		if (ifDebug) {
			logLevel = 6;
			ifSave2Sd = true;
		} else {
			logLevel = 0;
			ifSave2Sd = false;
		}
	}

	/**
	 * 想要不打印Log的话设置返回值为0即可
	 * 
	 * @return
	 */
	public static int getLogLevel() {
		return logLevel;
	}

	/**
	 * 设置是否保存将程序出错信息保存到SD卡中 true为保存，false为不保存
	 * 
	 * @return
	 */
	public static boolean getIfSave2Sd() {
		return ifSave2Sd;
	}

	/**
	 * 用来标示当前应用开发状态；true代表处于开发状态，Log保存等；false代表上线；
	 * 
	 * @return
	 */
	public static boolean getIfDebug() {
		return ifDebug;
	}
}
