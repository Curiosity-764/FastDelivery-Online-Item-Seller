<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
    
    <%@ include file="admin-nav.jsp" %>
    
    <div class="container mt-4">
        <!-- Header -->
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h3><i class="fas fa-users me-2"></i> User Management</h3>
                <p class="text-muted">Manage customer and administrator accounts</p>
            </div>
            <div class="btn-group">
                <a href="${contextPath}/admin/users?showInactive=false" 
                   class="btn btn-outline-primary ${not param.showInactive ? 'active' : ''}">
                    Active Users
                </a>
                <a href="${contextPath}/admin/users?showInactive=true" 
                   class="btn btn-outline-primary ${param.showInactive ? 'active' : ''}">
                    All Users
                </a>
                <a href="${contextPath}/admin/users?action=add" 
                   class="btn btn-success">
                    <i class="fas fa-user-plus me-1"></i> Add User
                </a>
            </div>
        </div>

        <!-- User List Table -->
        <div class="card">
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Role</th>
                                <th>Status</th>
                                <th>Joined</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="user" items="${users}">
                                <tr>
                                    <td>${user.userId}</td>
                                    <td>
                                        <strong>${user.username}</strong>
                                        <c:if test="${user.userId == sessionScope.user.userId}">
                                            <span class="badge bg-info ms-1">You</span>
                                        </c:if>
                                    </td>
                                    <td>${user.fullName}</td>
                                    <td>${user.email}</td>
                                    <td>
                                        <span class="badge ${user.role == 'ADMIN' ? 'bg-danger' : 'bg-secondary'}">
                                            ${user.role}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                                            ${user.active ? 'Active' : 'Inactive'}
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${user.createdAt}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td>
                                        <div class="btn-group btn-group-sm">
                                            <!-- Role Change -->
                                            <c:if test="${user.userId != sessionScope.user.userId}">
                                                <c:choose>
                                                    <c:when test="${user.role == 'CUSTOMER'}">
                                                        <form action="${contextPath}/admin/users" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="makeAdmin">
                                                            <input type="hidden" name="userId" value="${user.userId}">
                                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                            <button type="submit" class="btn btn-outline-warning btn-sm" 
                                                                    onclick="return confirm('Make ${user.username} an ADMIN?')">
                                                                Make Admin
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:when test="${user.role == 'ADMIN'}">
                                                        <form action="${contextPath}/admin/users" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="makeCustomer">
                                                            <input type="hidden" name="userId" value="${user.userId}">
                                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                            <button type="submit" class="btn btn-outline-secondary btn-sm"
                                                                    onclick="return confirm('Change ${user.username} to CUSTOMER?')">
                                                                Make Customer
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                </c:choose>
                                                
                                                <!-- Activation/Deactivation -->
                                                <c:choose>
                                                    <c:when test="${user.active}">
                                                        <form action="${contextPath}/admin/users" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="deactivate">
                                                            <input type="hidden" name="userId" value="${user.userId}">
                                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                            <button type="submit" class="btn btn-outline-danger btn-sm"
                                                                    onclick="return confirm('Deactivate ${user.username}?')">
                                                                Deactivate
                                                            </button>
                                                        </form>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <form action="${contextPath}/admin/users" method="post" class="d-inline">
                                                            <input type="hidden" name="action" value="activate">
                                                            <input type="hidden" name="userId" value="${user.userId}">
                                                            <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                            <button type="submit" class="btn btn-outline-success btn-sm">
                                                                Activate
                                                            </button>
                                                        </form>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                
                <c:if test="${empty users}">
                    <div class="text-center py-5">
                        <i class="fas fa-users fa-3x text-muted mb-3"></i>
                        <h5>No Users Found</h5>
                        <p class="text-muted">
                            <c:choose>
                                <c:when test="${param.showInactive}">No users in the system</c:when>
                                <c:otherwise>No active users found</c:otherwise>
                            </c:choose>
                        </p>
                        <a href="${contextPath}/admin/users?action=add" class="btn btn-primary">
                            <i class="fas fa-user-plus me-2"></i> Add First User
                        </a>
                    </div>
                </c:if>
                
                <div class="alert alert-info mt-4">
                    <i class="fas fa-info-circle me-2"></i>
                    <strong>Admin Actions Guide:</strong>
                    <div class="row mt-2">
                        <div class="col-md-3">
                            <span class="badge bg-warning">Make Admin</span><br>
                            <small>Give user administrator privileges</small>
                        </div>
                        <div class="col-md-3">
                            <span class="badge bg-secondary">Make Customer</span><br>
                            <small>Remove admin privileges</small>
                        </div>
                        <div class="col-md-3">
                            <span class="badge bg-danger">Deactivate</span><br>
                            <small>Disable user account (soft delete)</small>
                        </div>
                        <div class="col-md-3">
                            <span class="badge bg-success">Activate</span><br>
                            <small>Reactivate disabled account</small>
                        </div>
                    </div>
                    <p class="mt-2 mb-0"><small><i class="fas fa-shield-alt me-1"></i> You cannot modify your own role or status</small></p>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="admin-footer.jsp" %>
</body>
</html>