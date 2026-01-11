<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Products - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <!-- Header with Add Button -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h3><i class="fas fa-box me-2"></i> Product Management</h3>
            <p class="text-muted">Add, edit, or remove products from your store</p>
        </div>
        <a href="${contextPath}/admin/products?action=add" class="btn btn-success">
            <i class="fas fa-plus-circle me-2"></i> Add New Product
        </a>
    </div>

    <!-- Check if we're in Add/Edit mode -->
    <c:if test="${param.action == 'add' or param.action == 'edit'}">
        <%@ include file="product-form.jsp" %>
    </c:if>
    
    <!-- Product List Table -->
    <c:if test="${empty param.action}">
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Image</th>
                                <th>Product Name</th>
                                <th>Category</th>
                                <th>Price</th>
                                <th>Stock</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="product" items="${products}">
                                <tr>
                                    <td>${product.id}</td>
                                    <td>
                                        <img src="${product.image_url}" 
                                             style="width: 60px; height: 60px; object-fit: contain; border-radius: 5px;"
                                             onerror="this.src='https://via.placeholder.com/60?text=No+Image'"
                                             alt="${product.product_name}">
                                    </td>
                                    <td>
                                        <strong>${product.product_name}</strong>
                                        <div class="text-muted small" style="max-width: 200px; overflow: hidden; text-overflow: ellipsis;">
                                            ${product.description}
                                        </div>
                                    </td>
                                    <td>
                                        <c:forEach var="cat" items="${allCategories}">
                                            <c:if test="${cat.key == product.category_id.toString()}">
                                                <span class="badge bg-secondary">${cat.value}</span>
                                            </c:if>
                                        </c:forEach>
                                    </td>
                                    <td>$${product.price}</td>
                                    <td>
                                        <span class="badge ${product.stock > 10 ? 'bg-success' : 
                                                          product.stock > 0 ? 'bg-warning' : 'bg-danger'}">
                                            ${product.stock}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <a href="${contextPath}/admin/products?action=edit&id=${product.id}" 
                                               class="btn btn-outline-primary">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <form action="${contextPath}/admin/products" method="post" 
                                                  onsubmit="return confirm('Delete product: ${product.product_name}?')"
                                                  class="d-inline">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="productId" value="${product.id}">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                <button type="submit" class="btn btn-outline-danger">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <c:if test="${empty products}">
                    <div class="text-center py-5">
                        <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
                        <h5>No Products Found</h5>
                        <p class="text-muted">Add your first product to get started</p>
                        <a href="${contextPath}/admin/products?action=add" class="btn btn-primary">
                            <i class="fas fa-plus-circle me-2"></i> Add Product
                        </a>
                    </div>
                </c:if>
            </div>
        </div>
    </c:if>
    
    <%@ include file="admin-footer.jsp" %>
</body>
</html>