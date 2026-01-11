<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Manage Orders - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <!-- Header -->
    <div class="mb-4">
        <h3><i class="fas fa-shopping-cart me-2"></i> Order Management</h3>
        <p class="text-muted">View and manage customer orders</p>
    </div>

    <!-- Filters -->
    <div class="card mb-4">
        <div class="card-body">
            <form method="get" class="row g-3">
                <div class="col-md-3">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-control">
                        <option value="">All Statuses</option>
                        <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="CONFIRMED" ${param.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                        <option value="SHIPPED" ${param.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                        <option value="DELIVERED" ${param.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                        <option value="CANCELLED" ${param.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                    </select>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Payment Status</label>
                    <select name="paymentStatus" class="form-control">
                        <option value="">All Payments</option>
                        <option value="PENDING" ${param.paymentStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                        <option value="PAID" ${param.paymentStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                        <option value="FAILED" ${param.paymentStatus == 'FAILED' ? 'selected' : ''}>Failed</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Date Range</label>
                    <input type="date" name="startDate" class="form-control" value="${param.startDate}">
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="fas fa-filter me-2"></i> Filter
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Orders Table -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Order #</th>
                            <th>Customer</th>
                            <th>Date</th>
                            <th>Items</th>
                            <th>Total</th>
                            <th>Status</th>
                            <th>Payment</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td>
                                    <strong>${order.orderNumber}</strong>
                                </td>
                                <td>
                                    <c:if test="${not empty order.user}">
                                        <div>${order.user.username}</div>
                                        <small class="text-muted">${order.user.email}</small>
                                    </c:if>
                                </td>
                                <td>
                                    <fmt:formatDate value="${order.orderDate}" pattern="MM/dd/yyyy"/>
                                    <div class="text-muted small">
                                        <fmt:formatDate value="${order.orderDate}" pattern="hh:mm a"/>
                                    </div>
                                </td>
                                <td>
                                    <c:if test="${not empty order.items}">
                                        ${order.items.size()} items
                                    </c:if>
                                </td>
                                <td>
                                    <strong>$${order.totalAmount}</strong>
                                </td>
                                <td>
                                    <form action="${contextPath}/admin/orders" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="updateStatus">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                                        <select name="status" class="form-select form-select-sm" 
                                                onchange="this.form.submit()" 
                                                style="width: auto; display: inline-block;">
                                            <option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                            <option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Confirmed</option>
                                            <option value="SHIPPED" ${order.status == 'SHIPPED' ? 'selected' : ''}>Shipped</option>
                                            <option value="DELIVERED" ${order.status == 'DELIVERED' ? 'selected' : ''}>Delivered</option>
                                            <option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Cancelled</option>
                                        </select>
                                    </form>
                                </td>
                                <td>
                                    <form action="${contextPath}/admin/orders" method="post" class="d-inline">
                                        <input type="hidden" name="action" value="updatePaymentStatus">
                                        <input type="hidden" name="orderId" value="${order.orderId}">
                                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                                        <select name="paymentStatus" class="form-select form-select-sm" 
                                                onchange="this.form.submit()" 
                                                style="width: auto; display: inline-block;">
                                            <option value="PENDING" ${order.paymentStatus == 'PENDING' ? 'selected' : ''}>Pending</option>
                                            <option value="PAID" ${order.paymentStatus == 'PAID' ? 'selected' : ''}>Paid</option>
                                            <option value="FAILED" ${order.paymentStatus == 'FAILED' ? 'selected' : ''}>Failed</option>
                                        </select>
                                    </form>
                                </td>
                                <td>
                                    <a href="${contextPath}/orders/view?id=${order.orderId}" 
                                       class="btn btn-sm btn-outline-primary" title="View Details">
                                        <i class="fas fa-eye"></i>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
            <c:if test="${empty orders}">
                <div class="text-center py-5">
                    <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                    <h5>No Orders Found</h5>
                    <p class="text-muted">No orders match your filters</p>
                </div>
            </c:if>
        </div>
    </div>
    
    <%@ include file="admin-footer.jsp" %>