package model;

import java.util.Date;
import java.util.List;

public class Order {
	private int orderId;
	private String orderNumber;
	private int userId;
	private Date orderDate;
	private double totalAmount;
	private String status; // pending,confirmed, shipped, delivered, cancelled
	private String shippingAddress;
	private String paymentMethod;
	private String paymentStatus; // pending,paid,failed

	private User user;
	private List<OrderItem> items;

	public Order() {
	}

	public Order(int userId, double totalAmount, String shippingAddress) {
		this.userId = userId;
		this.totalAmount = totalAmount;
		this.shippingAddress = shippingAddress;
		this.status = "PENDING";
		this.paymentStatus = "PENDING";
		this.orderDate = new Date();
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public void setItems(List<OrderItem> items) {
		this.items = items;
	}

	public String getFormattedOrderDate() {
		if (orderDate == null)
			return "";
		return new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm").format(orderDate);
	}

	public String getStatusColor() {
		switch (status.toUpperCase()) {
		case "PENDING":
			return "warning";
		case "CONFIRMED":
			return "info";
		case "SHIPPED":
			return "primary";
		case "DELIVERED":
			return "success";
		case "CANCELLED":
			return "danger";
		default:
			return "secondary";
		}
	}

	public String getPaymentStatusColor() {
		switch (paymentStatus.toUpperCase()) {
		case "PENDING":
			return "warning";
		case "PAID":
			return "success";
		case "FAILED":
			return "danger";
		default:
			return "secondary";
		}
	}

	public boolean canBeCancelled() {
		return status.equals("PENDING") || status.equals("CONFIRMED");
	}
}