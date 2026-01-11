<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
        /* Pagination styles */
        .pagination .page-item.active .page-link {
            background-color: #0d6efd;
            border-color: #0d6efd;
        }
        .page-size-select {
            width: auto !important;
            display: inline-block !important;
        }
        .pagination-info {
            color: #6c757d;
            font-size: 0.9rem;
        }
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
        
        <div class="d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary position-relative me-3">
                <i class="fas fa-shopping-cart"></i>
                <c:if test="${sessionScope.cart != null && !sessionScope.cart.isEmpty()}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        ${sessionScope.cart.totalItemsCount}
                    </span>
                </c:if>
            </a>
            
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <div class="dropdown">
                        <button class="btn btn-outline-secondary dropdown-toggle" type="button" 
                                data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user me-1"></i> ${sessionScope.user.username}
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/profile">
                                <i class="fas fa-user-circle me-2"></i> Profile
                            </a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders">
                                <i class="fas fa-box me-2"></i> My Orders
                            </a></li>
                            <!-- Add Admin Link for Admin Users -->
                            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">
                                    <i class="fas fa-cogs me-2"></i> Admin Panel
                                </a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/logout">
                                <i class="fas fa-sign-out-alt me-2"></i> Logout
                            </a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline-primary">
                            <i class="fas fa-sign-in-alt me-1"></i> Login
                        </a>
                        <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">
                            <i class="fas fa-user-plus me-1"></i> Register
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
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
                           href="${pageContext.request.contextPath}/product?sort=name${not empty queryParams ? queryParams : ''}">Name (A-Z)</a></li>
                    <li><a class="dropdown-item ${currentSort == 'price' ? 'active' : ''}" 
                           href="${pageContext.request.contextPath}/product?sort=price${not empty queryParams ? queryParams : ''}">Price (Low to High)</a></li>
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
                <!-- Pagination Info (Top) -->
                <div class="row mb-4">
                    <div class="col-md-8">
                        <div class="pagination-info">
                            <c:if test="${totalProducts > 0}">
                                Showing 
                                <strong>${startIndex} to ${endIndex}</strong> 
                                of <strong>${totalProducts}</strong> products
                                <c:if test="${not empty currentSearch}">
                                    for "<strong><c:out value='${currentSearch}' /></strong>"
                                </c:if>
                                <c:if test="${not empty currentCategory}">
                                    in <strong>${allCategories[currentCategory]}</strong>
                                </c:if>
                            </c:if>
                        </div>
                    </div>
                    <div class="col-md-4 text-end">
                        <div class="d-flex align-items-center justify-content-end gap-2">
                            <span class="pagination-info">Items per page:</span>
                            <select class="form-select form-select-sm page-size-select" id="pageSizeSelect" style="width: auto;">
                                <option value="6" ${pageSize == 6 ? 'selected' : ''}>6</option>
                                <option value="12" ${pageSize == 12 ? 'selected' : ''}>12</option>
                                <option value="24" ${pageSize == 24 ? 'selected' : ''}>24</option>
                                <option value="48" ${pageSize == 48 ? 'selected' : ''}>48</option>
                            </select>
                        </div>
                    </div>
                </div>
                
                <!-- Products Grid -->
                <div class="row">
                    <c:forEach var="product" items="${products}">
                        <div class="col-md-4 col-lg-3 mb-4">
                            <div class="card product-card">
                                <img src="${product.image_url}" 
                                     class="card-img-top product-img" 
                                     alt="${product.product_name}"
                                     onerror="this.src='https://via.placeholder.com/300x200?text=No+Image'">
                                
                                <div class="card-body">
                                    <!-- FIXED CATEGORY DISPLAY -->
                                    <span class="category-badge mb-2">
                                        <c:forEach var="cat" items="${allCategories}">
                                            <c:if test="${cat.key == product.category_id.toString()}">
                                                ${cat.value}
                                            </c:if>
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
                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                            <button type="submit" class="btn btn-primary w-100" 
                                                    ${product.stock <= 0 ? 'disabled' : ''}>
                                                <i class="fas fa-cart-plus me-2"></i> Add to Cart
                                            </button>
                                        </form>
                                        
                                        <!-- View Details Button (triggers POST to track view) -->
                                        <form action="${pageContext.request.contextPath}/product" method="post">
                                            <input type="hidden" name="viewProductId" value="${product.id}">
                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
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
                
                <!-- Pagination Controls (Bottom) -->
                <c:if test="${totalPages > 1}">
                    <nav aria-label="Product pagination" class="mt-4">
                        <ul class="pagination justify-content-center">
                            <!-- First Page -->
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="product?page=1${queryParams}" aria-label="First">
                                    <i class="fas fa-angle-double-left"></i>
                                </a>
                            </li>
                            
                            <!-- Previous Page -->
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="product?page=${currentPage - 1}${queryParams}" aria-label="Previous">
                                    <i class="fas fa-angle-left"></i>
                                </a>
                            </li>
                            
                            <!-- Page Numbers -->
                            <c:choose>
                                <c:when test="${totalPages <= 7}">
                                    <c:forEach begin="1" end="${totalPages}" var="i">
                                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                                            <a class="page-link" href="product?page=${i}${queryParams}">${i}</a>
                                        </li>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <!-- Always show first page -->
                                    <li class="page-item ${currentPage == 1 ? 'active' : ''}">
                                        <a class="page-link" href="product?page=1${queryParams}">1</a>
                                    </li>
                                    
                                    <c:choose>
                                        <c:when test="${currentPage <= 4}">
                                            <!-- Show 2, 3, 4, 5 -->
                                            <c:forEach begin="2" end="5" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="product?page=${i}${queryParams}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:when>
                                        <c:when test="${currentPage >= totalPages - 3}">
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                            <c:forEach begin="${totalPages - 4}" end="${totalPages - 1}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="product?page=${i}${queryParams}">${i}</a>
                                                </li>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                            <c:forEach begin="${currentPage - 1}" end="${currentPage + 1}" var="i">
                                                <li class="page-item ${currentPage == i ? 'active' : ''}">
                                                    <a class="page-link" href="product?page=${i}${queryParams}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item disabled">
                                                <span class="page-link">...</span>
                                            </li>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <!-- Always show last page -->
                                    <li class="page-item ${currentPage == totalPages ? 'active' : ''}">
                                        <a class="page-link" href="product?page=${totalPages}${queryParams}">${totalPages}</a>
                                    </li>
                                </c:otherwise>
                            </c:choose>
                            
                            <!-- Next Page -->
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="product?page=${currentPage + 1}${queryParams}" aria-label="Next">
                                    <i class="fas fa-angle-right"></i>
                                </a>
                            </li>
                            
                            <!-- Last Page -->
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="product?page=${totalPages}${queryParams}" aria-label="Last">
                                    <i class="fas fa-angle-double-right"></i>
                                </a>
                            </li>
                        </ul>
                    </nav>
                    
                    <!-- Jump to Page Form -->
                    <div class="row mt-3">
                        <div class="col-md-6 offset-md-3">
                            <div class="card">
                                <div class="card-body py-2">
                                    <form class="row g-2 align-items-center" action="product" method="get">
                                        <input type="hidden" name="category" value="${currentCategory}">
                                        <input type="hidden" name="search" value="${currentSearch}">
                                        <input type="hidden" name="sort" value="${currentSort}">
                                        <input type="hidden" name="size" value="${pageSize}">
                                        
                                        <div class="col-auto">
                                            <label class="col-form-label">Jump to page:</label>
                                        </div>
                                        <div class="col">
                                            <input type="number" class="form-control form-control-sm" 
                                                   name="page" min="1" max="${totalPages}" 
                                                   placeholder="Page" value="${currentPage}">
                                        </div>
                                        <div class="col-auto">
                                            <button class="btn btn-primary btn-sm" type="submit">
                                                <i class="fas fa-arrow-right"></i>
                                            </button>
                                        </div>
                                        <div class="col-auto">
                                            <span class="text-muted small">of ${totalPages}</span>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:if>
                
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
    
    <!-- JavaScript for page size change -->
    <script>
    document.getElementById('pageSizeSelect').addEventListener('change', function() {
        const pageSize = this.value;
        const url = new URL(window.location.href);
        
        // Update or add size parameter
        url.searchParams.set('size', pageSize);
        
        // Reset to page 1 when changing page size
        url.searchParams.delete('page');
        
        // Redirect to new URL
        window.location.href = url.toString();
    });
    
    // Update sort links to include current pagination
    document.addEventListener('DOMContentLoaded', function() {
        const sortLinks = document.querySelectorAll('.dropdown-menu a[href*="sort="]');
        sortLinks.forEach(link => {
            const href = link.getAttribute('href');
            const queryParams = '${queryParams}';
            
            // Remove existing page parameter from queryParams if present
            let cleanParams = queryParams.replace(/&page=\d+/g, '');
            
            // Add cleaned params to sort link
            link.setAttribute('href', href + cleanParams);
        });
    });
    </script>
</body>
</html>