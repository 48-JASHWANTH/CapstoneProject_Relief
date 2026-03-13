package org.hartford.relief.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.hartford.relief.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtFilter extends GenericFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    @org.springframework.context.annotation.Lazy
    private UserDetailsService userService;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        String header = req.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            if (jwtUtil.validateToken(token)) {
                try {
                    String email = jwtUtil.extractUsername(token);
                    UserDetails user = userService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities());

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } catch (Exception e) {
                    // Token is valid but user not found or other error — skip authentication
                    SecurityContextHolder.clearContext();
                }
            }
        }

        chain.doFilter(request, response);
    }
}
