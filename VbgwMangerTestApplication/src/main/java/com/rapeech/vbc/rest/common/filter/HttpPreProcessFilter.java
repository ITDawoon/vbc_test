package com.rapeech.vbc.rest.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@WebFilter(urlPatterns= "/*")
public class HttpPreProcessFilter  extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")
                || request.getMethod().equalsIgnoreCase("PUT")
                || request.getMethod().equalsIgnoreCase("DELETE")
                || request.getMethod().equalsIgnoreCase("TRACE")
                || request.getMethod().equalsIgnoreCase("PATCH")
                || request.getMethod().equalsIgnoreCase("OPTION")) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            response.setStatus(200); // 응답 status code 200

            return;
        }

        filterChain.doFilter(request, response);
    }
}