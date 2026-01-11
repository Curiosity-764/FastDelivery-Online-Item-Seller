<%@ page contentType="text/html;charset=UTF-8" language="java" %>
        </div> <!-- End main content -->
    </div> <!-- End row -->
</div> <!-- End container-fluid -->

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Add active class to current menu
    document.addEventListener('DOMContentLoaded', function() {
        const path = window.location.pathname;
        const links = document.querySelectorAll('.sidebar-link');
        
        links.forEach(link => {
            // Remove active class
            link.style.background = '';
            link.style.color = '#495057';
            
            // Check if this link matches current page
            const linkPath = link.getAttribute('href');
            if (linkPath && path.includes(linkPath.split('/').pop())) {
                link.style.background = '#0d6efd';
                link.style.color = 'white';
            }
        });
        
        // Also check for dashboard
        if (path.endsWith('/admin') || path.endsWith('/admin/')) {
            const dashboardLink = document.querySelector('a[href$="/admin"]');
            if (dashboardLink) {
                dashboardLink.style.background = '#0d6efd';
                dashboardLink.style.color = 'white';
            }
        }
    });
</script>
</body>
</html>