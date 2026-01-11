<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>
        <c:choose>
            <c:when test="${not empty product}">Edit Product</c:when>
            <c:otherwise>Add New Product</c:otherwise>
        </c:choose>
        - Admin
    </title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3>
                <i class="fas ${not empty product ? 'fa-edit' : 'fa-plus-circle'} me-2"></i>
                <c:choose>
                    <c:when test="${not empty product}">Edit Product</c:when>
                    <c:otherwise>Add New Product</c:otherwise>
                </c:choose>
            </h3>
            <a href="${contextPath}/admin/products" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-2"></i> Back to Products
            </a>
        </div>

        <div class="card">
            <div class="card-body">
                <form action="${contextPath}/admin/products" method="post">
                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                    
                    <c:if test="${not empty product}">
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" name="productId" value="${product.id}">
                    </c:if>
                    <c:if test="${empty product}">
                        <input type="hidden" name="action" value="add">
                    </c:if>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="productName" class="form-label">Product Name *</label>
                            <input type="text" class="form-control" id="productName" name="productName" 
                                   value="${product.product_name}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="categoryId" class="form-label">Category *</label>
                            <select class="form-select" id="categoryId" name="categoryId" required>
                                <option value="">Select Category</option>
                                <c:forEach var="cat" items="${allCategories}">
                                    <option value="${cat.key}" 
                                        ${product.category_id == Integer.parseInt(cat.key) ? 'selected' : ''}>
                                        ${cat.value}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="description" class="form-label">Description *</label>
                        <textarea class="form-control" id="description" name="description" 
                                  rows="3" required>${product.description}</textarea>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-3">
                            <label for="price" class="form-label">Price ($) *</label>
                            <input type="number" class="form-control" id="price" name="price" 
                                   step="0.01" min="0" value="${product.price}" required>
                        </div>
                        <div class="col-md-3">
                            <label for="stock" class="form-label">Stock Quantity *</label>
                            <input type="number" class="form-control" id="stock" name="stock" 
                                   min="0" value="${product.stock}" required>
                        </div>
                        <div class="col-md-6">
                            <label for="imageUrl" class="form-label">Image URL</label>
                            <input type="text" class="form-control" id="imageUrl" name="imageUrl" 
                                   value="${product.image_url}" 
                                   placeholder="https://example.com/image.jpg">
                            <small class="text-muted">Leave empty for default placeholder image</small>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">Image Preview</label>
                        <div class="mt-2">
                            <img id="imagePreview" 
                                 src="${not empty product.image_url ? product.image_url : 'https://via.placeholder.com/300x200?text=No+Image'}" 
                                 style="max-width: 300px; max-height: 200px; border-radius: 8px; border: 1px solid #dee2e6;"
                                 class="img-fluid">
                        </div>
                    </div>

                    <div class="d-flex justify-content-between">
                        <a href="${contextPath}/admin/products" class="btn btn-outline-secondary">
                            <i class="fas fa-times me-2"></i> Cancel
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-2"></i>
                            <c:choose>
                                <c:when test="${not empty product}">Update Product</c:when>
                                <c:otherwise>Add Product</c:otherwise>
                            </c:choose>
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script>
        // Live image preview
        document.getElementById('imageUrl').addEventListener('input', function() {
            const preview = document.getElementById('imagePreview');
            if (this.value && this.value.trim() !== '') {
                preview.src = this.value;
            } else {
                preview.src = 'https://via.placeholder.com/300x200?text=No+Image';
            }
        });
        
        // Form validation
        document.querySelector('form').addEventListener('submit', function(e) {
            const price = document.getElementById('price').value;
            const stock = document.getElementById('stock').value;
            
            if (price <= 0) {
                e.preventDefault();
                alert('Price must be greater than 0');
                return false;
            }
            
            if (stock < 0) {
                e.preventDefault();
                alert('Stock cannot be negative');
                return false;
            }
        });
    </script>
    
    <%@ include file="admin-footer.jsp" %>
</body>
</html>