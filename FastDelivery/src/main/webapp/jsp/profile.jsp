<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>My Profile - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
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
        <h2 class="mb-4">My Profile</h2>
        
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
        
        <c:if test="${not empty user}">
            <div class="row">
                <!-- Profile Info -->
                <div class="col-lg-4 mb-4">
                    <div class="card">
                        <div class="card-body text-center">
                            <div class="mb-3">
                                <i class="fas fa-user-circle fa-5x text-secondary"></i>
                            </div>
                            <h4>${user.fullName}</h4>
                            <p class="text-muted">@${user.username}</p>
                            <span class="badge ${user.role == 'ADMIN' ? 'bg-danger' : 'bg-primary'}">
                                ${user.role}
                            </span>
                            <hr>
                            <p class="mb-1"><i class="fas fa-envelope me-2"></i> ${user.email}</p>
                            <c:if test="${not empty user.phone}">
                                <p class="mb-1"><i class="fas fa-phone me-2"></i> ${user.phone}</p>
                            </c:if>
                            <p class="mb-0"><i class="fas fa-calendar me-2"></i> Member since ${user.createdAt}</p>
                        </div>
                    </div>
                </div>
                
                <!-- Update Forms -->
                <div class="col-lg-8">
                    <!-- Update Profile Form -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5 class="mb-0">Update Profile</h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/auth/update-profile" method="post">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <label for="firstName" class="form-label">First Name</label>
                                        <input type="text" class="form-control" id="firstName" name="firstName" 
                                               value="${user.firstName}">
                                    </div>
                                    <div class="col-md-6">
                                        <label for="lastName" class="form-label">Last Name</label>
                                        <input type="text" class="form-control" id="lastName" name="lastName" 
                                               value="${user.lastName}">
                                    </div>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email *</label>
                                    <input type="email" class="form-control" id="email" name="email" 
                                           value="${user.email}" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="phone" class="form-label">Phone</label>
                                    <input type="text" class="form-control" id="phone" name="phone" 
                                           value="${user.phone}">
                                </div>
                                
                                <div class="mb-3">
                                    <label for="address" class="form-label">Address</label>
                                    <textarea class="form-control" id="address" name="address" rows="3">${user.address}</textarea>
                                </div>
                                
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-2"></i> Update Profile
                                </button>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Change Password Form -->
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0">Change Password</h5>
                        </div>
                        <div class="card-body">
                            <form action="${pageContext.request.contextPath}/auth/change-password" method="post">
                                <div class="mb-3">
                                    <label for="currentPassword" class="form-label">Current Password *</label>
                                    <input type="password" class="form-control" id="currentPassword" 
                                           name="currentPassword" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="newPassword" class="form-label">New Password *</label>
                                    <input type="password" class="form-control" id="newPassword" 
                                           name="newPassword" required>
                                </div>
                                
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Confirm New Password *</label>
                                    <input type="password" class="form-control" id="confirmPassword" 
                                           name="confirmPassword" required>
                                </div>
                                
                                <button type="submit" class="btn btn-warning">
                                    <i class="fas fa-key me-2"></i> Change Password
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>