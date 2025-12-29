package model;

import java.util.Date;

public class User {
	private int userId;
	private String username;
	private String email;
	private String passwordHash;
	private String firstName;
	private String lastName;
	private String phone;
	private String address;
	private String role;
	private Date createdAt;
	private boolean active;

	
	public User() {
	}

	public User(String username, String email, String passwordHash, String role) {
		this.username = username;
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
		this.active = true;
		this.createdAt = new Date();
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getFullName() {
		if (firstName == null && lastName == null)
			return username;
		if (lastName == null)
			return firstName;
		if (firstName == null)
			return lastName;
		return firstName + " " + lastName;
	}

	public boolean isAdmin() {
		return "ADMIN".equals(role);
	}
}