<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Cart" %>
<%
    Cart cart = (Cart) session.getAttribute("cart");
    if (cart == null) {
        cart = new Cart();
        session.setAttribute("cart", cart);
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Shopping Cart - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .cart-item-img {
            width: 80px;
            height: 80px;
            object-fit: contain;
            border-radius: 8px;
            background: #fff;
            padding: 5px;
            border: 1px solid #dee2e6;
        }
        .quantity-input {
            width: 60px;
            text-align: center;
        }
        .summary-card {
            background: #f8f9fa;
            border-radius: 10px;
        }
        .empty-cart {
            max-width: 400px;
            margin: 0 auto;
        }
    </style>
</head>
<body>
     <!-- Navigation -->
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
        <h2 class="mb-4">Your Shopping Cart</h2>
        
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
        
        <c:choose>
            <c:when test="${cart != null && !cart.isEmpty()}">
                <div class="row">
                    <!-- Cart Items -->
                    <div class="col-lg-8">
                        <div class="card border-0 shadow-sm">
                            <div class="card-body">
                                <!-- Cart Items Table -->
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th style="width: 100px;">Product</th>
                                                <th>Details</th>
                                                <th class="text-center">Quantity</th>
                                                <th class="text-end">Price</th>
                                                <th class="text-end">Total</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="item" items="${cart.items}">
                                                <tr>
                                                    <td>
                                                        <img src="${item.product.image_url}" 
                                                             class="cart-item-img" 
                                                             alt="${item.product.product_name}"
                                                             onerror="this.src='https://via.placeholder.com/100'">
                                                    </td>
                                                    <td>
                                                        <h6 class="mb-1">${item.product.product_name}</h6>
                                                        <small class="text-muted">$${item.product.price} each</small>
                                                    </td>
                                                    <td class="text-center">
                                                        <div class="d-flex justify-content-center align-items-center">
                                                            <!-- Minus button form -->
                                                            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                                                                <input type="hidden" name="productId" value="${item.product.id}">
                                                                <input type="hidden" name="action" value="update">
                                                                <input type="hidden" name="quantity" value="${item.quantity - 1}">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <button type="submit" class="btn btn-sm btn-outline-secondary" 
                                                                        ${item.quantity <= 1 ? 'disabled' : ''}>
                                                                    <i class="fas fa-minus"></i>
                                                                </button>
                                                            </form>
                                                            <span class="mx-2">${item.quantity}</span>
                                                            <!-- Plus button form -->
                                                            <form action="${pageContext.request.contextPath}/cart" method="post" class="d-inline">
                                                                <input type="hidden" name="productId" value="${item.product.id}">
                                                                <input type="hidden" name="action" value="update">
                                                                <input type="hidden" name="quantity" value="${item.quantity + 1}">
                                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                                <button type="submit" class="btn btn-sm btn-outline-secondary"
                                                                        ${item.quantity >= item.product.stock ? 'disabled' : ''}>
                                                                    <i class="fas fa-plus"></i>
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </td>
                                                    <td class="text-end">$${item.product.price}</td>
                                                    <td class="text-end">$${item.totalPrice}</td>
                                                    <td>
                                                        <!-- Remove button form -->
                                                        <form action="${pageContext.request.contextPath}/cart" method="post">
                                                            <input type="hidden" name="productId" value="${item.product.id}">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                            <button type="submit" class="btn btn-danger btn-sm">
                                                                <i class="fas fa-trash"></i>
                                                            </button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Order Summary -->
                    <div class="col-lg-4">
                        <div class="card summary-card">
                            <div class="card-body">
                                <h5 class="card-title">Order Summary</h5>
                                
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Subtotal (${cart.totalItemsCount} items)</span>
                                    <span>$${cart.totalPrice}</span>
                                </div>
                                
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Shipping</span>
                                    <span>$5.00</span>
                                </div>
                                
                                <div class="d-flex justify-content-between mb-3">
                                    <span>Tax (10%)</span>
                                    <span>$${String.format("%.2f", cart.totalPrice * 0.1)}</span>
                                </div>
                                
                                <hr>
                                
                                <div class="d-flex justify-content-between mb-4">
                                    <strong>Total</strong>
                                    <strong>$${String.format("%.2f", cart.totalPrice + 5 + (cart.totalPrice * 0.1))}</strong>
                                </div>
                                
                                <c:choose>
                                    <c:when test="${sessionScope.user != null}">
                                        <!-- 🆕 FIXED: Changed to direct link with shopping bag icon -->
                                        <a href="${pageContext.request.contextPath}/checkout" 
                                           class="btn btn-success btn-lg w-100 mb-2">
                                            <i class="fas fa-shopping-bag me-2"></i> Proceed to Checkout
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Guest users need to login -->
                                        <div class="alert alert-warning mb-3">
                                            <p class="mb-2"><i class="fas fa-exclamation-triangle me-2"></i> 
                                                Please login to complete your purchase</p>
                                            <div class="d-grid gap-2">
                                                <a href="${pageContext.request.contextPath}/auth/login?redirect=${pageContext.request.contextPath}/cart" 
                                                   class="btn btn-primary">
                                                    <i class="fas fa-sign-in-alt me-2"></i> Login
                                                </a>
                                                <a href="${pageContext.request.contextPath}/auth/register" 
                                                   class="btn btn-outline-primary">
                                                    <i class="fas fa-user-plus me-2"></i> Register
                                                </a>
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                                
                                <div class="mt-3 text-center">
                                    <a href="${pageContext.request.contextPath}/product" class="text-decoration-none">
                                        <i class="fas fa-arrow-left"></i> Continue Shopping
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5">
                    <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
                    <h3>Your cart is empty</h3>
                    <p class="text-muted mb-4">Add some products to your cart</p>
                    <a href="${pageContext.request.contextPath}/product" class="btn btn-primary btn-lg">
                        <i class="fas fa-shopping-bag me-2"></i> Start Shopping
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>