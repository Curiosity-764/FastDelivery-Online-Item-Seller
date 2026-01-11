<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<title>Checkout - Fast Delivery</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<style>
body {
	background-color: #f8f9fa;
}

.checkout-card {
	border-radius: 10px;
	box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.order-summary {
	background: #f8f9fa;
	border-radius: 10px;
	padding: 20px;
}

.progress-steps {
	display: flex;
	justify-content: space-between;
	margin-bottom: 30px;
	position: relative;
}

.progress-steps:before {
	content: '';
	position: absolute;
	top: 15px;
	left: 0;
	right: 0;
	height: 2px;
	background: #dee2e6;
	z-index: 1;
}

.step {
	position: relative;
	z-index: 2;
	text-align: center;
	flex: 1;
}

.step-number {
	width: 30px;
	height: 30px;
	border-radius: 50%;
	background: white;
	border: 2px solid #dee2e6;
	display: flex;
	align-items: center;
	justify-content: center;
	margin: 0 auto 10px;
	font-weight: bold;
}

.step.active .step-number {
	background: #28a745;
	border-color: #28a745;
	color: white;
}

.step.completed .step-number {
	background: #28a745;
	border-color: #28a745;
	color: white;
}
</style>
</head>
<body>
	<!-- Navigation -->
	<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-light bg-white shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold text-primary" href="${pageContext.request.contextPath}/">
            <i class="fas fa-bolt"></i> Fast Delivery
        </a>
        
        <form class="d-flex mx-auto" action="${pageContext.request.contextPath}/product" method="get" style="max-width: 400px;">
            <div class="input-group">
                <input type="text" class="form-control" name="search" placeholder="Search products..." 
                       value="${currentSearch}">
                <button class="btn btn-outline-primary" type="submit">
                    <i class="fas fa-search"></i>
                </button>
            </div>
        </form>
        
        <div class="d-flex align-items-center">
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary position-relative me-3">
                <i class="fas fa-shopping-cart"></i>
                <c:if test="${sessionScope.cart != null && !sessionScope.cart.isEmpty()}">
                    <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                        ${sessionScope.cart.totalItemsCount}
                    </span>
                </c:if>
            </a>
            
            <c:choose>
                <c:when test="${sessionScope.user != null}">
                    <div class="dropdown">
                        <button class="btn btn-outline-secondary dropdown-toggle" type="button" 
                                data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user me-1"></i> ${sessionScope.user.username}
                        </button>
                        <ul class="dropdown-menu">
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/profile">
                                <i class="fas fa-user-circle me-2"></i> Profile
                            </a></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/orders">
                                <i class="fas fa-box me-2"></i> My Orders
                            </a></li>
                            <!-- Add Admin Link for Admin Users -->
                            <c:if test="${sessionScope.user.role == 'ADMIN'}">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/admin">
                                    <i class="fas fa-cogs me-2"></i> Admin Panel
                                </a></li>
                            </c:if>
                            <li><hr class="dropdown-divider"></li>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/auth/logout">
                                <i class="fas fa-sign-out-alt me-2"></i> Logout
                            </a></li>
                        </ul>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/auth/login" class="btn btn-outline-primary">
                            <i class="fas fa-sign-in-alt me-1"></i> Login
                        </a>
                        <a href="${pageContext.request.contextPath}/auth/register" class="btn btn-primary">
                            <i class="fas fa-user-plus me-1"></i> Register
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

	<div class="container py-4">
		<!-- Progress Steps -->
		<div class="progress-steps">
			<div class="step completed">
				<div class="step-number">1</div>
				<small>Cart</small>
			</div>
			<div class="step active">
				<div class="step-number">2</div>
				<small>Checkout</small>
			</div>
			<div class="step">
				<div class="step-number">3</div>
				<small>Confirmation</small>
			</div>
		</div>

		<h2 class="mb-4">Checkout</h2>

		<!-- Flash Messages -->
		<c:if test="${not empty sessionScope.flashMessage}">
			<div class="alert alert-success alert-dismissible fade show mb-4"
				role="alert">
				${sessionScope.flashMessage}
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
			<c:remove var="flashMessage" scope="session" />
		</c:if>

		<c:if test="${not empty sessionScope.flashError}">
			<div class="alert alert-danger alert-dismissible fade show mb-4"
				role="alert">
				${sessionScope.flashError}
				<button type="button" class="btn-close" data-bs-dismiss="alert"></button>
			</div>
			<c:remove var="flashError" scope="session" />
		</c:if>

		<div class="row">
			<!-- Shipping Information -->
			<div class="col-lg-8 mb-4">
				<div class="card checkout-card">
					<div class="card-body">
						<h4 class="card-title mb-4">
							<i class="fas fa-shipping-fast me-2"></i> Shipping Information
						</h4>

						<form action="${pageContext.request.contextPath}/checkout"
							method="post" id="checkoutForm">
							<!-- CSRF Token -->
							<input type="hidden" name="csrfToken" value="${csrfToken}">
							<div class="mb-4">
								<label class="form-label">Customer Information</label>
								<div class="card bg-light">
									<div class="card-body">
										<div class="row">
											<div class="col-md-6">
												<p class="mb-1">
													<strong>Name:</strong> ${user.fullName}
												</p>
												<p class="mb-1">
													<strong>Email:</strong> ${user.email}
												</p>
											</div>
											<div class="col-md-6">
												<c:if test="${not empty user.phone}">
													<p class="mb-1">
														<strong>Phone:</strong> ${user.phone}
													</p>
												</c:if>
												<c:if test="${not empty user.address}">
													<p class="mb-0">
														<strong>Current Address:</strong> ${user.address}
													</p>
												</c:if>
											</div>
										</div>
									</div>
								</div>
							</div>

							<!-- Shipping Address -->
							<div class="mb-4">
								<label for="shippingAddress" class="form-label">Shipping
									Address *</label>
								<textarea class="form-control" id="shippingAddress"
									name="shippingAddress" rows="4" required
									placeholder="Enter your complete shipping address including street, city, postal code">${user.address}</textarea>
								<div class="form-text">Please provide complete address for
									delivery</div>
							</div>

							<!-- Payment Method -->
							<div class="mb-4">
								<label class="form-label">Payment Method *</label>
								<div class="form-check mb-2">
									<input class="form-check-input" type="radio"
										name="paymentMethod" id="cod" value="COD" checked required>
									<label class="form-check-label" for="cod"> <i
										class="fas fa-money-bill-wave me-2"></i> Cash on Delivery
										(COD)
									</label>
									<div class="form-text ms-4">Pay when you receive your
										order</div>
								</div>
								<div class="form-check">
									<input class="form-check-input" type="radio"
										name="paymentMethod" id="card" value="CARD" disabled>
									<label class="form-check-label text-muted" for="card">
										<i class="fas fa-credit-card me-2"></i> Credit/Debit Card
									</label>
									<div class="form-text ms-4 text-muted">Coming soon</div>
								</div>
							</div>

							<!-- Order Notes (Optional) -->
							<div class="mb-4">
								<label for="orderNotes" class="form-label">Order Notes
									(Optional)</label>
								<textarea class="form-control" id="orderNotes" name="orderNotes"
									rows="3" placeholder="Any special instructions for delivery..."></textarea>
							</div>

							<!-- Terms and Conditions -->
							<div class="form-check mb-4">
								<input class="form-check-input" type="checkbox" id="terms"
									required> <label class="form-check-label" for="terms">
									I agree to the <a href="#" class="text-decoration-none">Terms
										& Conditions</a> and <a href="#" class="text-decoration-none">Privacy
										Policy</a>
								</label>
							</div>

							<button type="submit" class="btn btn-success btn-lg w-100 py-3"
								id="placeOrderBtn">
								<i class="fas fa-lock me-2"></i> Place Order & Pay
							</button>

							<div class="text-center mt-3">
								<small class="text-muted"> <i
									class="fas fa-shield-alt me-1"></i> Secure checkout · Your
									information is safe
								</small>
							</div>
						</form>
					</div>
				</div>
			</div>

			<!-- Order Summary -->
			<div class="col-lg-4">
				<div class="order-summary">
					<h4 class="mb-4">Order Summary</h4>

					<!-- Items List -->
					<div class="mb-3">
						<h6>Items (${cart.totalItemsCount})</h6>
						<c:forEach var="item" items="${cart.items}">
							<div
								class="d-flex justify-content-between mb-2 border-bottom pb-2">
								<div>
									<span class="small">${item.product.product_name} ×
										${item.quantity}</span>
								</div>
								<span class="fw-bold">$${item.totalPrice}</span>
							</div>
						</c:forEach>
					</div>

					<hr>

					<!-- Price Breakdown -->
					<div class="mb-2">
						<div class="d-flex justify-content-between mb-2">
							<span>Subtotal</span> <span>$${cart.totalPrice}</span>
						</div>
						<div class="d-flex justify-content-between mb-2">
							<span>Shipping</span> <span
								class="${cart.totalPrice >= 50 ? 'text-success' : ''}"> <c:choose>
									<c:when test="${cart.totalPrice >= 50}">
										<i class="fas fa-check-circle"></i> FREE
                                    </c:when>
									<c:otherwise>
                                        $5.00
                                    </c:otherwise>
								</c:choose>
							</span>
						</div>
						<div class="d-flex justify-content-between mb-2">
							<span>Tax (10%)</span> <span>$${String.format("%.2f",
								cart.totalPrice * 0.1)}</span>
						</div>
					</div>

					<hr>

					<!-- Total -->
					<div class="d-flex justify-content-between align-items-center mb-4">
						<h4>Total</h4>
						<h4 class="text-success" id="orderTotal">
							$${String.format("%.2f", cart.totalPrice + (cart.totalPrice >= 50
							? 0 : 5) + (cart.totalPrice * 0.1))}</h4>
					</div>

					<!-- Delivery Estimate -->
					<div class="alert alert-info mb-4">
						<h6>
							<i class="fas fa-shipping-fast"></i> Estimated Delivery
						</h6>
						<p class="mb-0">3-5 business days</p>
						<small class="text-muted">Free returns within 30 days</small>
					</div>

					<!-- Continue Shopping -->
					<div class="mt-3 text-center">
						<a href="${pageContext.request.contextPath}/product"
							class="text-decoration-none"> <i class="fas fa-arrow-left"></i>
							Continue Shopping
						</a>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
	<script>
		// Form validation and submission handling
		document
				.getElementById('checkoutForm')
				.addEventListener(
						'submit',
						function(e) {
							const placeOrderBtn = document
									.getElementById('placeOrderBtn');
							const originalText = placeOrderBtn.innerHTML;

							// Disable button and show loading
							placeOrderBtn.disabled = true;
							placeOrderBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i> Processing...';

							// You could add additional validation here

							// Form will submit normally
						});

		// Auto-save address as user types (optional)
		document.getElementById('shippingAddress').addEventListener('input',
				function() {
					// Could save to session storage or cookies
					sessionStorage.setItem('shippingAddress', this.value);
				});

		// Load saved address if exists
		window
				.addEventListener(
						'load',
						function() {
							const savedAddress = sessionStorage
									.getItem('shippingAddress');
							if (savedAddress
									&& !document
											.getElementById('shippingAddress').value) {
								document.getElementById('shippingAddress').value = savedAddress;
							}
						});
	</script>
</body>
</html>