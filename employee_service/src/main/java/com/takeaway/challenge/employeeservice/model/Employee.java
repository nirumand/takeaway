package com.takeaway.challenge.employeeservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Employee {

	@JsonProperty
	@Id
	private UUID uuid;

	@Email
	@JsonProperty
	private String email;

	@JsonProperty
	@Column(name = "full_name")
	private String fullName;

	@JsonProperty
	private String birthday;

	@JsonProperty
	@ElementCollection
	private List<String> hobbies;

	public Employee() {
		this.uuid = UUID.randomUUID();
	}

	public Employee(String email, String fullName, String birthday, List<String> hobbies) {
		this();
		this.email = email;
		this.fullName = fullName;
		this.birthday = birthday;
		this.hobbies = hobbies;
	}

	public String getUuid() {
		return uuid.toString();
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

	public String getBirthday() {
		return birthday;
	}

	public Employee setBirthday(String birthday) {
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

		return uuid.equals(employee.uuid);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
