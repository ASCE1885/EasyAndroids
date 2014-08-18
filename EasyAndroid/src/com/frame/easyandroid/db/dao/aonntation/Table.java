package com.frame.easyandroid.db.dao.aonntation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解类辅助实现数据库的操作
 * 
 * @author zhao.liu
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
	/**
	 * 指定实体对应的表名
	 * 
	 * @return
	 */
	public String value();
}
