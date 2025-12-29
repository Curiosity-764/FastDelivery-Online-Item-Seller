<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
<head>
    <title>Error - Fast Delivery</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            background: #f8f9fa;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }
        .error-card {
            max-width: 600px;
            margin: 0 auto;
            padding: 30px;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            background: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="error-card text-center">
            <i class="fas fa-exclamation-triangle fa-4x text-danger mb-4"></i>
            <h1 class="display-4 fw-bold text-danger">Oops!</h1>
            <h2 class="mb-4">Something went wrong</h2>
            
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <p class="lead mb-4">${errorMessage}</p>
                </c:when>
                <c:otherwise>
                    <p class="lead mb-4">We're sorry, but an error occurred while processing your request.</p>
                </c:otherwise>
            </c:choose>
            
            <div class="alert alert-danger text-start mb-4" role="alert">
                <h5 class="alert-heading">Error Details:</h5>
                <c:if test="${not empty pageContext.errorData.throwable}">
                    <p class="mb-0">${pageContext.errorData.throwable.message}</p>
                </c:if>
                <c:if test="${not empty pageContext.errorData.requestURI}">
                    <hr>
                    <p class="mb-0"><strong>Request URI:</strong> ${pageContext.errorData.requestURI}</p>
                </c:if>
                <c:if test="${not empty pageContext.errorData.statusCode}">
                    <p class="mb-0"><strong>Status Code:</strong> ${pageContext.errorData.statusCode}</p>
                </c:if>
            </div>
            
            <div class="d-grid gap-2 d-md-flex justify-content-md-center">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary me-md-2">
                    <i class="fas fa-home me-2"></i> Go Home
                </a>
                <a href="${pageContext.request.contextPath}/product" class="btn btn-outline-primary">
                    <i class="fas fa-store me-2"></i> Browse Products
                </a>
                <button onclick="history.back()" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i> Go Back
                </button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>