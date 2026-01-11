<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<style>
    .sidebar-link {
        padding: 12px 15px;
        border-radius: 8px;
        margin-bottom: 5px;
        display: block;
        text-decoration: none;
        color: #495057;
        transition: all 0.2s;
    }
    .sidebar-link:hover, .sidebar-link.active {
        background: #0d6efd;
        color: white;
    }
    .admin-navbar {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
    }
</style>

<!-- Admin Navbar -->
<nav class="navbar navbar-expand-lg navbar-dark admin-navbar shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin">
            <i class="fas fa-cogs me-2"></i> Admin Panel
        </a>
        
        <div class="d-flex align-items-center">
            <!-- Quick Store View -->
            <a href="${pageContext.request.contextPath}/" class="btn btn-outline-light me-3">
                <i class="fas fa-store me-1"></i> View Store
            </a>
            
            <!-- Admin User Dropdown -->
            <c:if test="${sessionScope.user != null}">
                <div class="dropdown">
                    <button class="btn btn-outline-light dropdown-toggle" type="button" 
                            data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fas fa-user-shield me-1"></i> ${sessionScope.user.username}
                    </button>
                    <ul class="dropdown-menu">
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">
                            <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                        </a></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/profile">
                            <i class="fas fa-user-circle me-2"></i> My Profile
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/">
                            <i class="fas fa-home me-2"></i> Homepage
                        </a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/logout">
                            <i class="fas fa-sign-out-alt me-2"></i> Logout
                        </a></li>
                    </ul>
                </div>
            </c:if>
        </div>
    </div>
</nav>

<!-- Admin Sidebar and Main Content -->
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3 col-lg-2 p-0">
            <div class="admin-sidebar p-3" style="min-height: calc(100vh - 56px); background: #f8f9fa; border-right: 1px solid #dee2e6;">
                <h6 class="text-muted mb-3">ADMIN NAVIGATION</h6>
                
                <a href="${pageContext.request.contextPath}/admin" 
                   class="sidebar-link ${empty activeMenu or activeMenu == 'dashboard' ? 'active' : ''}">
                    <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                </a>
                
                <a href="${pageContext.request.contextPath}/admin/users" 
                   class="sidebar-link ${activeMenu == 'users' ? 'active' : ''}">
                    <i class="fas fa-users me-2"></i> Users Management
                </a>
                
                <a href="${pageContext.request.contextPath}/admin/products" 
                   class="sidebar-link ${activeMenu == 'products' ? 'active' : ''}">
                    <i class="fas fa-box me-2"></i> Products Management
                </a>
                
                <a href="${pageContext.request.contextPath}/admin/orders" 
                   class="sidebar-link ${activeMenu == 'orders' ? 'active' : ''}">
                    <i class="fas fa-shopping-cart me-2"></i> Orders Management
                </a>
                
                <hr class="my-3">
                
                <h6 class="text-muted mb-3">QUICK ACTIONS</h6>
                
                <a href="${pageContext.request.contextPath}/admin/products?action=add" 
                   class="sidebar-link ${param.action == 'add' && activeMenu == 'products' ? 'active' : ''}">
                    <i class="fas fa-plus-circle me-2"></i> Add New Product
                </a>
                
                <a href="${pageContext.request.contextPath}/admin/users?action=add" 
                   class="sidebar-link ${param.action == 'add' && activeMenu == 'users' ? 'active' : ''}">
                    <i class="fas fa-user-plus me-2"></i> Add New User
                </a>
            </div>
        </div>

        <!-- Main Content Area -->
        <div class="col-md-9 col-lg-10 p-4">
            <!-- Flash Messages -->
            <c:if test="${not empty sessionScope.flashMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${sessionScope.flashMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashMessage" scope="session"/>
            </c:if>
            
            <c:if test="${not empty sessionScope.flashError}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${sessionScope.flashError}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
                <c:remove var="flashError" scope="session"/>
            </c:if>