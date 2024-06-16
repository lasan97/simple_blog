package io.martin.inside.common.security.introspector;

import io.martin.inside.common.security.user.CustomUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class CustomOpaqueTokenIntrospector implements OpaqueTokenIntrospector {

	private final OAuth2AuthorizationService authorizationService;

	public CustomOpaqueTokenIntrospector(OAuth2AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}

	@Override
	public OAuth2AuthenticatedPrincipal introspect(String token) {

		OAuth2Authorization authorization = authorizationService.findByToken(token, OAuth2TokenType.ACCESS_TOKEN);

		if(authorization == null) {
			throw new BadOpaqueTokenException("Access token is null");
		}

		Authentication authentication = authorization.getAttribute(Principal.class.getName());

		if(authentication == null) {
			throw new BadOpaqueTokenException("Access token is null");
		}

		CustomUser customUser = (CustomUser) authentication.getPrincipal();
		OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getAccessToken();
		Map<String, Object> attributes = new HashMap<>();
		if(!authorization.getAccessToken().isActive()) {
			throw new BadOpaqueTokenException("Provided token isn't active");
		}
		attributes.put(OAuth2TokenIntrospectionClaimNames.ACTIVE, true);
		attributes.put(OAuth2TokenIntrospectionClaimNames.IAT, accessToken.getToken().getIssuedAt());
		attributes.put(OAuth2TokenIntrospectionClaimNames.EXP, accessToken.getToken().getExpiresAt());
		attributes.put(OAuth2TokenIntrospectionClaimNames.TOKEN_TYPE, accessToken.getToken().getTokenType().getValue());
		attributes.put(OAuth2TokenIntrospectionClaimNames.SCOPE, customUser.getAuthorities());
		attributes.put("id", customUser.getId());

		return new DefaultOAuth2AuthenticatedPrincipal(customUser.getUsername(), attributes, (Collection<GrantedAuthority>) customUser.getAuthorities());
	}
}
