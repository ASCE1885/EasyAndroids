package com.frame.easyandroid.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.frame.easyandroid.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

/**
 * 关于图片操作的工具类；压缩，转换；方便的图片读取，缓存等
 * 
 * @author liuzhao
 * 
 */
public class DrawableUtils {

	/**
	 * 根据需要（传递的参数）从网上获取合适的压缩图片。 这个方法包含：1.假解析；获取合适的一个InSampleSize。不需要分配内存！
	 * 2.真解析；将图片进行压缩再次显示出来！真实的分配内存！
	 * 
	 * @param data
	 *            byte信息
	 * @param offset
	 *            起点
	 * @param length
	 *            全部长度
	 * @param reqWidth
	 *            期望压缩后的宽度
	 * @param reqHeight
	 *            期望压缩后的高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeSampledBitmapFromInt(byte[] data, int offset,
			int length, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, offset, length, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, offset, length, options);
	}

	/**
	 * 根据需要（传递的参数）从SD卡中获取合适的压缩图片。 这个方法包含：1.假解析；获取合适的一个InSampleSize。不需要分配内存！
	 * 2.真解析；将图片进行压缩再次显示出来！真实的分配内存！
	 * 
	 * @param filePath
	 *            文件路径
	 * @param reqWidth
	 *            期望压缩后的宽度
	 * @param reqHeight
	 *            期望压缩后的高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeSampledBitmapFromSD(String filePath,
			int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(filePath, options);
	}

	/**
	 * 根据需要（传递的参数）从资源文件中获取合适的压缩图片。 这个方法包含：1.假解析；获取合适的一个InSampleSize。不需要分配内存！
	 * 2.真解析；将图片进行压缩再次显示出来！真实的分配内存！
	 * 
	 * @param res
	 *            资源图片
	 * @param resId
	 * @param reqWidth
	 *            期望压缩后的宽度
	 * @param reqHeight
	 *            期望压缩后的高度
	 * @return 压缩后的图片
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * 获取一个合适的压缩比例：InSampleSize
	 * 根据需要（传入的宽高设置），与原图片的宽高进行比例的调节，获取一个合适的InSampleSize也就是一个合适的压缩比例！
	 * 
	 * @param options
	 *            BitmapFactory的设置选项
	 * @param reqWidth
	 *            期望压缩后的宽度
	 * @param reqHeight
	 *            期望压缩后的高度
	 * @return 压缩后的图片
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	/**
	 * 将Drawable转换为bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable != null) {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			Bitmap bitmap = Bitmap.createBitmap(w, h, config);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 将Bitmap转换为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable bitmapToDrawable(Bitmap bitmap) {
		if (bitmap != null) {
			return new BitmapDrawable(bitmap);
		} else {
			return null;
		}
	}

	/**
	 * InputStream2bitmap
	 * 
	 * @param inputStream
	 * @return
	 * @throws Exception
	 */
	public static Bitmap inputStreamToBitmap(InputStream inputStream)
			throws Exception {
		if (inputStream != null) {
			return BitmapFactory.decodeStream(inputStream);
		} else {
			return null;
		}
	}

	/**
	 * Bytes2bitmap
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Bitmap bytesToBitmap(byte[] byteArray) {
		if (byteArray != null && byteArray.length != 0) {
			return BitmapFactory
					.decodeByteArray(byteArray, 0, byteArray.length);
		} else {
			return null;
		}
	}

	/**
	 * Bytes2drawable
	 * 
	 * @param byteArray
	 * @return
	 */
	public static Drawable byteToDrawable(byte[] byteArray) {
		if (byteArray != null && byteArray.length > 0) {
			ByteArrayInputStream ins = new ByteArrayInputStream(byteArray);
			return Drawable.createFromStream(ins, null);
		} else {
			return null;
		}

	}

	/**
	 * Bitmap2bytes
	 * 
	 * @param byteArray
	 * @return
	 */
	public static byte[] bitmapToBytes(Bitmap bm) {
		if (bm != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
			byte[] bytes = baos.toByteArray();
			return bytes;
		} else {
			return null;
		}

	}

