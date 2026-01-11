<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tracking Order #${order.orderNumber} - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .tracking-card {
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
        .tracking-step {
            position: relative;
            padding-left: 40px;
            margin-bottom: 30px;
        }
        .tracking-step:before {
            content: '';
            position: absolute;
            left: 0;
            top: 0;
            width: 25px;
            height: 25px;
            border-radius: 50%;
            border: 3px solid #dee2e6;
            background: white;
        }
        .tracking-step.active:before {
            border-color: #28a745;
            background: #28a745;
        }
        .tracking-step.completed:before {
            border-color: #28a745;
            background: #28a745;
        }
        .tracking-step:after {
            content: '';
            position: absolute;
            left: 11px;
            top: 25px;
            bottom: -30px;
            width: 2px;
            background: #dee2e6;
        }
        .tracking-step:last-child:after {
            display: none;
        }
        .tracking-step.completed:after {
            background: #28a745;
        }
    </style>
</head>
<body>
    <div class="container py-5">
        <div class="card tracking-card">
            <div class="card-body p-5">
                <!-- Order Header -->
                <div class="text-center mb-5">
                    <i class="fas fa-truck fa-3x text-primary mb-3"></i>
                    <h2>Tracking Order #${order.orderNumber}</h2>
                    <p class="text-muted">
                        Ordered on <fmt:formatDate value="${order.orderDate}" pattern="MMMM dd, yyyy" />
                    </p>
                    <span class="badge bg-${order.statusColor} fs-5 px-3 py-2">
                        ${order.status}
                    </span>
                </div>
                
                <!-- Tracking Timeline -->
                <div class="row">
                    <div class="col-lg-8 mx-auto">
                        <div class="tracking-step ${order.status == 'PENDING' ? 'active' : 'completed'}">
                            <h5>Order Placed</h5>
                            <p class="text-muted mb-0">Your order has been received</p>
                            <small class="text-muted">
                                <fmt:formatDate value="${order.orderDate}" pattern="MMM dd, HH:mm" />
                            </small>
                        </div>
                        
                        <div class="tracking-step ${order.status == 'CONFIRMED' ? 'active' : 
                                                   order.status == 'SHIPPED' || order.status == 'DELIVERED' ? 'completed' : ''}">
                            <h5>Order Confirmed</h5>
                            <p class="text-muted mb-0">Order is being processed</p>
                            <small class="text-muted">Estimated: <fmt:formatDate value="${order.orderDate}" pattern="MMM dd" /></small>
                        </div>
                        
                        <div class="tracking-step ${order.status == 'SHIPPED' ? 'active' : 
                                                   order.status == 'DELIVERED' ? 'completed' : ''}">
                            <h5>Shipped</h5>
                            <p class="text-muted mb-0">Your order is on the way</p>
                            <small class="text-muted">
                                <c:choose>
                                    <c:when test="${order.status == 'SHIPPED' || order.status == 'DELIVERED'}">
                                        Shipped on <fmt:formatDate value="${order.orderDate}" pattern="MMM dd" />
                                    </c:when>
                                    <c:otherwise>
                                        Estimated: Tomorrow
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                        
                        <div class="tracking-step ${order.status == 'DELIVERED' ? 'active' : ''}">
                            <h5>Delivered</h5>
                            <p class="text-muted mb-0">Your order has been delivered</p>
                            <small class="text-muted">
                                <c:choose>
                                    <c:when test="${order.status == 'DELIVERED'}">
                                        Delivered today
                                    </c:when>
                                    <c:otherwise>
                                        Estimated: 3-5 business days
                                    </c:otherwise>
                                </c:choose>
                            </small>
                        </div>
                    </div>
                </div>
                
                <!-- Order Details -->
                <div class="row mt-5">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h6 class="mb-0">Shipping Address</h6>
                            </div>
                            <div class="card-body">
                                <p class="mb-0">${order.shippingAddress}</p>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h6 class="mb-0">Order Summary</h6>
                            </div>
                            <div class="card-body">
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Order Total</span>
                                    <strong>$${order.totalAmount}</strong>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Payment Method</span>
                                    <span>${order.paymentMethod}</span>
                                </div>
                                <div class="d-flex justify-content-between">
                                    <span>Payment Status</span>
                                    <span class="badge bg-${order.paymentStatusColor}">
                                        ${order.paymentStatus}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Actions -->
                <div class="text-center mt-4">
                    <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                        <a href="${pageContext.request.contextPath}/orders/view?number=${order.orderNumber}" 
                           class="btn btn-primary">
                            <i class="fas fa-file-invoice me-2"></i> View Full Order Details
                        </a>
                        <a href="${pageContext.request.contextPath}/orders/track" 
                           class="btn btn-outline-primary">
                            <i class="fas fa-search me-2"></i> Track Another Order
                        </a>
                        <a href="${pageContext.request.contextPath}/orders" 
                           class="btn btn-outline-secondary">
                            <i class="fas fa-arrow-left me-2"></i> My Orders
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>