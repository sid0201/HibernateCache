package org.hibernate.l2cache.test;

import static org.hibernate.l2cache.factory.SessionBuilder.sessionFactory;

import org.hibernate.Session;
import org.hibernate.l2cache.domain.Address;
import org.hibernate.l2cache.domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCachingAssociation {
	private static Logger logger = LoggerFactory.getLogger(TestCachingAssociation.class);

	public static Runnable readerWorker(){
		return () -> {
			Session session = sessionFactory.openSession();
			for(int i = 1; i <= 5; i++){
				Employee emp = (Employee) session.get(Employee.class, 1l);
				logger.info("{"+Thread.currentThread().getName()+"} "+i+". emp.getAddress : "+emp.getAddress());
				logger.info("{"+Thread.currentThread().getName()+"} "+i+". Get Address : "+session.get(Address.class, 1l));
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(i == 4){
					logger.info("{"+Thread.currentThread().getName()+"} clearing all session objects... ");
					session.clear();
				}
			}
			session.close();
		};
	}
	
	public static Runnable writerWorker() {
		return () -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Session session = sessionFactory.openSession();
			Employee emp = (Employee) session.get(Employee.class, 1l);
			Address address = emp.getAddress();
			logger.info("{"
					+ Thread.currentThread().getName()
					+ "} > Changing address details from association owner side, i.e employee");
			session.getTransaction().begin();
			address.setRoad(address.getRoad() + " changed...");
//			emp.setName(emp.getName()+" changed...");
//			session.update(emp);
			session.update(address);
			session.getTransaction().commit();
//			logger.info("{"+Thread.currentThread().getName()+"} Employee : "+session.get(Employee.class, 1l));
			logger.info("{"+Thread.currentThread().getName()+"} Address : "+session.get(Address.class, 1l));
			session.close();

		};
	}
}
