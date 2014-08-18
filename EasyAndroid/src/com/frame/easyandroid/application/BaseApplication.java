package com.frame.easyandroid.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.frame.easyandroid.biz.MyCrashHandler;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * 全局Application：执行初始化、以及工具类需要数据的准备。 也可以传递全局数据
 * 
 * @author zhao.liu
 * 
 */
@SuppressWarnings("unused")
public class BaseApplication extends Application {
	/** UniversalImageLoad的配置 */
	private ImageLoader imageLoader;
	private ImageLoaderConfiguration config;
	/** 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了 */
	public static Context appContext;
	/** 主线程ID */
	private static int mMainThreadId = -1;
	/** 主线程ID */
	private static Thread mMainThread;
	/** 主线程Handler */
	private static Handler mMainThreadHandler;
	/** 主线程Looper */
	private static Looper mMainLooper;

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
		ImageLoader.getInstance().init(config);
		mMainThreadId = android.os.Process.myTid();
		mMainThread = Thread.currentThread();
		mMainThreadHandler = new Handler();
		mMainLooper = getMainLooper();
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

	/**
	 * 获取主线程ID
	 * 
	 */
	public static int getMainThreadId() {
		return mMainThreadId;
	}

	/**
	 * 获取主线程
	 * 
	 */
	public static Thread getMainThread() {
		return mMainThread;
	}

	/**
	 * 获取主线程的handler
	 * 
	 */
	public static Handler getMainThreadHandler() {
		return mMainThreadHandler;
	}

	/**
	 * 获取主线程的looper
	 * 
	 */
	public static Looper getMainThreadLooper() {
		return mMainLooper;
	}
}
