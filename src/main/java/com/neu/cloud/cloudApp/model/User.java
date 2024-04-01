package com.neu.cloud.cloudApp.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

	//@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	//private int id;

	@Id
	@GeneratedValue(generator = "UUID")
	@Column(columnDefinition = "BINARY(16)")
	private UUID uuid;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "username", unique = true)
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "account_created")
	private Date accountCreated;

	@Column(name = "account_updated")
	private Date accountUpdated;

	@Column(name = "is_verified")
	private boolean isVerified;

	public boolean isVerified() {
		return isVerified;
	}

	public void setVerified(boolean verified) {
		isVerified = verified;
	}

	public UUID getId() {
		return uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getAccountCreated() {
		return accountCreated;
	}

	public void setAccountCreated(Date accountCreated) {
		this.accountCreated = accountCreated;
	}

	public Date getAccountUpdated() {
		return accountUpdated;
	}

	public void setAccountUpdated(Date accountUpdated) {
		this.accountUpdated = accountUpdated;
	}

	@Override
	public String toString() {
		return "User [id=" + uuid + ", firstName=" + firstName + ", lastName=" + lastName + ", username=" + username
				+ ", password=" + password + ", accountCreated=" + accountCreated + ", accountUpdated=" + accountUpdated
				+ "]";
	}

	@PrePersist
	private void ensureId() {
		this.setId(UUID.randomUUID());
	}
}
