package org.hibernate.l2cache.test;

import static org.hibernate.l2cache.factory.SessionBuilder.sessionFactory;

import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.l2cache.domain.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCachingQuery {
	private static Logger logger = LoggerFactory.getLogger(TestCachingQuery.class);
	
	public static Runnable queryWithOutCache(){
		return () -> {
			Session session = sessionFactory.openSession();
			logger.info("{"+Thread.currentThread().getName()+ "} Get by id -> Region : ");
			logger.info("{"+Thread.currentThread().getName()+"} "+session.get(Region.class, 1l));
			logger.info("{"+Thread.currentThread().getName()+ "} Get by query -> Region : ");
			@SuppressWarnings({"unchecked" })
			List<Region> regions = session
					.createQuery("from Region where id = :id")
					.setParameter("id", 1l)
					.list(); 
			logger.info("{"+Thread.currentThread().getName()+"} "+regions);
			session.close();
		};
	}
	
	public static Runnable readerWithNaturalId1(){
		return () -> {
			Session session = sessionFactory.openSession();
			logger.info("{"+Thread.currentThread().getName()+ "} Get by id -> Region : ");
			logger.info("{"+Thread.currentThread().getName()+"} "+session.get(Region.class, 1l));
			logger.info("{"+Thread.currentThread().getName()+ "} Get by naturalId -> Region : ");
			logger.info("{"+Thread.currentThread().getName()+"} "+session.byNaturalId(Region.class).using("regionName", "US West").load());
			logger.info("{"+Thread.currentThread().getName()+ "} Clearing all session objects...");
			session.clear();
			logger.info("{"+Thread.currentThread().getName()+ "} Get by naturalId -> Region : ");
			logger.info("{"+Thread.currentThread().getName()+"} "+session.byNaturalId(Region.class).using("regionName", "US West").load());
			session.close();
		};
	}
	
	public static Runnable readerWithNaturalId2(){
		return () -> {
			Session session = sessionFactory.openSession();
			Region region = null;
			for(int i = 1; i <= 5; i++){
				logger.info("{"+Thread.currentThread().getName()+ "} Get by naturalId -> Region : ");
				region= (Region) session.bySimpleNaturalId(Region.class).load("US West");
				if(region == null){
					logger.info("{"+Thread.currentThread().getName()+ "} Data has been changed by worker");
					logger.info("{"
							+ Thread.currentThread().getName()
							+ "} new value "
							+ session.bySimpleNaturalId(Region.class).load(
									"US West modified..."));
					/*logger.info("{"
							+ Thread.currentThread().getName()
							+ "} new value "
							+ session.bySimpleNaturalId(Region.class)
									.setSynchronizationEnabled(false)
									.load("US West modified..."));*/
					break;
				}
				logger.info("{"+Thread.currentThread().getName()+"} "+region);
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
	
	public static Runnable writerWorker(){
		return () -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Session session = sessionFactory.openSession();
			Region region= (Region) session.bySimpleNaturalId(Region.class).load("US West");
			logger.info("{"+Thread.currentThread().getName()+"} Region : "+region);
			logger.info("{"+Thread.currentThread().getName()+"} modifying region...");
			region.setRegionName(region.getRegionName()+" modified...");
			session.beginTransaction();
			session.update(region);
			session.getTransaction().commit();
			logger.info("{"+Thread.currentThread().getName()+"} Region : "+session.get(Region.class, 1l));
			session.close();
		};
	}
	

	@SuppressWarnings("unchecked")
	public static Runnable queryCache(){
		return () -> {
			Session session = sessionFactory.openSession();
			logger.info("{"+Thread.currentThread().getName()+ "} Get by query -> Region : ");
			List<Region> regions = session
					.createQuery("from Region where id = :id")
					.setParameter("id", 1l)
					.setCacheable(true)
					.setCacheRegion( "query.cache.region" )
//					.setCacheMode( CacheMode.REFRESH )
					.list(); 
			logger.info("{"+Thread.currentThread().getName()+"} "+regions);
			logger.info("{"+Thread.currentThread().getName()+ "} Get by query -> Region : ");
			regions = session
					.createQuery("from Region where id = :id")
					.setParameter("id", 1l)
					.setCacheable(true)
					.setCacheRegion( "query.cache.region" )
//					.setCacheMode( CacheMode.REFRESH )
					.list(); 
			logger.info("{"+Thread.currentThread().getName()+"} "+regions);
			logger.info("{"+Thread.currentThread().getName()+ "} Get by query -> Region : ");
			regions = session
					.createQuery("from Region where id = :id")
					.setParameter("id", 1l)
					.setCacheable(true)
					.setCacheRegion( "query.cache.region" )
					.setCacheMode( CacheMode.REFRESH )
					.list(); 
			logger.info("{"+Thread.currentThread().getName()+"} "+regions);
			session.close();
		};
	}
}
