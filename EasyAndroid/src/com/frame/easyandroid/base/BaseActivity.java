package com.frame.easyandroid.base;

import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.frame.easyandroid.MainActivity;
import com.frame.easyandroid.biz.Constant;
import com.frame.easyandroid.util.PromptManager;
import com.frame.easyandroid.util.PromptManager.dialogListener;

/**
 * Activity的基类 使用showLayoutView()替代每次Activity的setContentView(R.layout.);
 * setUpView()为View组件的初始化； fillData()为对象的赋值操作； setListener()为设置组件的监听事件；
 * getViewById()为使用泛型替换findViewById()每次需要的强转；
 * 
 * 子类使用的时候无需再次调用onCreate()；（备注：调用的话也可以，做setUpView()，fillData()，setListener()
 * 之外的方法）
 * 
 * 以及记录所有的Activity、获取当前栈顶Activity等
 * 
 * @author zhao.liu
 * 
 */
@SuppressWarnings("unchecked")
public abstract class BaseActivity extends Activity implements OnClickListener {
	/** 记录处于前台的Activity */
	private static BaseActivity mForegroundActivity = null;
	/** 记录所有活动的Activity */
	private static final List<BaseActivity> mActivities = new LinkedList<BaseActivity>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(showLayoutView());
		setUpView();
		fillData();
		setListener();
	}

	/**
	 * 获取控件的实例对象的通用方法，避免每一次的强转！ 备注：这是直接使用Activity的加载控件；
	 * 
	 * @param id
	 *            具体控件的Id
	 * @return
	 */
	protected <T extends View> T getViewById(int id) {
		return (T) findViewById(id);
	}

	/**
	 * 获取控件的实例对象的通用方法，避免每一次的强转！ 备注：这是直接使用View的加载控件；区分使用Activity的加载控件；
	 * 
	 * @param id
	 *            具体控件的Id
	 * @return
	 */
	protected <T extends View> T getViewById(View view, int id) {
		return (T) view.findViewById(id);
	}

	/**
	 * 每一个Activity真实的布局
	 * 
	 * @return gen目录中xml的id引用
	 */
	protected abstract int showLayoutView();

	/**
	 * 初始化控件
	 */
	protected abstract void setUpView();

	/**
	 * 对象的赋值操作
	 */
	protected abstract void fillData();

	/**
	 * 控件设置监听事件
	 */
	protected abstract void setListener();

	@Override
	protected void onResume() {
		mForegroundActivity = this;
		super.onResume();
	}

	@Override
	protected void onPause() {
		mForegroundActivity = null;
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		PromptManager.showMenu(this, "确定退出程序？", new dialogListener() {

			@Override
			public void clickBut(boolean isOk) {
				if (isOk) {
					// 点击确定将程序退出
					Constant.isBack = true;
					Intent in = new Intent(BaseActivity.this,
							MainActivity.class);
					in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(in);
					finish();
				} else {

				}
			}
		});
		// 将系统的菜单取消掉！
		return false;
	}

	/** 关闭所有Activity */
	public static void finishAll() {
		List<BaseActivity> copy;
		synchronized (mActivities) {
			copy = new ArrayList<BaseActivity>(mActivities);
		}
		for (BaseActivity activity : copy) {
			activity.finish();
		}
	}

	/** 关闭所有Activity，除了参数传递的Activity */
	public static void finishAll(BaseActivity except) {
		List<BaseActivity> copy;
		synchronized (mActivities) {
			copy = new ArrayList<BaseActivity>(mActivities);
		}
		for (BaseActivity activity : copy) {
			if (activity != except)
				activity.finish();
		}
	}

	/** 是否有启动的Activity */
	public static boolean hasActivity() {
		return mActivities.size() > 0;
	}

	/** 获取当前处于前台的activity */
	public static BaseActivity getForegroundActivity() {
		return mForegroundActivity;
	}

	/** 获取当前处于栈顶的activity，无论其是否处于前台 */
	public static BaseActivity getCurrentActivity() {
		List<BaseActivity> copy;
		synchronized (mActivities) {
			copy = new ArrayList<BaseActivity>(mActivities);
		}
		if (copy.size() > 0) {
			return copy.get(copy.size() - 1);
		}
		return null;
	}

}
