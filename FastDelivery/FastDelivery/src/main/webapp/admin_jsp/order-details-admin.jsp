<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Order #${order.orderNumber} - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .invoice-header {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
            color: white;
            border-radius: 10px;
            padding: 25px;
        }
    </style>
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <div class="container py-4">
        <!-- Admin Header with Back Button -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3><i class="fas fa-shopping-cart me-2"></i> Order #${order.orderNumber}</h3>
                <p class="text-muted">
                    <a href="${contextPath}/admin/orders" class="text-decoration-none">
                        <i class="fas fa-arrow-left me-1"></i> Back to Orders
                    </a>
                    |
                    <a href="${contextPath}/admin" class="text-decoration-none">
                        <i class="fas fa-tachometer-alt me-1"></i> Dashboard
                    </a>
                </p>
            </div>
            <div>
                <span class="badge bg-${order.statusColor} fs-5 px-3 py-2">
                    ${order.status}
                </span>
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
        
        <div class="row">
            <!-- Order Details -->
            <div class="col-lg-8">
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Order Items</h5>
                    </div>
                    <div class="card-body">
                        <c:forEach var="item" items="${order.items}">
                            <div class="row border-bottom py-2">
                                <div class="col-2">
                                    <img src="${item.product.image_url}" 
                                         class="img-fluid rounded" 
                                         alt="${item.productName}"
                                         onerror="this.src='https://via.placeholder.com/100'"
                                         style="width: 60px; height: 60px; object-fit: contain;">
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
                        </c:forEach>
                    </div>
                </div>
            </div>
            
            <!-- Order Summary & Actions -->
            <div class="col-lg-4">
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Customer Information</h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty order.user}">
                                <p class="mb-1"><strong>Name:</strong> ${order.user.username}</p>
                                <p class="mb-1"><strong>Email:</strong> ${order.user.email}</p>
                                <c:if test="${not empty order.user.phone}">
                                    <p class="mb-1"><strong>Phone:</strong> ${order.user.phone}</p>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <p class="mb-1 text-muted"><i>User ID: ${order.userId}</i></p>
                                <p class="mb-0 text-muted"><i>User information not available</i></p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="mb-0">Shipping & Payment</h5>
                    </div>
                    <div class="card-body">
                        <div class="mb-3">
                            <h6>Shipping Address</h6>
                            <p class="small">${order.shippingAddress}</p>
                        </div>
                        
                        <div class="mb-3">
                            <h6>Payment Method</h6>
                            <p class="small">
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
                        
                        <div class="mb-3">
                            <h6>Payment Status</h6>
                            <span class="badge bg-${order.paymentStatusColor}">
                                ${order.paymentStatus}
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Admin Actions -->
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">Admin Actions</h5>
                    </div>
                    <div class="card-body">
                        <!-- Status Update Form -->
                        <form action="${contextPath}/admin/orders" method="post" class="mb-3">
                            <input type="hidden" name="action" value="updateStatus">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            
                            <label class="form-label">Update Status</label>
                            <div class="input-group mb-2">
                                <select name="status" class="form-select">
                                    <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                    <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                    <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                    <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                </select>
                                <button type="submit" class="btn btn-primary">Update</button>
                            </div>
                        </form>
                        
                        <!-- Payment Status Update -->
                        <form action="${contextPath}/admin/orders" method="post" class="mb-3">
                            <input type="hidden" name="action" value="updatePaymentStatus">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                            
                            <label class="form-label">Update Payment Status</label>
                            <div class="input-group mb-2">
                                <select name="paymentStatus" class="form-select">
                                    <option value="PENDING" ${order.paymentStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="PAID" ${order.paymentStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                                    <option value="FAILED" ${order.paymentStatus == 'FAILED' ? 'selected' : ''}>Failed</option>
                                </select>
                                <button type="submit" class="btn btn-primary">Update</button>
                            </div>
                        </form>
                        
                        <!-- Cancel Order Button (only if not already cancelled) -->
                        <c:if test="${order.status != 'CANCELLED'}">
                            <form action="${contextPath}/orders/cancel" method="post" 
                                  onsubmit="return confirm('Cancel order #${order.orderNumber}? This cannot be undone.')">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <button type="submit" class="btn btn-danger w-100">
                                    <i class="fas fa-times me-2"></i> Cancel Order
                                </button>
                            </form>
                        </c:if>
                        
                        <!-- Delete Order Button (if cancelled) -->
                        <c:if test="${order.status == 'CANCELLED'}">
                            <form action="${contextPath}/admin/orders" method="post" 
                                  onsubmit="return confirm('Permanently delete order #${order.orderNumber}? This cannot be undone.')">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <button type="submit" class="btn btn-danger w-100 mt-2">
                                    <i class="fas fa-trash me-2"></i> Delete Order
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>
                
                <!-- Price Breakdown -->
                <div class="card mt-4">
                    <div class="card-header">
                        <h5 class="mb-0">Price Breakdown</h5>
                    </div>
                    <div class="card-body">
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
            </div>
        </div>
    </div>
    
    <%@ include file="admin-footer.jsp" %>
</body>
</html>