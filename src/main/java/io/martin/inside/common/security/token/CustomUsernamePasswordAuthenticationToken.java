package io.martin.inside.common.security.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class CustomUsernamePasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken {

	private final Object user;

	public CustomUsernamePasswordAuthenticationToken(Object principal,
													 Authentication clientPrincipal, Map<String, Object> additionalParameters
	) {
		super(AuthorizationGrantType.PASSWORD, clientPrincipal, additionalParameters);
		Assert.notNull(principal, "principal cannot be null");
		this.user = principal;
	}

	public Object getUser() {
		return user;
	}
}
