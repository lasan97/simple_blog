package io.martin.inside.common.security.provider;

import io.martin.inside.common.security.token.CustomUsernamePasswordAuthenticationToken;
import io.martin.inside.common.security.user.CustomUser;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.AuthorizationServerContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.security.Principal;
import java.util.UUID;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class CustomUsernamePasswordAuthenticationProvider implements AuthenticationProvider {
	private final UserDetailsService userDetailsService;
	private final OAuth2AuthorizationService authorizationService;
	private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;
	private final PasswordEncoder passwordEncoder;

	public CustomUsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService,
														PasswordEncoder passwordEncoder,
														OAuth2AuthorizationService authorizationService,
														OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
		this.userDetailsService = userDetailsService;
		this.authorizationService = authorizationService;
		this.tokenGenerator = tokenGenerator;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		CustomUsernamePasswordAuthenticationToken passwordAuthentication = (CustomUsernamePasswordAuthenticationToken) authentication;

		OAuth2ClientAuthenticationToken clientPrincipal = CustomOAuth2AuthenticationProviderUtils.getAuthenticatedClientElseThrowInvalidClient(passwordAuthentication);
		RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

		CustomUser customUser = (CustomUser) passwordAuthentication.getUser();
		UserDetails user = userDetailsService.loadUserByUsername(customUser.getUsername());

		if(user == null) {
			throw new OAuth2AuthenticationException("invalid account");
		}

		if(!passwordEncoder.matches(customUser.getPassword(), user.getPassword())) {
			throw new OAuth2AuthenticationException("invalid account");
		}

		Authentication principal = new UsernamePasswordAuthenticationToken(user, Strings.EMPTY, user.getAuthorities());

		if (!isPrincipalAuthenticated(principal)) {
			return passwordAuthentication;
		}

		OAuth2Authorization authorization = authorizationBuilder(registeredClient, principal)
				.build();

		DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
				.registeredClient(registeredClient)
				.principal(principal)
				.authorizationServerContext(AuthorizationServerContextHolder.getContext())
				.authorization(authorization)
				.authorizedScopes(clientPrincipal.getRegisteredClient().getScopes())
				.authorizationGrantType(AuthorizationGrantType.PASSWORD)
				.authorizationGrant(passwordAuthentication);

		OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.from(authorization);

		// ----- Access token -----
		OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
		OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

		// generatedAccessToken.getTokenValue()
		OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER,
				generatedAccessToken.getTokenValue(), generatedAccessToken.getIssuedAt(),
				generatedAccessToken.getExpiresAt(), tokenContext.getAuthorizedScopes());

		authorizationBuilder.accessToken(accessToken);

		// ----- Refresh token -----
		tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
		OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);

		OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(UUID.randomUUID().toString(), generatedRefreshToken.getIssuedAt(), generatedRefreshToken.getExpiresAt());
		authorizationBuilder.refreshToken(refreshToken);

		authorization = authorizationBuilder.build();

		// TODO - 하나의 토큰만 허용할 경우 여기에 기존 유저 토큰 삭제 로직 추가
		this.authorizationService.save(authorization);

		return new OAuth2AccessTokenAuthenticationToken(
				registeredClient, clientPrincipal, accessToken, refreshToken);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return CustomUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	private static boolean isPrincipalAuthenticated(Authentication principal) {
		return principal != null &&
				!AnonymousAuthenticationToken.class.isAssignableFrom(principal.getClass()) &&
				principal.isAuthenticated();
	}

	private static OAuth2Authorization.Builder authorizationBuilder(RegisteredClient registeredClient, Authentication principal) {
		return OAuth2Authorization.withRegisteredClient(registeredClient)
				.principalName(principal.getName())
				.authorizationGrantType(AuthorizationGrantType.PASSWORD)
				.attribute(Principal.class.getName(), principal);
	}
}
