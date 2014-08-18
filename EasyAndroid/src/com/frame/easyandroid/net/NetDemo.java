package com.frame.easyandroid.net;

import org.apache.http.Header;
import org.json.JSONObject;

import com.frame.easyandroid.util.Logger;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * 使用AsyncHttpClient进行联网操作的一个Demo！
 * 或者是现在使用Volley也很方便
 * 
 * @author liuzhao
 * 
 */
public class NetDemo {
	protected static final String tag = "NetDemo";
	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void testDemo1() {
		client.setTimeout(10000);
		client.get(
				"http://api.app.i.sogou.com/list/hotest?end=20&from=1&groupid=1",
				new JsonHttpResponseHandler() {// 这里直接使用的是：JsonHttpResponseHandler如果是普通字符的话：AsyncHttpResponseHandler

					/**
					 * 这个方法与下面的方法属于方法的重载 区别在于下面的方法返回的是一个String类型的字符串；
					 * 这个方法返回的是可以直接进行操作的Json对象！
					 */
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						Logger.i(tag, "success");
						super.onSuccess(statusCode, headers, response);
						try {
							Logger.i(tag, response.get("data").toString());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					/**
					 * 联网成功的时候执行onSuccess方法！返回的是String类型的字符串！
					 */
					@Override
					public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
						super.onSuccess(arg0, arg1, arg2);
						Logger.i(tag, "success");
						Logger.i(tag, new String(arg2, 0, arg0));
					}

					/**
					 * 联网失败的时候执行！
					 */
					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						super.onFailure(arg0, arg1, arg2, arg3);
						Logger.i(tag, "failure");
						Logger.i(tag, new String(arg2, 0, arg0));
					}

					/**
					 * 开始的时候调用，必定会被执行！
					 */
					@Override
					public void onStart() {
						super.onStart();
					}

				});

		// 这个使用post方式进行的联网！
		// client.post(url, params, responseHandler)
	}

}
