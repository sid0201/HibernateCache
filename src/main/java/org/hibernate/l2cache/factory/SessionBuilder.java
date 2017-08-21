package org.hibernate.l2cache.factory;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.l2cache.domain.Address;
import org.hibernate.l2cache.domain.Employee;
import org.hibernate.l2cache.domain.Region;

import com.hazelcast.core.Hazelcast;

/**
 * A SessionFactory is set up once for an application!
 * 
 * */
public class SessionBuilder {

	public static SessionFactory sessionFactory;

	static{
		Hazelcast.newHazelcastInstance();
		setUp();
		Address add = new Address("Road 1", "Delhi");
		Employee emp = new Employee("Newer 1", 'M', 10000, add, new Region("US West", "US West Head "), new Region("US East", "US East Head"));
		Session session = sessionFactory.openSession();
		session.beginTransaction();
//		session.save(add);
		session.save(emp);
		System.out.println("Save : "+emp);
		session.getTransaction().commit();	
	}
	
	public static void setUp() {

		Configuration configuration = new Configuration();
		configuration.configure();
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.applySettings(configuration.getProperties()).build();
		try {
			sessionFactory = configuration.buildSessionFactory(registry);
		} catch (Exception e) {
			// The registry would be destroyed by the SessionFactory, but we had
			// trouble building the SessionFactory
			// so destroy it manually.
			e.printStackTrace();
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}


}
