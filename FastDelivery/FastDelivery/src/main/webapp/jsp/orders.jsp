<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Orders - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .order-card {
            border-radius: 10px;
            transition: all 0.3s ease;
            border: 1px solid #dee2e6;
        }
        .order-card:hover {
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        .status-badge {
            font-size: 0.85rem;
            padding: 5px 15px;
            border-radius: 20px;
        }
        .order-summary {
            background: #f8f9fa;
            border-radius: 10px;
            padding: 20px;
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
        <h2 class="mb-4">
            <i class="fas fa-box-open me-2"></i> My Orders
        </h2>
        
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
        
        <!-- Track Order Section -->
        <div class="card mb-4">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <h5 class="mb-1"><i class="fas fa-search me-2"></i> Track an Order</h5>
                        <p class="text-muted mb-0">Enter your order number to track your package</p>
                    </div>
                    <div class="col-md-4">
                        <a href="${pageContext.request.contextPath}/orders/track" class="btn btn-primary w-100">
                            <i class="fas fa-truck me-2"></i> Track Order
                        </a>
                    </div>
                </div>
            </div>
        </div>
        
        <c:choose>
            <c:when test="${not empty orders && !orders.isEmpty()}">
                <!-- Orders Count -->
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h5>${orders.size()} Order(s) Found</h5>
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-primary btn-sm">All</a>
                        <a href="#" class="btn btn-outline-primary btn-sm">Pending</a>
                        <a href="#" class="btn btn-outline-primary btn-sm">Delivered</a>
                    </div>
                </div>
                
                <!-- Orders List -->
                <c:forEach var="order" items="${orders}">
                    <div class="card order-card mb-3">
                        <div class="card-body">
                            <div class="row align-items-center">
                                <!-- Order Info -->
                                <div class="col-md-3">
                                    <h6 class="mb-1 fw-bold">Order #${order.orderNumber}</h6>
                                    <small class="text-muted d-block">
                                        <i class="far fa-calendar me-1"></i>
                                        <fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy HH:mm" />
                                    </small>
                                    <small class="text-muted">
                                        <i class="fas fa-map-marker-alt me-1"></i>
                                        ${order.shippingAddress.length() > 30 ? order.shippingAddress.substring(0,30) + '...' : order.shippingAddress}
                                    </small>
                                </div>
                                
                                <!-- Status -->
                                <div class="col-md-2">
                                    <div class="d-flex flex-column">
                                        <small class="text-muted">Status</small>
                                        <span class="badge bg-${order.statusColor} status-badge mt-1">
                                            <i class="fas fa-circle me-1" style="font-size: 0.5rem;"></i>
                                            ${order.status}
                                        </span>
                                    </div>
                                </div>
                                
                                <!-- Payment -->
                                <div class="col-md-2">
                                    <div class="d-flex flex-column">
                                        <small class="text-muted">Payment</small>
                                        <span class="badge bg-${order.paymentStatusColor} status-badge mt-1">
                                            ${order.paymentStatus}
                                        </span>
                                    </div>
                                </div>
                                
                                <!-- Amount -->
                                <div class="col-md-2">
                                    <div class="d-flex flex-column">
                                        <small class="text-muted">Total Amount</small>
                                        <h5 class="mb-0 text-success">$${order.totalAmount}</h5>
                                    </div>
                                </div>
                                
                                <!-- Actions -->
                                <div class="col-md-3">
                                    <div class="d-flex flex-column align-items-end">
                                        <div class="d-flex gap-2">
                                            <a href="${pageContext.request.contextPath}/orders/view?id=${order.orderId}" 
                                               class="btn btn-outline-primary btn-sm">
                                                <i class="fas fa-eye me-1"></i> View Details
                                            </a>
                                            <c:if test="${order.canBeCancelled()}">
                                                <form action="${pageContext.request.contextPath}/orders/cancel" method="post" class="d-inline">
                                                    <input type="hidden" name="orderId" value="${order.orderId}">
                                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                    <button type="submit" class="btn btn-outline-danger btn-sm" 
                                                            onclick="return confirm('Are you sure you want to cancel order #${order.orderNumber}?')">
                                                        <i class="fas fa-times me-1"></i> Cancel
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
                                        <c:if test="${order.status == 'DELIVERED'}">
                                            <button class="btn btn-outline-success btn-sm mt-2">
                                                <i class="fas fa-redo me-1"></i> Buy Again
                                            </button>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <!-- No Orders -->
                <div class="text-center py-5">
                    <div class="mb-4">
                        <i class="fas fa-box-open fa-5x text-muted"></i>
                    </div>
                    <h3 class="mb-3">No orders yet</h3>
                    <p class="text-muted mb-4">You haven't placed any orders yet. Start shopping!</p>
                    <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                        <a href="${pageContext.request.contextPath}/product" class="btn btn-primary btn-lg">
                            <i class="fas fa-store me-2"></i> Start Shopping
                        </a>
                        <a href="${pageContext.request.contextPath}/" class="btn btn-outline-primary btn-lg">
                            <i class="fas fa-home me-2"></i> Go Home
                        </a>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Footer -->
    <footer class="bg-dark text-white py-4 mt-5">
        <div class="container">
            <div class="row">
                <div class="col-md-4">
                    <h5><i class="fas fa-bolt"></i> Fast Delivery</h5>
                    <p class="text-muted">Fastest delivery service for all your needs.</p>
                </div>
                <div class="col-md-4">
                    <h5>Quick Links</h5>
                    <ul class="list-unstyled">
                        <li><a href="${pageContext.request.contextPath}/product" class="text-white text-decoration-none">Shop</a></li>
                        <li><a href="${pageContext.request.contextPath}/cart" class="text-white text-decoration-none">Cart</a></li>
                        <li><a href="${pageContext.request.contextPath}/orders" class="text-white text-decoration-none">Orders</a></li>
                    </ul>
                </div>
                <div class="col-md-4">
                    <h5>Need Help?</h5>
                    <p class="text-muted">Contact: support@fastdelivery.com</p>
                </div>
            </div>
            <hr class="bg-light">
            <p class="text-center mb-0">&copy; 2024 Fast Delivery. All rights reserved.</p>
        </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>