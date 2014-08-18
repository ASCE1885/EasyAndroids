package com.frame.easyandroid.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 基础适配器 可以在此基础上扩展，可以配合这个使用协议，更加的简单方便
 * 
 * @author liuzhao
 * @param <T>
 *            泛型
 */
public abstract class EasyAdapter<T> extends BaseAdapter {
	private List<T> mlist = new ArrayList<T>();

	public EasyAdapter(List<T> list) {
		super();
		this.mlist = list;
	}

	/**
	 * 往顶部添加数据
	 * 
	 * @param list
	 */
	public void add2Head(List<T> list) {
		mlist.addAll(0, list);
		notifyDataSetChanged();
	}

	/**
	 * 往底部添加数据
	 * 
	 * @param list
	 */
	public void add2Bottom(List<T> list) {
		mlist.addAll(list);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mlist.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	/**
	 * 实际显示View的方法，使用抽象方法强制调用者覆写！
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getViewItem(position, convertView, parent);
	}

	/**
	 * 抽象方法，强制调用者覆写！
	 * 
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract View getViewItem(int position, View convertView,
			ViewGroup parent);

}
