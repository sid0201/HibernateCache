package org.hibernate.l2cache.test;

import static org.hibernate.l2cache.factory.SessionBuilder.sessionFactory;

import org.hibernate.Session;
import org.hibernate.l2cache.domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.hazelcast.hibernate.serialization.Value;

public class TestCachingEntity {
	private static Logger logger = LoggerFactory.getLogger(TestCachingEntity.class);
	
	public static Runnable readerWorker1(){
		return () -> {
			Session session = sessionFactory.openSession();
			logger.info("{"+Thread.currentThread().getName()+ "} Get Employee : ");
			Employee emp1 = (Employee) session.get(Employee.class, 1l);
			logger.info("{"+Thread.currentThread().getName()+"} emp1 : "+emp1);
			logger.info("{"+Thread.currentThread().getName()+ "} Get Employee : ");
			Employee emp2 = (Employee) session.get(Employee.class, 1l);
			logger.info("{"+Thread.currentThread().getName()+"} emp2 : "+emp2+"\n");
			logger.info("{"+Thread.currentThread().getName()+ "} Checking entity keys into L1 cache >> "+session+"\n");
			logger.info("{"+Thread.currentThread().getName()+ "} emp1 == emp2 : "+ (emp1 == emp2));
			logger.info("{"+Thread.currentThread().getName()+ "} Evicting emp1 from session\n");
			session.evict(emp1);
			logger.info("{"+Thread.currentThread().getName()+ "} Again checking entity keys into L1 cache >> "+session+"\n");
			logger.info("{"+Thread.currentThread().getName()+ "} Get Employee : ");
			Employee emp3 = (Employee) session.get(Employee.class, 1l);
			logger.info("{"+Thread.currentThread().getName()+"} emp3 : "+emp3);
			logger.info("{"+Thread.currentThread().getName()+ "} emp1 == emp3 : "+ (emp1 == emp3));
			session.close();
		};
	}
	
	public static Runnable readerWorker2(){
		final Session session = sessionFactory.openSession();
		return () -> {while(!Thread.currentThread().isInterrupted()){
			
			logger.info("{"+Thread.currentThread().getName()+"} Employee : "+session.get(Employee.class, 1l));
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}};
	}
	
	public static Runnable readerWorker3(){
		final Session session = sessionFactory.openSession();
		return () -> {for(int i = 0; i < 50; i++){
			Employee emp = (Employee) session.get(Employee.class, 1l);
			logger.info("{"+Thread.currentThread().getName()+"} Employee : "+emp);
			if(i == 15){
				logger.info("{"+Thread.currentThread().getName()+"} Refreshing employee...");
//				session.refresh(emp);
				session.clear();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}};
	}
	
	public static Runnable writerWorker(){
		return () -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Session session = sessionFactory.openSession();
			Employee emp = (Employee) session.get(Employee.class, 1l);
//			System.out.println(((Value)sessionFactory.getStatistics().getSecondLevelCacheStatistics("employee").getEntries().get(1l)).getValue());
			logger.info("{"+Thread.currentThread().getName()+"} Employee : "+emp);
			logger.info("{"+Thread.currentThread().getName()+"} modifying emp object");
			emp.setName(emp.getName()+" modified...");
			session.beginTransaction();
			session.update(emp);
			session.getTransaction().commit();
//			System.out.println(((Value)sessionFactory.getStatistics().getSecondLevelCacheStatistics("employee").getEntries().get(1l)).getValue());
			logger.info("{"+Thread.currentThread().getName()+"} Employee : "+session.get(Employee.class, 1l));
			session.close();
		};
	}
}
