package org.hibernate.l2cache.test;

import static org.hibernate.l2cache.factory.SessionBuilder.sessionFactory;

import java.util.Set;

import org.hibernate.Session;
import org.hibernate.l2cache.domain.Employee;
import org.hibernate.l2cache.domain.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCachingCollection {
	private static Logger logger = LoggerFactory.getLogger(TestCachingCollection.class);

	public static Runnable readerWorker(){
		return () -> {
			Session session = sessionFactory.openSession();
			for(int i = 1; i <= 5; i++){
				Employee emp = (Employee) session.get(Employee.class, 1l);
				logger.info("{"+Thread.currentThread().getName()+"} "+i+". emp.getRegions : "+emp.getRegions());
				logger.info("{"+Thread.currentThread().getName()+"} "+i+". Get Region : "+session.get(Region.class, 1l));
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
			logger.info("{"
					+ Thread.currentThread().getName()
					+ "} > Changing region details (with region id 1) from association owner side, i.e employee");
			
			Employee emp = (Employee) session.get(Employee.class, 1l);
			Set<Region> regions = emp.getRegions();
			for(Region r: regions){
				if(r.getId() == 1l){
					r.setRegionHead(r.getRegionHead()+" changed...");
				}
			}
			
			session.getTransaction().begin();
			session.update(emp);
			session.getTransaction().commit();
			
			logger.info("{"+Thread.currentThread().getName()+"} emp.getRegions : "+((Employee)session.get(Employee.class, 1l)).getRegions());
			session.close();

		};
	}
	
}
