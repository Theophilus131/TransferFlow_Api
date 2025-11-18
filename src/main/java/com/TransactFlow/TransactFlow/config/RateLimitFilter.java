package com.TransactFlow.TransactFlow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> rateLimitBuckets;
    private final RateLimitConfig rateLimitConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
                                    ) throws ServletException, IOException{

        String path =  request.getRequestURI();

        if(path.contains("/api/auth/register") || path.contains("/api/auth/login")){
            filterChain.doFilter(request,response);
            return;
        }

        String userKey = getUserKey(request, path);

        Bucket bucket = rateLimitBuckets.computeIfAbsent(userKey, k -> {
            if (path.startsWith("/api/auth")) {
                return rateLimitConfig.createAuthBucket();
            } else {
                return rateLimitConfig.createNewBucket();
            }
        });

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {

            long waitForRefill = probe.getNanosToWaitForRefill() / 1_000_000_000;

            log.warn("Rate limit exceeded for user: {} on path: {}", userKey, path);

//            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json");
            response.addHeader("X-RateLimit-Retry-After-Seconds", String.valueOf(waitForRefill));

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", LocalDateTime.now());
            errorResponse.put("status", 429);
            errorResponse.put("error", "Too Many Requests");
            errorResponse.put("message", "Rate limit exceeded. Please try again in " + waitForRefill + " seconds");
            errorResponse.put("retryAfter", waitForRefill + " seconds");

            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    private String getUserKey(HttpServletRequest request, String path) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        if (authentication != null && authentication.isAuthenticated()
                && !authentication.getPrincipal().equals("anonymousUser")) {
            return authentication.getName();
        }


        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return "ip:" + ip;
    }

    }


