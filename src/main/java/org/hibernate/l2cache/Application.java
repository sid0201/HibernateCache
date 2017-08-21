package org.hibernate.l2cache;

import org.hibernate.l2cache.test.TestCachingCollection;
import org.hibernate.l2cache.test.TestCachingQuery;

public class Application {

	public static void main(String[] args) throws Exception {
		new Thread(TestCachingQuery.queryCache(), "Reader worker").start();
		new Thread(TestCachingCollection.writerWorker(), "Writer worker").start();
	}
	
}
