package com.frame.easyandroid.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.frame.easyandroid.biz.MyCrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 全局Application
 * 
 * @author zhao.liu
 * 
 */
@SuppressWarnings("unused")
public class MyApp extends Application {
	private ImageLoader imageLoader;
	private ImageLoaderConfiguration config;
	public static Context appContext;

	@Override
	public void onCreate() {
		super.onCreate();
		appContext = this;
		/**
		 * 实现程序异常终止时候优雅的关闭以及重新启动app
		 */
		MyCrashHandler handler = MyCrashHandler.getMyCrashHandler();
		handler.init(getApplicationContext());
		Thread.currentThread().setUncaughtExceptionHandler(handler);

		/**
		 * 使用Universal_ImageLoader的准备工作！
		 */
		imageLoader = ImageLoader.getInstance();
		config = new ImageLoaderConfiguration.Builder(getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();
		// Initialize ImageLoader with configuration
		ImageLoader.getInstance().init(config);

	}

	/**
	 * 低内存的时候发送广播：关闭正在访问的activity，清理内存！ 目的避免因为OOM异常造成程序强行退出！
	 */
	@Override
	public void onLowMemory() {
		Intent intent = new Intent();
		intent.setAction("Low_Memory_Kill");
		sendBroadcast(intent);
		super.onLowMemory();
	}

	/**
	 * 向外提供获取全局Context方法
	 * 
	 * @return
	 */
	public static Context getContext() {
		return appContext;
	}
}
