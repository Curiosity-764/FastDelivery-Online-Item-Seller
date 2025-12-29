package model;

public class Product {
	private int id;
	private String product_name;
	private String description;
	private double price;
	private int stock;
	private String image_url;
	private int category_id;

	public Product() {
	}

	public Product(int id, String product_name, String description, double price, int stock, String image_url,
			int category_id) {
		this.id = id;
		this.product_name = product_name;
		this.description = description;
		this.price = price;
		this.stock = stock;
		this.image_url = image_url;
		this.category_id = category_id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}
}
