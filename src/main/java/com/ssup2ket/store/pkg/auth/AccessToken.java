package com.ssup2ket.store.pkg.auth;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AccessToken extends AbstractAuthenticationToken {
  public static final String ROLE_ADMIN = "admin";
  public static final String ROLE_USER = "user";

  private String token;
  private String userId;
  private String userLoginId;
  private String userRole;

  public AccessToken(String token, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.token = token;
  }

  public AccessToken(
      String token,
      String userId,
      String userLoginId,
      String userRole,
      Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.token = token;
    this.userId = userId;
    this.userLoginId = userLoginId;
    this.userRole = userRole;
  }

  @Override
  public String getPrincipal() {
    return userId;
  }

  @Override
  public Object getCredentials() {
    return null;
  }
}
