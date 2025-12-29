package model;

import java.util.*;

public class CartItems {
	private Product product;
	private int quantity;

	public CartItems(Product product, int quantity) {
		this.product = product;
		this.quantity = quantity;
	}

	public double getTotalPrice() {
		return product.getPrice() * quantity;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
