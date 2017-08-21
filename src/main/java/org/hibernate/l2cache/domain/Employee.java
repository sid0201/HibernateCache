package org.hibernate.l2cache.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "employee")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="employee")
public class Employee implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private long empId;
	private String name;
	private char gender;
	private double salary;
	@ManyToMany(cascade= CascadeType.ALL, fetch= FetchType.LAZY)
	@JoinTable(
			joinColumns= @JoinColumn(name="emp_id",  referencedColumnName="empId"), 
			inverseJoinColumns= @JoinColumn(name="region_id",  referencedColumnName="id")
			)
	private Set<Region> regions;
	@OneToOne(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="address_id")
	private Address address;

	public Employee() {
	}

	public Employee(String name, char gender, double salary, Address address, Region... regions) {
		this.name = name;
		this.gender = gender;
		this.salary = salary;
		this.address = address;
		this.regions = Stream.of(regions).filter(r -> r!= null).collect(Collectors.toSet());
	}

	public long getEmpId() {
		return empId;
	}

	@SuppressWarnings("unused")
	private void setEmpId(long empId) {
		this.empId = empId;
	}

	public char getGender() {
		return gender;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	
	public Set<Region> getRegions() {
		return regions;
	}

	public void setRegions(Set<Region> regions) {
		this.regions = regions;
	}

	@Override
	public String toString() {
		return "Employee [empId=" + empId + ", name=" + name + ", gender="
				+ gender + ", salary=" + salary + "]";
	}

}
