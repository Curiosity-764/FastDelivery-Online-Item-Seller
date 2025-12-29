package Filters;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		HttpSession session = request.getSession(false);

		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();

		boolean isLoginPage = requestURI.equals(contextPath + "/auth/login")
				|| requestURI.equals(contextPath + "/login.jsp");
		boolean isRegisterPage = requestURI.equals(contextPath + "/auth/register")
				|| requestURI.equals(contextPath + "/register.jsp");
		boolean isProductPage = requestURI.equals(contextPath + "/product")
				|| requestURI.startsWith(contextPath + "/product?");
		boolean isStaticResource = requestURI.contains("/css/") || requestURI.contains("/js/")
				|| requestURI.contains("/images/");

		boolean loggedIn = session != null && session.getAttribute("user") != null;
		// check xem co dang login khong
		if (isLoginPage || isRegisterPage || isProductPage || isStaticResource || loggedIn) {
			chain.doFilter(request, response);
		} else {
			session.setAttribute("redirectAfterLogin", requestURI);
			response.sendRedirect(contextPath + "/auth/login");
		}
	}
}