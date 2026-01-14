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
        
        // Remove context path to get the resource path
        String resourcePath = requestURI.substring(contextPath.length());

        boolean isLoginPage = requestURI.equals(contextPath + "/auth/login")
                || requestURI.equals(contextPath + "/login.jsp");
        boolean isRegisterPage = requestURI.equals(contextPath + "/auth/register")
                || requestURI.equals(contextPath + "/register.jsp");
        boolean isProductPage = requestURI.equals(contextPath + "/product")
                || requestURI.startsWith(contextPath + "/product?");
        boolean isCartPage = requestURI.equals(contextPath + "/cart");
        boolean isCheckoutPage = requestURI.equals(contextPath + "/checkout");
        boolean isOrdersPage = requestURI.startsWith(contextPath + "/orders");
        boolean isProfilePage = requestURI.equals(contextPath + "/auth/profile");
        boolean isStaticResource = requestURI.contains("/css/") 
                || requestURI.contains("/js/")
                || requestURI.contains("/images/")
                || requestURI.endsWith(".css")
                || requestURI.endsWith(".js")
                || requestURI.endsWith(".png")
                || requestURI.endsWith(".jpg")
                || requestURI.endsWith(".gif");
        
        // Homepage (index.jsp) - ALWAYS ALLOWED
        boolean isHomePage = requestURI.equals(contextPath + "/")
                || requestURI.equals(contextPath + "/index.jsp")
                || requestURI.equals(contextPath + "/index.html");

        boolean loggedIn = session != null && session.getAttribute("user") != null;

        
      
        if (isHomePage) {
            System.out.println("  Allowing homepage access");
            chain.doFilter(request, response);
            return;
        }
        
        // If it's a static resource, ALWAYS allow
        if (isStaticResource) {
            chain.doFilter(request, response);
            return;
        }
        
        if ((isLoginPage || isRegisterPage) && loggedIn) {
            response.sendRedirect(contextPath + "/product");
            return;
        }
        
        if (!loggedIn && (isProductPage || isCartPage || isCheckoutPage || isOrdersPage || isProfilePage)) {
            System.out.println("   → Redirecting to login (protected page accessed)");
            
            if (session == null) {
                session = request.getSession(true);
            }
            session.setAttribute("redirectAfterLogin", requestURI);
            
            response.sendRedirect(contextPath + "/auth/login");
            return;
        }
        
        chain.doFilter(request, response);
    }

}
