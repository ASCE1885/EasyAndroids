package com.frame.easyandroid.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.alibaba.fastjson.util.IOUtils;
import com.frame.easyandroid.R;
import com.frame.easyandroid.http.HttpHelper;
import com.frame.easyandroid.http.HttpHelper.HttpResult;
import com.frame.easyandroid.util.DrawableUtils;
import com.frame.easyandroid.util.FileUtils;
import com.frame.easyandroid.util.Logger;
import com.frame.easyandroid.util.StringUtils;
import com.frame.easyandroid.util.SystemUtils;
import com.frame.easyandroid.util.ThreadManager;
import com.frame.easyandroid.util.ThreadManager.ThreadPoolProxy;
import com.frame.easyandroid.util.UIUtils;

/**
 * 图片加载工具类的一个封装
 * 
 * @author zhao.liu
 */
public class ImageLoader {
	/** 图片下载的线程池名称 */
	public static final String THREAD_POOL_NAME = "IMAGE_THREAD_POOL";
	/** 图片缓存最大数量 */
	public static final int MAX_DRAWABLE_COUNT = 100;
	/** 图片的KEY缓存 */
	private static ConcurrentLinkedQueue<String> mKeyCache = new ConcurrentLinkedQueue<String>();
	/** 图片的缓存 */
	private static Map<String, Drawable> mDrawableCache = new ConcurrentHashMap<String, Drawable>();
	private static BitmapFactory.Options mOptions = new BitmapFactory.Options();
	/** 图片下载的线程池 */
	private static ThreadPoolProxy mThreadPool = ThreadManager
			.getSinglePool(THREAD_POOL_NAME);
	/** 用于记录图片下载的任务，以便取消 */
	private static ConcurrentHashMap<String, Runnable> mMapRuunable = new ConcurrentHashMap<String, Runnable>();
	/** 图片的总大小 */
	private static long mTotalSize;

	static {
		mOptions.inDither = false;// 设置为false，将不考虑图片的抖动值，这会减少图片的内存占用
		mOptions.inPurgeable = true;// 设置为ture，表示允许系统在内存不足时，删除bitmap的数组。
		mOptions.inInputShareable = true;// 和inPurgeable配合使用，如果inPurgeable是false，那么该参数将被忽略，表示是否对bitmap的数组进行共享
	}

	/** 加载图片 */
	public static void load(ImageView view, String url) {
		if (view == null || StringUtils.isEmpty(url)) {
			return;
		}
		view.setTag(url);// 把控件和图片的url进行绑定，因为加载是一个耗时的，等加载完毕了需要判定该控件是否和该url匹配
		Drawable drawable = loadFromMemory(url);// 从内存中加载
		if (drawable != null) {
			setImageSafe(view, url, drawable);// 如果内存中加载到了，直接设置图片
		} else {
			view.setImageResource(R.drawable.ic_launcher);// 如果没加载到，设置默认图片，并异步加载
			asyncLoad(view, url);
		}
	}

