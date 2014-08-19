package com.frame.easyandroid.biz;

import com.frame.easyandroid.util.PackageUtils;

import android.os.Environment;

/**
 * 全局的常量设置
 * 
 * @author liuzhao
 * 
 */

public class Constant {

	/**
	 * 一个标示、 默认false,不退出软件 在MainActivity中进行判断，作用：安全退出app！
	 */
	public static boolean isBack = false;

	/**
	 * Hncj统一的SD路径；使用时在这个目录下创建文件夹；
	 */
	public static String App_Path = Environment.getExternalStorageDirectory()
			.getAbsolutePath()
			+ "/Android/data/"
			+ PackageUtils.getPackageName();

}
