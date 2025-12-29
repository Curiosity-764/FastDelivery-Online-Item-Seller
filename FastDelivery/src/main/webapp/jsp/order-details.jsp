<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Order #${order.orderNumber} - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .invoice-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-radius: 10px;
            padding: 25px;
        }
        .order-item {
            border-bottom: 1px solid #dee2e6;
            padding: 15px 0;
        }
        .order-item:last-child {
            border-bottom: none;
        }
        .status-timeline {
            position: relative;
            padding-left: 30px;
        }
        .status-timeline:before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 2px;
            background: #dee2e6;
        }
        .status-step {
            position: relative;
            margin-bottom: 25px;
        }
        .status-step:before {
            content: '';
            position: absolute;
            left: -34px;
            top: 0;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #dee2e6;
        }
        .status-step.active:before {
            background: #28a745;
            box-shadow: 0 0 0 3px rgba(40, 167, 69, 0.2);
        }
        .status-step.completed:before {
            background: #28a745;
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
            
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/product">
                            <i class="fas fa-store"></i> Shop
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="${pageContext.request.contextPath}/cart">
                            <i class="fas fa-shopping-cart"></i> Cart
                            <c:if test="${sessionScope.cart != null && !sessionScope.cart.isEmpty()}">
                                <span class="badge bg-danger">${sessionScope.cart.totalItemsCount}</span>
                            </c:if>
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link active" href="${pageContext.request.contextPath}/orders">
                            <i class="fas fa-box"></i> Orders
                        </a>
                    </li>
                    <c:if test="${sessionScope.user != null}">
                        <li class="nav-item">
                            <span class="nav-link">Welcome, ${sessionScope.user.username}</span>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout">
                                <i class="fas fa-sign-out-alt"></i> Logout
                            </a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container py-4">
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
        
        <!-- Invoice Header -->
        <div class="invoice-header mb-4">
            <div class="row align-items-center">
                <div class="col-md-6">
                    <h2 class="mb-1">Order #${order.orderNumber}</h2>
                    <p class="mb-0">
                        <i class="far fa-calendar me-1"></i>
                        <fmt:formatDate value="${order.orderDate}" pattern="MMMM dd, yyyy 'at' HH:mm" />
                    </p>
                </div>
                <div class="col-md-6 text-end">
                    <span class="badge bg-${order.statusColor} fs-6 px-3 py-2">
                        ${order.status}
                    </span>
                    <div class="mt-2">
                        <span class="badge bg-${order.paymentStatusColor}">
                            Payment: ${order.paymentStatus}
                        </span>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="row">
            <!-- Order Details -->
            <div class="col-lg-8">
                <!-- Order Items -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Order Items</h5>
                    </div>
                    <div class="card-body">
                        <c:forEach var="item" items="${order.items}">
                            <div class="order-item">
                                <div class="row align-items-center">
                                    <div class="col-2">
                                        <img src="${item.product.image_url}" 
                                             class="img-fluid rounded" 
                                             alt="${item.productName}"
                                             onerror="this.src='https://via.placeholder.com/100'"
                                             style="width: 80px; height: 80px; object-fit: contain;">
                                    </div>
                                    <div class="col-5">
                                        <h6 class="mb-1">${item.productName}</h6>
                                        <small class="text-muted">Product ID: ${item.productId}</small>
                                    </div>
                                    <div class="col-2 text-center">
                                        <span class="badge bg-light text-dark">×${item.quantity}</span>
                                    </div>
                                    <div class="col-3 text-end">
                                        <div class="fw-bold">$${item.unitPrice}</div>
                                        <div class="text-muted small">Total: $${item.totalPrice}</div>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
                
                <!-- Order Status Timeline -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Order Status</h5>
                    </div>
                    <div class="card-body">
                        <div class="status-timeline">
                            <div class="status-step ${order.status == 'PENDING' ? 'active' : 'completed'}">
                                <h6 class="mb-1">Order Placed</h6>
                                <p class="text-muted mb-0 small">Order confirmed and payment received</p>
                            </div>
                            <div class="status-step ${order.status == 'CONFIRMED' ? 'active' : 
                                                      order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'completed' : ''}">
                                <h6 class="mb-1">Order Confirmed</h6>
                                <p class="text-muted mb-0 small">Order is being processed</p>
                            </div>
                            <div class="status-step ${order.status == 'SHIPPED' ? 'active' : 
                                                      order.status == 'DELIVERED' ? 'completed' : ''}">
                                <h6 class="mb-1">Shipped</h6>
                                <p class="text-muted mb-0 small">Order is on the way</p>
                            </div>
                            <div class="status-step ${order.status == 'DELIVERED' ? 'active' : ''}">
                                <h6 class="mb-1">Delivered</h6>
                                <p class="text-muted mb-0 small">Order delivered successfully</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Order Summary -->
            <div class="col-lg-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Order Summary</h5>
                    </div>
                    <div class="card-body">
                        <!-- Customer Info -->
                        <div class="mb-4">
                            <h6>Customer Information</h6>
                            <div class="small">
                                <p class="mb-1"><strong>Name:</strong> ${order.user.username}</p>
                                <p class="mb-1"><strong>Email:</strong> ${order.user.email}</p>
                            </div>
                        </div>
                        
                        <!-- Shipping Info -->
                        <div class="mb-4">
                            <h6>Shipping Address</h6>
                            <p class="small text-muted">${order.shippingAddress}</p>
                        </div>
                        
                        <!-- Payment Info -->
                        <div class="mb-4">
                            <h6>Payment Method</h6>
                            <p class="small text-muted">
                                <c:choose>
                                    <c:when test="${order.paymentMethod == 'COD'}">
                                        <i class="fas fa-money-bill-wave me-1"></i> Cash on Delivery
                                    </c:when>
                                    <c:otherwise>
                                        ${order.paymentMethod}
                                    </c:otherwise>
                                </c:choose>
                            </p>
                        </div>
                        
                        <!-- Price Breakdown -->
                        <div class="mb-4">
                            <h6>Price Breakdown</h6>
                            <div class="small">
                                <div class="d-flex justify-content-between mb-1">
                                    <span>Subtotal</span>
                                    <span>
                                        <c:set var="subtotal" value="0"/>
                                        <c:forEach var="item" items="${order.items}">
                                            <c:set var="subtotal" value="${subtotal + item.totalPrice}"/>
                                        </c:forEach>
                                        $${subtotal}
                                    </span>
                                </div>
                                <div class="d-flex justify-content-between mb-1">
                                    <span>Shipping</span>
                                    <span>$5.00</span>
                                </div>
                                <div class="d-flex justify-content-between mb-1">
                                    <span>Tax</span>
                                    <span>
                                        $<fmt:formatNumber value="${subtotal * 0.1}" pattern="#.##"/>
                                    </span>
                                </div>
                                <hr class="my-2">
                                <div class="d-flex justify-content-between">
                                    <strong>Total</strong>
                                    <strong>$${order.totalAmount}</strong>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Actions -->
                        <div class="d-grid gap-2">
                            <c:if test="${order.canBeCancelled()}">
                                <form action="${pageContext.request.contextPath}/orders/cancel" method="post">
                                    <input type="hidden" name="orderId" value="${order.orderId}">
                                    <button type="submit" class="btn btn-danger w-100" 
                                            onclick="return confirm('Cancel order #${order.orderNumber}?')">
                                        <i class="fas fa-times me-2"></i> Cancel Order
                                    </button>
                                </form>
                            </c:if>
                            
                            <c:if test="${order.status == 'DELIVERED'}">
                                <button class="btn btn-outline-primary w-100">
                                    <i class="fas fa-redo me-2"></i> Buy Again
                                </button>
                                <button class="btn btn-outline-success w-100">
                                    <i class="fas fa-star me-2"></i> Rate Order
                                </button>
                            </c:if>
                            
                            <a href="${pageContext.request.contextPath}/product" class="btn btn-primary w-100">
                                <i class="fas fa-store me-2"></i> Continue Shopping
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>