<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>


<head>
    <title>${title} - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { background-color: #f8f9fa; }
        .product-card { 
            border: none; 
            border-radius: 10px;
            overflow: hidden;
            transition: all 0.3s ease;
            height: 100%;
        }
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 20px rgba(0,0,0,0.15);
        }
        .product-img {
            height: 200px;
            object-fit: contain;
            background: #fff;
            padding: 15px;
        }
        .category-badge {
            background: #e9ecef;
            color: #495057;
            padding: 3px 10px;
            border-radius: 15px;
            font-size: 0.8rem;
        }
        .price-tag {
            color: #dc3545;
            font-weight: 700;
            font-size: 1.2rem;
        }
        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary position-relative me-3">
    <i class="fas fa-shopping-cart"></i>
    <c:if test="${sessionScope.cart != null && !sessionScope.cart.isEmpty()}">
        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
            ${sessionScope.cart.totalItemsCount}
        </span>
    </c:if>
</a>
    </style>
    
</head>
<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
        <div class="container">
            <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/">
                <i class="fas fa-bolt"></i> Fast Delivery
            </a>
            
            <form class="d-flex mx-auto" action="${pageContext.request.contextPath}/product" method="get" style="max-width: 400px;">
                <div class="input-group">
                    <input type="text" class="form-control" name="search" placeholder="Search products..." 
                           value="${currentSearch}">
                    <button class="btn btn-outline-primary" type="submit">
                        <i class="fas fa-search"></i>
                    </button>
                </div>
            </form>
            
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary position-relative me-3">
                <i class="fas fa-shopping-cart"></i>
                <c:if test="${sessionScope.cart != null && !sessionScope.cart.isEmpty()}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        ${sessionScope.cart.totalItemsCount}
                    </span>
                </c:if>
            </a>
            
            <c:if test="${sessionScope.user != null}">
                <span class="navbar-text">Welcome, ${sessionScope.user.username}</span>
            </c:if>
        </div>
    </nav>

    <div class="container py-4">
        <!-- Page Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2>${title}</h2>
            
            <div class="dropdown">
                <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                    <i class="fas fa-sort"></i> Sort
                </button>
                <ul class="dropdown-menu">
                    <li><a class="dropdown-item ${currentSort == 'name' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/product?sort=name">Name (A-Z)</a></li>
                    <li><a class="dropdown-item ${currentSort == 'price' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/product?sort=price">Price (Low to High)</a></li>
                </ul>
            </div>
        </div>
        
        <!-- Categories Filter -->
        <div class="mb-4">
            <div class="d-flex flex-wrap gap-2">
                <a href="${pageContext.request.contextPath}/product" 
                   class="btn ${empty currentCategory ? 'btn-primary' : 'btn-outline-primary'}">
                    All Products
                </a>
                <c:forEach var="cat" items="${allCategories}">
                    <a href="${pageContext.request.contextPath}/product?category=${cat.key}" 
                       class="btn ${currentCategory == cat.key ? 'btn-primary' : 'btn-outline-primary'}">
                        ${cat.value}
                    </a>
                </c:forEach>
            </div>
        </div>
        
        <!-- Flash Messages -->
        <c:if test="${not empty sessionScope.flashMessage}">
            <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
                ${sessionScope.flashMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="flashMessage" scope="session"/>
        </c:if>
        
        <c:if test="${not empty sessionScope.flashError}">
            <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                ${sessionScope.flashError}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="flashError" scope="session"/>
        </c:if>
        
        <!-- Products Grid -->
        <c:choose>
            <c:when test="${not empty products}">
                <div class="row">
                    <c:forEach var="product" items="${products}">
                        <div class="col-md-4 col-lg-3 mb-4">
                            <div class="card product-card">
                                <img src="${product.image_url}" 
                                     class="card-img-top product-img" 
                                     alt="${product.product_name}"
                                     onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                                
                                <div class="card-body">
                                    <span class="category-badge mb-2">
                                        <c:forEach var="cat" items="${allCategories}">
                                            <c:if test="${cat.key == product.category_id.toString()}">${cat.value}</c:if>
                                        </c:forEach>
                                    </span>
                                    
                                    <h5 class="card-title" style="font-size: 1rem; min-height: 2.5rem;">
                                        ${product.product_name}
                                    </h5>
                                    
                                    <p class="card-text text-muted small mb-3">
                                        <c:choose>
                                            <c:when test="${product.description.length() > 80}">
                                                ${product.description.substring(0, 80)}...
                                            </c:when>
                                            <c:otherwise>
                                                ${product.description}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>
                                    
                                    <div class="d-flex justify-content-between align-items-center mb-3">
                                        <span class="price-tag">$${product.price}</span>
                                        <span class="badge ${product.stock > 0 ? 'bg-success' : 'bg-danger'}">
                                            ${product.stock > 0 ? 'In Stock' : 'Out of Stock'}
                                        </span>
                                    </div>
                                    
                                    <div class="d-grid gap-2">
                                        <!-- Add to Cart Form -->
                                        <form action="${pageContext.request.contextPath}/cart" method="post">
                                            <input type="hidden" name="productId" value="${product.id}">
                                            <button type="submit" class="btn btn-primary w-100" 
                                                    ${product.stock <= 0 ? 'disabled' : ''}>
                                                <i class="fas fa-cart-plus me-2"></i> Add to Cart
                                            </button>
                                        </form>
                                        
                                        <!-- View Details Button (triggers POST to track view) -->
                                        <form action="${pageContext.request.contextPath}/product" method="post">
                                            <input type="hidden" name="viewProductId" value="${product.id}">
                                            <button type="submit" class="btn btn-outline-secondary w-100">
                                                <i class="fas fa-eye me-2"></i> View Details
                                            </button>
                                        </form>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-search fa-4x text-muted mb-3"></i>
                    <h3>No Products Found</h3>
                    <p class="text-muted">${message}</p>
                    <a href="${pageContext.request.contextPath}/product" class="btn btn-primary">
                        <i class="fas fa-store me-2"></i> Browse All Products
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4 mt-5">
        <div class="container text-center">
            <p>&copy; 2024 Fast Delivery System. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>