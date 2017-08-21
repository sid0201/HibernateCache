package org.hibernate.l2cache.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name="region")
public class Region {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
	@GenericGenerator(name = "native", strategy = "native")
	private Long id;
	@NaturalId(mutable=true)
	private String regionName;
	private String regionHead;
	
	public Region(){}
	
	public Region(String regionName, String regionHead) {
		this.regionName = regionName;
		this.regionHead = regionHead;
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	public String getRegionName() {
		return regionName;
	}

	public Region setRegionName(String regionName) {
		this.regionName = regionName;
		return this;
	}

	public String getRegionHead() {
		return regionHead;
	}

	public Region setRegionHead(String regionHead) {
		this.regionHead = regionHead;
		return this;
	}

	@Override
	public String toString() {
		return "Region [id=" + id + ", regionName=" + regionName
				+ ", regionHead=" + regionHead + "]";
	}
	
	
}
