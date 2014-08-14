package com.frame.easyandroid.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.frame.easyandroid.util.Utils;

/**
 * 接收程序低内存的广播进行相对应的处理：需要移植到具体的Activity中去，在代码中注册！
 * 
 * @author liuzhao
 * 
 */
public class KillReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 先将内存缓存清理；然后关闭当前的页面：
		Utils.toastShow("当前内存不足！");
	}

}