	/**
	 * Drawable2bytes
	 * 
	 * @param drawable
	 * @return
	 */
	public static byte[] drawableToBytes(Drawable drawable) {
		if (drawable != null) {
			BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
			Bitmap bitmap = bitmapDrawable.getBitmap();
			byte[] bytes = bitmapToBytes(bitmap);
			;
			return bytes;
		} else {
			return null;
		}

	}

	/**
	 * 获取带有倒影的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getReflectionImageWithBitmap(Bitmap bitmap) {
		if (bitmap != null) {
			final int reflectionGap = 4;
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);
			Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w,
					h / 2, matrix, false);

			Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2),
					Config.ARGB_8888);

			Canvas canvas = new Canvas(bitmapWithReflection);
			canvas.drawBitmap(bitmap, 0, 0, null);
			Paint deafalutPaint = new Paint();
			canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);

			canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, bitmap.getHeight(),
					0, bitmapWithReflection.getHeight() + reflectionGap,
					0x70ffffff, 0x00ffffff, TileMode.CLAMP);
			paint.setShader(shader);
			// Set the Transfer mode to be porter duff and destination in
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			// Draw a rectangle using the paint with our linear gradient
			canvas.drawRect(0, h, w, bitmapWithReflection.getHeight()
					+ reflectionGap, paint);
			return bitmapWithReflection;
		} else {
			return null;
		}
	}

	/**
	 * 获得圆角图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 *            5 10
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
			Canvas canvas = new Canvas(output);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, w, h);
			final RectF rectF = new RectF(rect);
			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);
			return output;
		} else {
			return null;
		}
	}

	/**
	 * 对Bitmap图片进行缩放
	 * 
	 * @param bitmap
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
		if (bitmap != null) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidth = ((float) width / w);
			float scaleHeight = ((float) height / h);
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix,
					true);
			return newbmp;
		} else {
			return null;
		}
	}

	/**
	 * 对Drawable图片进行缩放
	 * 
	 * @param drawable
	 * @param w
	 * @param h
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
		if (drawable != null) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			Bitmap oldbmp = drawableToBitmap(drawable);
			Matrix matrix = new Matrix();
			float sx = ((float) w / width);
			float sy = ((float) h / height);
			matrix.postScale(sx, sy);
			Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
					matrix, true);
			return new BitmapDrawable(newbmp);
		} else {
			return null;
		}
	}

	/**
	 * 清除所有的SD卡某路径的图片
	 * 
	 * @param path
	 */
	public static void deleteAllPictures(String path) {
		if (Utils.checkSD()) {
			File folder = new File(path);
			File[] files = folder.listFiles();
			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	/**
	 * 清除具体某一张图片
	 * 
	 * @param path
	 *            String PATH =
	 *            Environment.getExternalStorageDirectory().getAbsolutePath() +
	 *            "/dirName";
	 * @param fileName
	 */
	public static boolean deletePicture(String path, String fileName) {
		if (Utils.checkSD()) {
			File file = new File(path + "/" + fileName);
			if (file == null || !file.exists() || file.isDirectory())
				return false;
			return file.delete();
		} else {
			return false;
		}
	}

	/**
	 * 将图片保存在SD卡中
	 * 
	 * @param photoBitmap
	 * @param photoName
	 *            String PATH =
	 *            Environment.getExternalStorageDirectory().getAbsolutePath() +
	 *            "/dirName";
	 * @param path
	 */
	public static boolean savePictureToSDCard(Bitmap photoBitmap, String path,
			String photoName) {
		boolean flag = false;
		if (Utils.checkSD()) {
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File photoFile = new File(path, photoName);

			try {
				FileOutputStream fileOutputStream = new FileOutputStream(
						photoFile);

				if (photoBitmap != null) {
					if (photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
							fileOutputStream)) {
						fileOutputStream.flush();
						fileOutputStream.close();
						flag = true;
					}
				}
			} catch (FileNotFoundException e) {
				photoFile.delete();
				e.printStackTrace();
			} catch (IOException e) {
				photoFile.delete();
				e.printStackTrace();
			}
		}
		return flag;
	}

	/**
	 * 从SD卡上拿到图片 String PATH =
	 * Environment.getExternalStorageDirectory().getAbsolutePath() + "/picName";
	 * 
	 * @param photoName
	 * @return
	 */
	public static Bitmap getPictureFromSDCard(String path, String photoName) {
		if (!Utils.checkSD()) {
			return null;
		}
		Bitmap photoBitmap = BitmapFactory.decodeFile(path + "/" + photoName);
		if (photoBitmap == null) {
			return null;
		} else {
			return photoBitmap;
		}
	}

	/**
	 * 判断图片在SD卡中是否存在
	 * 
	 * @param path
	 * @param photoName
	 *            String PATH =
	 *            Environment.getExternalStorageDirectory().getAbsolutePath() +
	 *            "/dirName";
	 * @return
	 */
	public static boolean isPictureExistsInSDCard(String path, String photoName) {
		boolean flag = false;
		if (Utils.checkSD()) {
			File dir = new File(path);
			if (dir.exists()) {
				File folders = new File(path);
				File photoFile[] = folders.listFiles();
				for (int i = 0; i < photoFile.length; i++) {
					String fileName = photoFile[i].getName();
					// System.out.println("isPictureExistsInSDCard:"+fileName);
					if (fileName.equals(photoName)) {
						flag = true;
						break;
					}
				}
			} else {
				flag = false;
			}
		} else {
			flag = false;
		}
		return flag;
	}

	/**
	 * 创建一个图片
	 * 
	 * @param contentColor
	 *            内部填充颜色
	 * @param strokeColor
	 *            描边颜色
	 * @param radius
	 *            圆角
	 */
	public static GradientDrawable createDrawable(int contentColor,
			int strokeColor, int radius) {
		GradientDrawable drawable = new GradientDrawable(); // 生成Shape
		drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
		drawable.setColor(contentColor);// 内容区域的颜色
		drawable.setStroke(1, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影。如果父窗体是可以滑动的，需要把父View设置setScrollCache(false)
		drawable.setCornerRadius(radius); // 设置四角都为圆角
		return drawable;
	}

	/**
	 * 创建一个图片选择器
	 * 
	 * @param normalState
	 *            普通状态的图片
	 * @param pressedState
	 *            按压状态的图片
	 */
	public static StateListDrawable createSelector(Drawable normalState,
			Drawable pressedState) {
		StateListDrawable bg = new StateListDrawable();
		bg.addState(new int[] { android.R.attr.state_pressed,
				android.R.attr.state_enabled }, pressedState);
		bg.addState(new int[] { android.R.attr.state_enabled }, normalState);
		bg.addState(new int[] {}, normalState);
		return bg;
	}

	/**
	 * 获取图片的内存占用大小
	 * 
	 */
	@SuppressLint("NewApi")
	public static int getDrawableSize(Drawable drawable) {
		if (drawable == null) {
			return 0;
		}
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return bitmap.getByteCount();
		} else {
			return bitmap.getRowBytes() * bitmap.getHeight();
		}
	}

	/**
	 * 获取压缩之后的图片
	 * 
	 * @param context
	 * @param resId
	 *            res下资源图片的id
	 * @param reqWidth
	 *            期望的宽度
	 * @param reqHeight
	 *            期望的高度
	 * @return
	 */
	public static Bitmap getImageCompress(Context context, int resId,
			int reqWidth, int reqHeight) {
		Bitmap bitmap = null;
		try {
			bitmap = DrawableUtils.decodeSampledBitmapFromResource(
					context.getResources(), resId, reqWidth, reqHeight);
		} catch (OutOfMemoryError e) {
			bitmap = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.ic_launcher);
			e.printStackTrace();
		}
		return bitmap;
	}

}