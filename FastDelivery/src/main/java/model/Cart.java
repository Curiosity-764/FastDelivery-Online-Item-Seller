package model;

import java.util.*;
import java.util.stream.Collectors;



public class Cart {

    private List<CartItems> items;

    public Cart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        for (CartItems item : items) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        items.add(new CartItems(product, quantity));
    }

    public void removeItem(int productId) {
        items.removeIf(item -> item.getProduct().getId() == productId);
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(CartItems::getTotalPrice).sum();
    }

    public List<CartItems> searchItemsByName(String keyword) {
        if (keyword == null || keyword.isEmpty()) return items;
        String lower = keyword.toLowerCase();
        return items.stream()
                .filter(item -> item.getProduct().getProduct_name().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public List<CartItems> getItems() {
        return items;
    }

    public void clear() {
        items.clear();
    }
    
    public void updateQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }
        
        for (CartItems item : items) {
            if (item.getProduct().getId() == productId) {
                item.setQuantity(newQuantity);
                return;
            }
        }
    }
    
    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int getTotalItemsCount() {
        return items.stream().mapToInt(CartItems::getQuantity).sum();
    }

    public int size() {
        return items.size();
    }
}

	

