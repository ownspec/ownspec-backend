package com.ownspec.center.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * com.ownspec.center.configuration
 * <p>
 * JwtFilter
 *
 * @author lyrold
 * @since 2016-10-20
 */
public class JwtFilter extends GenericFilterBean {

  private String cookieName;
  private SecurityConfiguration.SecretKey secretKey;

  public JwtFilter(String cookieName, SecurityConfiguration.SecretKey secretKey) {
    this.cookieName = cookieName;
    this.secretKey = secretKey;
  }

  @Override
  public void doFilter(final ServletRequest req,
                       final ServletResponse res,
                       final FilterChain chain) throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
    if (!request.getRequestURI().equals("/api/users/login")) {
      try {
        //Get cookie
        Cookie requestCookie = Arrays.stream(request.getCookies())
                                     .filter(cookie -> cookie.getName().equals(cookieName))
                                     .findFirst().get();

        //Check token
        final Claims claims = Jwts.parser()
                                  .setSigningKey(secretKey.getValue())
                                  .parseClaimsJws(requestCookie.getValue())
                                  .getBody();
        req.setAttribute("claims", claims);
      } catch (Exception exception) {
        throw new ServletException("Missing or invalid token");
      }
    }
    chain.doFilter(req, res);
  }
}
