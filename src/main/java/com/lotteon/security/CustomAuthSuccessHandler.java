package com.lotteon.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.net.URLEncoder;

public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 사용자 권한(Role) 확인
        boolean isMember = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MEMBER"));
        boolean isSeller = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SELLER"));
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        // 역할에 따라 리다이렉트
        if (isMember) {
            response.sendRedirect("/"); // 회원일 경우 이동할 경로
        } else if (isSeller || isAdmin) {
            response.sendRedirect("/admin/main"); // 판매자일 경우 이동할 경로
        } else {
            response.sendRedirect("/user/login");
        }
    }
}
