<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Track Order - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .track-card {
            max-width: 500px;
            margin: 0 auto;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body>

    
    <div class="container py-5">
        <div class="card track-card">
            <div class="card-body p-5">
                <div class="text-center mb-4">
                    <i class="fas fa-truck fa-3x text-primary mb-3"></i>
                    <h2>Track Your Order</h2>
                    <p class="text-muted">Enter your order number to track your package</p>
                </div>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/orders/track" method="get">
                    <div class="mb-4">
                        <label for="orderNumber" class="form-label">Order Number</label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="fas fa-hashtag"></i>
                            </span>
                            <input type="text" class="form-control form-control-lg" 
                                   id="orderNumber" name="orderNumber" 
                                   placeholder="e.g., ORD000123" required>
                        </div>
                        <div class="form-text">
                            Enter the order number you received in your confirmation email
                        </div>
                    </div>
                    
                    <button type="submit" class="btn btn-primary btn-lg w-100 mb-3">
                        <i class="fas fa-search me-2"></i> Track Order
                    </button>
                    
                    <div class="text-center">
                        <a href="${pageContext.request.contextPath}/orders" class="text-decoration-none">
                            <i class="fas fa-arrow-left me-1"></i> Back to My Orders
                        </a>
                    </div>
                </form>
                
                <hr class="my-4">
                
                <div class="text-center">
                    <h6>Need Help?</h6>
                    <p class="text-muted small mb-0">
                        Can't find your order number? 
                        <a href="#" class="text-decoration-none">Contact Customer Support</a>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>