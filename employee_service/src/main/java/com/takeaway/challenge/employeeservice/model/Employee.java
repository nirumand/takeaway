package com.takeaway.challenge.employeeservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.validator.constraints.Email;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
public class Employee {

	@JsonProperty
	@Id
	private UUID employeeId;

	@Email
	@JsonProperty
	@Column(unique = true)
	private String email;

	@JsonProperty
	@Column(name = "full_name")
	private String fullName;

	@JsonProperty
	private Date birthday;

	@JsonProperty
	@ElementCollection
	private List<String> hobbies;

	public Employee() {
		this.employeeId = UUID.randomUUID();
	}

	public Employee(String email, String fullName, Date birthday, List<String> hobbies) {
		this();
		this.email = email;
		this.fullName = fullName;
		this.birthday = birthday;
		this.hobbies = hobbies;
	}

	public UUID getEmployeeId() {
		return this.employeeId;
	}


	public String getEmail() {
		return email;
	}

	public Employee setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getFullName() {
		return fullName;
	}

	public Employee setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public Date getBirthday() {
		return birthday;
	}

	public Employee setBirthday(Date birthday) {
		this.birthday = birthday;
		return this;
	}

	public List<String> getHobbies() {
		return hobbies;
	}

	public Employee setHobbies(List<String> hobbies) {
		this.hobbies = hobbies;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Employee employee = (Employee) o;

		return employeeId.equals(employee.employeeId);
	}

	@Override
	public int hashCode() {
		return employeeId.hashCode();
	}

	@Override
	public String toString() {
		Gson gson = new GsonBuilder()
				.setDateFormat("yyyy-MM-dd")
				.create();
		return gson.toJson(this);
	}
}
