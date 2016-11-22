package com.ownspec.center.configuration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.filter.GenericFilterBean;

import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * com.ownspec.center.configuration
 * <p>
 * JwtFilter
 *
 * @author lyrold
 * @since 2016-10-20
 */
public class JwtFilter extends GenericFilterBean {

  private final String cookieName;
  private final SecretKey secretKey;

  private final UserDetailsService userDetailsService;

  public JwtFilter(String cookieName, SecretKey secretKey, UserDetailsService userDetailsService) {
    this.cookieName = cookieName;
    this.secretKey = secretKey;
    this.userDetailsService = userDetailsService;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {

    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    try {
      //Get cookie
      Cookie requestCookie = Arrays.stream(request.getCookies())
          .filter(cookie -> cookieName.equals(cookie.getName()))
          .findFirst().orElse(null);

      if (requestCookie == null) {
        response.setStatus(401);
        return;
      }

      //Check token
      Claims claims = Jwts.parser()
          .setSigningKey(secretKey.getValue())
          .parseClaimsJws(requestCookie.getValue())
          .getBody();


      UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());

      SecurityContextHolder.getContext()
          .setAuthentication(new UsernamePasswordAuthenticationToken(user, claims, user.getAuthorities()));

      chain.doFilter(req, res);

    } catch (Exception exception) {
      response.setStatus(401);
    } finally {
      SecurityContextHolder.clearContext();
    }

  }
}
