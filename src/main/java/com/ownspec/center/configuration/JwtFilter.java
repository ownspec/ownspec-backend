package com.ownspec.center.configuration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  @Value("${server.session.cookie.name}")
  private String cookieName;

  @Autowired
  private SecurityConfiguration.SecretKey secretKey;

  @Override
  public void doFilter(final ServletRequest req,
                       final ServletResponse res,
                       final FilterChain chain) throws IOException, ServletException {
    final HttpServletRequest request = (HttpServletRequest) req;
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
      request.setAttribute("claims", claims);

    } catch (Exception exception) {
      throw new ServletException("Missing or invalid token");
    }

    chain.doFilter(req, res);
  }
}
