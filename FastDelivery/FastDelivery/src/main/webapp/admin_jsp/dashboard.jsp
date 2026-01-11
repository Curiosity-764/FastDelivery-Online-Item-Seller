<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <!-- Dashboard Content -->
    <div class="mb-4">
        <h3><i class="fas fa-tachometer-alt me-2"></i> Dashboard</h3>
        <p class="text-muted">Overview of your store's performance</p>
    </div>

    <!-- Stats Cards -->
    <div class="row mb-4">
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-primary">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="card-subtitle mb-2">Total Users</h6>
                            <h2 class="card-title">${totalUsers}</h2>
                        </div>
                        <i class="fas fa-users fa-2x opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="card-subtitle mb-2">Total Products</h6>
                            <h2 class="card-title">${totalProducts}</h2>
                        </div>
                        <i class="fas fa-box fa-2x opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card bg-warning text-dark">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="card-subtitle mb-2">Total Orders</h6>
                            <h2 class="card-title">${totalOrders}</h2>
                        </div>
                        <i class="fas fa-shopping-cart fa-2x opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="col-md-3 mb-3">
            <div class="card text-white bg-info">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="card-subtitle mb-2">Recent Revenue</h6>
                            <h2 class="card-title">
                                <c:set var="totalRevenue" value="0"/>
                                <c:forEach var="order" items="${recentOrders}">
                                    <c:set var="totalRevenue" value="${totalRevenue + order.totalAmount}"/>
                                </c:forEach>
                                $<fmt:formatNumber value="${totalRevenue}" pattern="#,##0.00"/>
                            </h2>
                        </div>
                        <i class="fas fa-dollar-sign fa-2x opacity-75"></i>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Recent Orders Table -->
    <div class="card">
        <div class="card-header">
            <h5 class="mb-0">
                <i class="fas fa-clock me-2"></i> Recent Orders
                <a href="${contextPath}/admin/orders" class="btn btn-sm btn-outline-primary float-end">
                    View All
                </a>
            </h5>
        </div>
        <div class="card-body">
            <c:choose>
                <c:when test="${not empty recentOrders}">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Order #</th>
                                    <th>Customer</th>
                                    <th>Date</th>
                                    <th>Amount</th>
                                    <th>Status</th>
                                    <th>Payment</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="order" items="${recentOrders}">
                                    <tr>
                                        <td>${order.orderNumber}</td>
                                        <td>
                                            <c:if test="${not empty order.user}">
                                                ${order.user.username}
                                            </c:if>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy"/>
                                        </td>
                                        <td>$${order.totalAmount}</td>
                                        <td>
                                            <span class="badge ${order.status == 'PENDING' ? 'bg-warning' : 
                                                              order.status == 'DELIVERED' ? 'bg-success' : 
                                                              order.status == 'CANCELLED' ? 'bg-danger' : 'bg-info'}">
                                                ${order.status}
                                            </span>
                                        </td>
                                        <td>
                                            <span class="badge ${order.paymentStatus == 'PAID' ? 'bg-success' : 
                                                              order.paymentStatus == 'FAILED' ? 'bg-danger' : 'bg-warning'}">
                                                ${order.paymentStatus}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${contextPath}/orders/view?id=${order.orderId}" 
                                               class="btn btn-sm btn-outline-primary">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-4">
                        <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                        <p class="text-muted">No orders yet</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <%@ include file="admin-footer.jsp" %>
</body>
</html>