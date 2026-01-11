	package Servlets;
	
	import jakarta.servlet.*;
	import jakarta.servlet.http.*;
	import jakarta.servlet.annotation.WebServlet;
	import java.io.IOException;
	import java.sql.SQLException;
	
	import DataAccessObject.UserDAO;
	import model.User;
	import utils.PasswordUtil;
	import utils.CSRFUtil;
	import Constants.Constants;
	
	@WebServlet("/auth/*")
	public class AuthServlet extends HttpServlet {
		private UserDAO userDAO;
	
		@Override
		public void init() {
			userDAO = new UserDAO();
		}
	
		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			String path = request.getPathInfo();
	
			if (path == null) {
				response.sendRedirect(request.getContextPath() + "/auth/login");
				return;
			}
	
			switch (path) {
			case "/login":
				showLoginPage(request, response);
				break;
			case "/register":
				showRegisterPage(request, response);
				break;
			case "/logout":
				handleLogout(request, response);
				break;
			case "/profile":
				showProfilePage(request, response);
				break;
			default:
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				break;
			}
		}
	
		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			String path = request.getPathInfo();
	
			if (path == null) {
				response.sendRedirect(request.getContextPath() + "/auth/login");
				return;
			}
	
			switch (path) {
			case "/login":
				handleLogin(request, response);
				break;
			case "/register":
				handleRegister(request, response);
				break;
			case "/update-profile":
				handleUpdateProfile(request, response);
				break;
			case "/change-password":
				handleChangePassword(request, response);
				break;
			default:
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				break;
			}
		}
	
		private void showLoginPage(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
			if (session != null && session.getAttribute("user") != null) {
				response.sendRedirect(request.getContextPath() + "/product");
				return;
			}
	
		
			String csrfToken = CSRFUtil.generateToken();
			request.getSession().setAttribute("csrfToken", csrfToken);
			request.setAttribute("csrfToken", csrfToken);
	
			String redirectUrl = request.getParameter("redirect");
			if (redirectUrl != null) {
				request.setAttribute("redirectUrl", redirectUrl);
			}
	
			request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
		}
	
		private void showRegisterPage(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
			if (session != null && session.getAttribute("user") != null) {
				response.sendRedirect(request.getContextPath() + "/product");
				return;
			}
	
		
			String csrfToken = CSRFUtil.generateToken();
			request.getSession().setAttribute("csrfToken", csrfToken);
			request.setAttribute("csrfToken", csrfToken);
	
			request.getRequestDispatcher("/jsp/register.jsp").forward(request, response);
		}
	
		private void showProfilePage(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
			User user = (User) session.getAttribute("user");
	
			if (user == null) {
				response.sendRedirect(request.getContextPath() + "/auth/login");
				return;
			}
	
			user = userDAO.findById(user.getUserId());
			session.setAttribute("user", user);
	
			// Generate CSRF token
			String csrfToken = CSRFUtil.generateToken();
			session.setAttribute("csrfToken", csrfToken);
			request.setAttribute("csrfToken", csrfToken);
	
			request.getRequestDispatcher("/jsp/profile.jsp").forward(request, response);
		}
	
		private void handleLogin(HttpServletRequest request, HttpServletResponse response)
		        throws ServletException, IOException {

		 
		    String csrfToken = request.getParameter("csrfToken");
		    String sessionToken = (String) request.getSession().getAttribute("csrfToken");
		    
		    if (csrfToken == null || !csrfToken.equals(sessionToken)) {
		        request.setAttribute("error", "Invalid request. Please try again.");
		        showLoginPage(request, response);
		        return;
		    }
		    

		    request.getSession().removeAttribute("csrfToken");

		    String username = request.getParameter("username");
		    String password = request.getParameter("password");
		    String rememberMe = request.getParameter("remember-me");

		    if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
		        request.setAttribute("error", "Username and password are required");
		        showLoginPage(request, response);
		        return;
		    }

		    username = username.trim();
		    
		    User user = userDAO.findByUsername(username);

		    if (user == null || !PasswordUtil.verifyPassword(password, user.getPasswordHash())) {
		        request.setAttribute("error", "Invalid username or password");
		        request.setAttribute("username", username);
		        showLoginPage(request, response);
		        return;
		    }

		    if (!user.isActive()) {
		        request.setAttribute("error", "Account is deactivated. Please contact administrator.");
		        showLoginPage(request, response);
		        return;
		    }
		    
		    if (user.isAdmin()) {
		        request.getSession().removeAttribute("redirectAfterLogin");
		    }

		    // ✅ Set user in session
		    HttpSession session = request.getSession();
		    session.setAttribute("user", user);
		    session.setAttribute("username", user.getUsername());
		    session.setAttribute("userId", user.getUserId());
		    session.setAttribute("role", user.getRole());

		    if ("on".equals(rememberMe)) {
		        Cookie usernameCookie = new Cookie("rememberedUsername", user.getUsername());
		        usernameCookie.setMaxAge(30 * 24 * 60 * 60);
		        usernameCookie.setHttpOnly(true);
		        response.addCookie(usernameCookie);
		    }

		
		    String redirectUrl = (String) session.getAttribute("redirectAfterLogin");
		    System.out.println("Redirect from session (filter): " + redirectUrl);
		    

		    if (redirectUrl != null) {
		        session.removeAttribute("redirectAfterLogin");
		    }
		    
		
		    if (redirectUrl == null || redirectUrl.isEmpty()) {
		        redirectUrl = request.getParameter("redirect");
		        System.out.println("Redirect from parameter: " + redirectUrl);
		    }
		    
	
		    if (redirectUrl == null || redirectUrl.isEmpty()) {
		        if (user.isAdmin()) {
		            redirectUrl = request.getContextPath() + "/admin/";
		            System.out.println("Using ADMIN default redirect: " + redirectUrl);
		        } else {
		            redirectUrl = request.getContextPath() + "/product";
		            System.out.println("Using USER default redirect: " + redirectUrl);
		        }
		    }
		    
		
		    if (user.isAdmin()) {
		        session.setAttribute(Constants.SESSION_FLASH_MESSAGE, 
		            "Welcome Administrator, " + user.getUsername() + "!");
		    } else {
		        session.setAttribute(Constants.SESSION_FLASH_MESSAGE, 
		            "Welcome back, " + user.getUsername() + "!");
		    }


		    System.out.println("FINAL REDIRECT TO: " + redirectUrl);
		    response.sendRedirect(redirectUrl);
		}
	
		private void handleRegister(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	

			String csrfToken = request.getParameter("csrfToken");
			String sessionToken = (String) request.getSession().getAttribute("csrfToken");
			
			if (csrfToken == null || !csrfToken.equals(sessionToken)) {
				request.setAttribute("error", "Invalid request. Please try again.");
				showRegisterPage(request, response);
				return;
			}
			
	
			request.getSession().removeAttribute("csrfToken");
	
			System.out.println("🔄 REGISTRATION PROCESS STARTED");
	
			String username = request.getParameter("username");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			String confirmPassword = request.getParameter("confirmPassword");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
	
			System.out.println("📝 Form parameters:");
			System.out.println("   Username: " + username);
			System.out.println("   Email: " + email);
	
			if (username == null || username.trim().isEmpty() || email == null || email.trim().isEmpty() || password == null
					|| password.trim().isEmpty()) {
				System.out.println("❌ Validation failed: Required fields empty");
				request.setAttribute("error", "Username, email and password are required");
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
				return;
			}
	
			username = username.trim();
			email = email.trim();
			password = password.trim();
			confirmPassword = confirmPassword != null ? confirmPassword.trim() : "";
	
			if (!password.equals(confirmPassword)) {
				System.out.println(" Passwords don't match");
				request.setAttribute("error", "Passwords do not match");
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
				return;
			}
	
			PasswordUtil.ValidationResult validation = PasswordUtil.validatePasswordComplexity(password);
			if (!validation.isValid()) {
				System.out.println("❌ Password complexity failed");
				request.setAttribute("error", validation.getErrorMessages());
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
				return;
			}
	
			if (userDAO.usernameExists(username)) {
				System.out.println(" Username already exists");
				request.setAttribute("error", "Username already taken");
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
				return;
			}
	
			if (userDAO.emailExists(email)) {
				System.out.println("❌ Email already exists");
				request.setAttribute("error", "Email already registered");
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
				return;
			}
	
			try {
				User user = new User();
				user.setUsername(username);
				user.setEmail(email);
				user.setPasswordHash(PasswordUtil.hashPassword(password));
				user.setFirstName(firstName != null && !firstName.trim().isEmpty() ? firstName.trim() : null);
				user.setLastName(lastName != null && !lastName.trim().isEmpty() ? lastName.trim() : null);
				user.setPhone(null);
				user.setAddress(null);
				user.setRole("CUSTOMER");
				user.setActive(true);
				user.setCreatedAt(new java.util.Date());
	
				int userId = userDAO.createUser(user);
	
				if (userId > 0) {
					HttpSession session = request.getSession();
					session.setAttribute(Constants.SESSION_FLASH_MESSAGE,
							"Registration successful! Please login with your credentials.");
	
					response.resetBuffer();
	
					String redirectUrl = request.getContextPath() + "/auth/login";
					System.out.println("🔗 Redirecting to: " + redirectUrl);
	
					response.sendRedirect(redirectUrl);
					return;
	
				} else {
					System.out.println("❌ User creation failed");
					request.setAttribute("error", "Registration failed. Please try again.");
					showRegisterPage(request, response);
				}
	
			} catch (SQLException e) {
				System.out.println("   Error: " + e.getMessage());
				String errorMsg = "Database error during registration.";
				if (e.getMessage().contains("active")) {
					errorMsg = "Database configuration error. Please contact administrator.";
				}
	
				request.setAttribute("error", errorMsg);
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
	
			} catch (Exception e) {
				System.out.println("❌ Unexpected exception:");
				e.printStackTrace();
	
				request.setAttribute("error", "Registration error: " + e.getMessage());
				request.setAttribute("username", username);
				request.setAttribute("email", email);
				request.setAttribute("firstName", firstName);
				request.setAttribute("lastName", lastName);
				showRegisterPage(request, response);
			}
		}
	
		private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
			User currentUser = (User) session.getAttribute("user");
	
			if (currentUser == null) {
				response.sendRedirect(request.getContextPath() + "/auth/login");
				return;
			}
	

			String csrfToken = request.getParameter("csrfToken");
			String sessionToken = (String) session.getAttribute("csrfToken");
			
			if (csrfToken == null || !csrfToken.equals(sessionToken)) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid request. Please try again.");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
			

			session.removeAttribute("csrfToken");
	
			String email = request.getParameter("email");
			String firstName = request.getParameter("firstName");
			String lastName = request.getParameter("lastName");
			String phone = request.getParameter("phone");
			String address = request.getParameter("address");
	
			if (email == null || email.trim().isEmpty()) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Email is required");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			User existingUser = userDAO.findByEmail(email.trim());
			if (existingUser != null && existingUser.getUserId() != currentUser.getUserId()) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Email already taken by another user");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			currentUser.setEmail(email.trim());
			currentUser.setFirstName(firstName != null ? firstName.trim() : null);
			currentUser.setLastName(lastName != null ? lastName.trim() : null);
			currentUser.setPhone(phone != null ? phone.trim() : null);
			currentUser.setAddress(address != null ? address.trim() : null);
	
			boolean updated = userDAO.updateUser(currentUser);
	
			if (updated) {
				currentUser = userDAO.findById(currentUser.getUserId());
				session.setAttribute("user", currentUser);
				session.setAttribute(Constants.SESSION_FLASH_MESSAGE, "Profile updated successfully!");
			} else {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Failed to update profile");
			}
	
			response.sendRedirect(request.getContextPath() + "/auth/profile");
		}
	
		private void handleChangePassword(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
			User currentUser = (User) session.getAttribute("user");
	
			if (currentUser == null) {
				response.sendRedirect(request.getContextPath() + "/auth/login");
				return;
			}
			
		
			String csrfToken = request.getParameter("csrfToken");
			String sessionToken = (String) session.getAttribute("csrfToken");
			
			if (csrfToken == null || !csrfToken.equals(sessionToken)) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Invalid request. Please try again.");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
			
			
			session.removeAttribute("csrfToken");
	
			String currentPassword = request.getParameter("currentPassword");
			String newPassword = request.getParameter("newPassword");
			String confirmPassword = request.getParameter("confirmPassword");
	
			if (currentPassword == null || currentPassword.trim().isEmpty() || newPassword == null
					|| newPassword.trim().isEmpty() || confirmPassword == null || confirmPassword.trim().isEmpty()) {
	
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "All password fields are required");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			if (!newPassword.equals(confirmPassword)) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "New passwords do not match");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPasswordHash())) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Current password is incorrect");
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			PasswordUtil.ValidationResult validation = PasswordUtil.validatePasswordComplexity(newPassword);
			if (!validation.isValid()) {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, validation.getErrorMessages());
				response.sendRedirect(request.getContextPath() + "/auth/profile");
				return;
			}
	
			String newPasswordHash = PasswordUtil.hashPassword(newPassword);
			boolean updated = userDAO.updatePassword(currentUser.getUserId(), newPasswordHash);
	
			if (updated) {
				currentUser = userDAO.findById(currentUser.getUserId());
				session.setAttribute("user", currentUser);
				session.setAttribute(Constants.SESSION_FLASH_MESSAGE, "Password changed successfully!");
			} else {
				session.setAttribute(Constants.SESSION_FLASH_ERROR, "Failed to change password");
			}
	
			response.sendRedirect(request.getContextPath() + "/auth/profile");
		}
	
		private void handleLogout(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
	
			HttpSession session = request.getSession(false);
	
			if (session != null) {
				session.invalidate();
			}
	
			Cookie usernameCookie = new Cookie("rememberedUsername", "");
			usernameCookie.setMaxAge(0);
			usernameCookie.setHttpOnly(true);
			response.addCookie(usernameCookie);
	
			response.sendRedirect(request.getContextPath() + "/auth/login?message=Logged out successfully");
		}
	}