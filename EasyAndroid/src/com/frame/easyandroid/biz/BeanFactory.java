package com.frame.easyandroid.biz;

import java.util.Properties;

/**
 * 使用工厂模式实现解耦！
 * @author liuzhao
 *
 */
public class BeanFactory {
	protected static Properties properties;
	static {
		properties = new Properties();
		try {
			properties.load(BeanFactory.class.getClassLoader().getResourceAsStream("bean.properties"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ExceptionInInitializerError();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getImpl(Class<T> clazz) {

		String simpleName = clazz.getSimpleName();

		String property = properties.getProperty(simpleName);
		T t;
		try {
			t = (T) Class.forName(property).newInstance();
			return t;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