	/** 异步加载 */
	private static void asyncLoad(final ImageView view, final String url) {
		// 先创建一个runnable封装执行过程
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				mMapRuunable.remove(url);
				Drawable drawable = loadFromLocal(url);
				if (drawable == null) {
					drawable = loadFromNet(url);
				}
				if (drawable != null) {
					setImageSafe(view, url, drawable);
				}
			}
		};
		cancel(url);// 先取消这个url的下载
		mMapRuunable.put(url, runnable);// 记住这个runnable，以便后面取消
		mThreadPool.execute(runnable);// 执行任务
	}

	/** 取消下载 */
	public static void cancel(String url) {
		Runnable runnable = mMapRuunable.remove(url);// 根据url来获取指定的runnable
		if (runnable != null) {
			mThreadPool.cancel(runnable);// 从线程池中删除该任务，如果任务已经开始下载了，就无法删除
		}
	}

	/** 从内存中加载 */
	private static Drawable loadFromMemory(String url) {
		Drawable drawable = mDrawableCache.get(url);
		if (drawable != null) {// 从内存中获取到了，需要重新放到内存队列的最后，以便满足LRC
			// 一般缓存算法有两种，第一是LFU，按使用次数来判定删除优先级，使用次数最少的最先删除
			// 还有一个就是LRC，就是按最后使用时间来判定删除优先级，最后使用时间越早的最先删除
			addDrawableToMemory(url, drawable);
		}
		return drawable;
	}

	/** 从本地设备中加载 */
	private static Drawable loadFromLocal(String url) {
		Bitmap bitmap = null;
		Drawable drawable = null;
		String path = FileUtils.getIconDir();
		FileInputStream fis = null;
		try {
			// 获取流
			fis = new FileInputStream(new File(path + url));
			if (fis != null) {
				// BitmapFactory.decodeByteArray(data, offset, length)
				// BitmapFactory.decodeFile(pathName)
				// BitmapFactory.decodeStream(is)
				// 上面三个分析源码可知，他们都是在Java层创建byte数组，然后把数据传递给本地代码。
				// 下面这个是把文件描述符传递给本地代码，由本地代码去创建图片
				// 优点，由于是本地代码创建的，那么byte数组的内存占用不会算到应用内存中，并且一旦内存不足，将会把bitmap的数组回收掉，而bitmap不会被回收
				// 当显示的时候，发现bitmap的数组为空时，将会再次根据文件描述符去加载图片，此时可能由于加载耗时造成界面卡顿，但总比OOM要好得多。
				// 由于本地代码在创建图片时，没有对图片进行校验，所以如果文件不完整，或者根本就不是一个图片时，系统也不会报错，仍然会返回一个bitmap,但是这个bitmap是一个纯黑色的bitmap。
				// 所以我们在下载图片的时候，一定要先以一个临时文件下载，等下载完毕了，再对图片进行重命名。
				bitmap = BitmapFactory.decodeFileDescriptor(fis.getFD(), null,
						mOptions);
			}
			if (null != bitmap) {// 把bitmap转换成drawable
				drawable = new BitmapDrawable(UIUtils.getResources(), bitmap);
			}
			if (drawable != null) {// 放到内存缓存队列中
				addDrawableToMemory(url, drawable);
			}
		} catch (OutOfMemoryError e) {
			mKeyCache.clear();
			mDrawableCache.clear();
			Logger.i(e.toString());
		} catch (Exception e) {
			Logger.i(e.toString());
		} finally {
			IOUtils.close(fis);
		}
		return drawable;
	}

	/** 从网络加载图片 */
	private static Drawable loadFromNet(String url) {
		HttpResult httpResult = HttpHelper.download(HttpHelper.URL
				+ "image?name=" + url);
		InputStream stream = null;
		if (httpResult == null
				|| (stream = httpResult.getInputStream()) == null) {// 请求网络
			return null;
		}
		String tempPath = FileUtils.getIconDir() + url + ".temp";
		String path = FileUtils.getIconDir() + url;
		FileUtils.writeFile(stream, tempPath, true);// 把网络下载保存在本地
		if (httpResult != null) {// 关闭网络连接
			httpResult.close();
		}
		FileUtils.copy(tempPath, path, true);// 进行改名
		return loadFromLocal(url);// 从本地加载
	}

	/** 添加到内存 */
	private static void addDrawableToMemory(String url, Drawable drawable) {
		mKeyCache.remove(url);
		mDrawableCache.remove(url);
		// 如果大于等于100张，或者图片的总大小大于应用总内存的四分之一先删除前面的
		while (mKeyCache.size() >= MAX_DRAWABLE_COUNT
				|| mTotalSize >= SystemUtils.getOneAppMaxMemory() / 4) {
			String firstUrl = mKeyCache.remove();
			Drawable remove = mDrawableCache.remove(firstUrl);
			mTotalSize -= DrawableUtils.getDrawableSize(remove);
		}
		mKeyCache.add(url);// 添加
		mDrawableCache.put(url, drawable);
		mTotalSize += DrawableUtils.getDrawableSize(drawable);
	}

	/** 设置给控件图片 */
	private static void setImageSafe(final ImageView view, final String url,
			final Drawable drawable) {
		if (drawable == null && view.getTag() == null) {
			return;
		}
		UIUtils.runInMainThread(new Runnable() {// 需要在主线程中设置
			@Override
			public void run() {
				Object tag;// 在主线程中判断，可以做到同步
				if ((tag = view.getTag()) != null) {
					String str = (String) tag;
					if (StringUtils.isEquals(str, url)) {// 检测如果url和控件匹配
						view.setImageDrawable(drawable);// 就进行图片设置
					}
				}
			}
		});
	}
}
