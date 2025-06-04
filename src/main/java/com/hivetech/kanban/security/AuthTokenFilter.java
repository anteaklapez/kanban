package com.hivetech.kanban.security;

import com.hivetech.kanban.service.KanbanUserDetailsService;
import com.hivetech.kanban.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Component that handles the authentication of any request by utilizing JWT service.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {
    private final KanbanUserDetailsService userService;
    private final JwtUtil jwtUtil;

    Logger logger = Logger.getLogger(String.valueOf(AuthTokenFilter.class));

    @Autowired
    public AuthTokenFilter(KanbanUserDetailsService userService, JwtUtil jwtUtil){
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Processes the request based on the jwtToken found inside. Tries to retrieve a user from the database
     * and return appropriate response. In case of user not existing or an invalid token, the response is
     * SC_FORBIDDEN (403) and nothing is returned. Otherwise, the response is 200.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param filterChain FilterChain
     * @throws ServletException if an error with servlet occurs
     * @throws IOException if an error with input/output occurs
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = request.getHeader("Authorization");

        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // jwt token is everything after "Bearer " (7)
        String jwtToken = requestTokenHeader.substring(7);
        try {
            // extract username from jwt token to check whether they already exist in db
            String username = jwtUtil.extractUsername(jwtToken);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // getting user details from db
                UserDetails userDetails = this.userService.loadUserByUsername(username);

                // check if the user is valid or not
                if (jwtUtil.isTokenValid(jwtToken, userDetails.getUsername())) {
                    // this token is needed by spring and security context holder to update security context
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
            } else {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            // always need to do the next filter in the chain!!
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            logger.log(Level.WARNING, e.getMessage());
        }

    }

}
