package com.frame.easyandroid.test;

import java.util.List;

import android.test.AndroidTestCase;

import com.frame.easyandroid.bean.Book;
import com.frame.easyandroid.db.dao.BookDao;
import com.frame.easyandroid.db.dao.impl.BookDaoImpl;
import com.frame.easyandroid.util.Logger;

/**
 * 测试框架，ManiFest中已经注册，直接使用
 * 
 * @author liuzhao
 * 
 */
public class Test extends AndroidTestCase {

	private static final String tag = "Test";
	
	/**
	 * 测试插入Book信息
	 */
	public void bookInsert() {
		BookDao dao = new BookDaoImpl(getContext());
		Book book = new Book();
		book.setId(1);
		book.setName("Android 2");
		book.setSummary("这是一本Android方面的很好的教材！");
		dao.insert(book);
	}

	/**
	 * 修改Book信息
	 */
	public void bookUpdate() {
		BookDao dao = new BookDaoImpl(getContext());
		Book book = new Book();
		book.setId(1);
		book.setName("Android 2 update");
		dao.update(book);
	}

	/**
	 * 查询Book信息
	 */
	public void bookQuery() {
		BookDao dao = new BookDaoImpl(getContext());

		List<Book> books = dao.queryAll();
		Logger.i(tag, books.size()+"");
	}

	/**
	 * 删除Book
	 */
	public void bookDelete() {
		BookDao dao = new BookDaoImpl(getContext());
		dao.delete(2);
	}
}
