package com.hivetech.kanban.interceptor;

import com.hivetech.kanban.service.UserDetailService;
import com.hivetech.kanban.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;
    private final UserDetailService userDetailsService;

    public WebSocketHandshakeInterceptor(JwtUtil jwtUtil, UserDetailService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        String token = null;
        if (request instanceof ServletServerHttpRequest servletReq) {
            HttpServletRequest httpReq = servletReq.getServletRequest();
            token = httpReq.getParameter("token");
            if (token == null) {
                String authHeader = httpReq.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
        }

        if (token == null) {
            return false;
        }

        try {
            String username = jwtUtil.extractUsername(token);
            if (username == null) {
                return false;
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!jwtUtil.isTokenValid(token, userDetails.getUsername())) {
                return false;
            }

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            attributes.put("principal", auth);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) { }
}

