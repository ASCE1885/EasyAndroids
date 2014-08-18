package com.frame.easyandroid.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.frame.easyandroid.R;

/**
 * Dialog显示的工具类；可以在此扩展
 * 
 * @author liuzhao
 * 
 */
public class PromptManager {

	/**
	 * 显示一个自定义的Dialog
	 * 
	 * @param context
	 * @param mag
	 *            想要显示的信息
	 * @param listener
	 *            回调接口
	 */
	public static void showDialog(Context context, String mag,
			final dialogListener listener) {
		final Dialog d = getDialog(context, 0.5f);
		TextView tv = (TextView) d.findViewById(R.id.textView1);
		tv.setText(mag);
		d.setCanceledOnTouchOutside(true);
		Button ok = (Button) d.findViewById(R.id.button1);
		Button no = (Button) d.findViewById(R.id.button2);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickBut(true);
				d.dismiss();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickBut(false);
				d.dismiss();
			}
		});
		d.show();

	}

	/**
	 * 获取显示Dialog的实例对象！
	 * 
	 * @param context
	 * @param f
	 *            透明度
	 * @return
	 */
	private static Dialog getDialog(Context context, float f) {
		final Dialog d = new Dialog(context, R.style.init_game);
		Window window = d.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.dimAmount = f;// 越大越不透明
		window.setAttributes(lp);
		window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		d.setContentView(R.layout.pop_twobtn);
		return d;
	}

	/**
	 * 按功能键，模拟出来Menu进行显示！
	 * 
	 * @param context
	 * @param mag
	 *            要显示的信息！
	 * @param listener
	 *            回调接口
	 */
	public static void showMenu(Context context, String mag,
			final dialogListener listener) {
		final Dialog d = getDialog(context, 0.5f);
		TextView tv = (TextView) d.findViewById(R.id.textView1);
		tv.setText(mag);
		Window window = d.getWindow();
		window.setGravity(Gravity.BOTTOM);
		Button ok = (Button) d.findViewById(R.id.button1);
		Button no = (Button) d.findViewById(R.id.button2);
		d.setCanceledOnTouchOutside(true);// 设置点击外部可以取消这个Dialog
		d.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_MENU) {
					d.dismiss();
				}
				return false;
			}
		});

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				listener.clickBut(true);
				d.dismiss();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/**
				 * 在按功能键弹出的Dialog上点击取消，其实是什么事情都没做（没写代码） 在baseActivity中，可以看看理解下！
				 */
				listener.clickBut(false);
				d.dismiss();
			}
		});
		d.show();

	}

	/**
	 * 写一个接口，进行函数的回调，让调用者确定究竟做什么！
	 * 
	 * @author liuzhao
	 */
	public interface dialogListener {
		void clickBut(boolean isOk);
	}
}
