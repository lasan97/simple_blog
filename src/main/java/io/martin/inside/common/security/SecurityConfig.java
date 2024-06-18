package io.martin.inside.common.security;

import io.martin.inside.common.security.converter.CustomAccessTokenRequestConverter;
import io.martin.inside.common.security.introspector.CustomOpaqueTokenIntrospector;
import io.martin.inside.common.security.provider.CustomUsernamePasswordAuthenticationProvider;
import io.martin.inside.common.security.user.CustomUserService;
import io.martin.inside.domain.account.Account;
import io.martin.inside.domain.account.AccountRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2AccessTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2RefreshTokenGenerator;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Configuration(proxyBeanMethods=false)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig {

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	public SecurityFilterChain authorizationServerSecurityFilterChain(
			HttpSecurity http,
			UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder,
			OAuth2AuthorizationService oAuth2AuthorizationService,
			OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator
	) throws Exception {
		OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
		http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
				.tokenEndpoint(tokenEndpoint ->{
					tokenEndpoint.accessTokenRequestConverter(
							new DelegatingAuthenticationConverter(
									Arrays.asList(
											new OAuth2AuthorizationCodeAuthenticationConverter(),
											new OAuth2RefreshTokenAuthenticationConverter(),
											new OAuth2ClientCredentialsAuthenticationConverter(),
											new CustomAccessTokenRequestConverter())));
					tokenEndpoint.authenticationProviders(authenticationProviders ->
							authenticationProviders.addAll(List.of(
									new CustomUsernamePasswordAuthenticationProvider(userDetailsService, passwordEncoder, oAuth2AuthorizationService, tokenGenerator),
									new OAuth2RefreshTokenAuthenticationProvider(oAuth2AuthorizationService, tokenGenerator)))
					);
				});

		return http.build();
	}

	@Bean
	@Order(1)
	public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
														  OAuth2AuthorizationService oAuth2AuthorizationService) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(opaqueToken -> opaqueToken.introspector(new CustomOpaqueTokenIntrospector(oAuth2AuthorizationService))))
				.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated());

		return http.build();
	}

	@Bean
	@ConditionalOnProperty(name = "spring.h2.console.enabled",havingValue = "true")
	public WebSecurityCustomizer configureH2ConsoleEnable() {
		return web -> web.ignoring()
				.requestMatchers(PathRequest.toH2Console());
	}

	@Bean
	public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository){
		return new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
	}

	@Bean
	public OAuth2TokenGenerator<? extends OAuth2Token> oAuth2TokenGenerator() {
		return new DelegatingOAuth2TokenGenerator(
				new OAuth2AccessTokenGenerator(),
				new OAuth2RefreshTokenGenerator());
	}

	@Bean
	public AuthorizationServerSettings authorizationServerSettings(){
		return AuthorizationServerSettings.builder().issuer("http://localhost:9000").build();
	}

	@Bean
	public PasswordEncoder passwordEncoder(){
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public UserDetailsService userDetailsService(AccountRepository accountRepository) {
		return new CustomUserService(accountRepository);
	}

	@Bean
	public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate, AccountRepository accountRepository, PasswordEncoder passwordEncoder){

		JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        jdbcRegisteredClientRepository.save(getRegisteredClient("martin1", "{noop}secret1"));
		accountRepository.save(new Account("martin", passwordEncoder.encode("1234")));

		return jdbcRegisteredClientRepository;
	}

	private RegisteredClient getRegisteredClient(String clientId, String clientSecret) {
		return RegisteredClient.withId(UUID.randomUUID().toString())
				.clientId(clientId)
				.clientSecret(clientSecret)
				.clientName(clientId)
				.clientIdIssuedAt(Instant.now())
				.clientSecretExpiresAt(null)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
				.clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
				.authorizationGrantType(AuthorizationGrantType.PASSWORD)
				.authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
				.redirectUri("http://127.0.0.1:8080")
				.scope(OidcScopes.PROFILE)
				.scope(OidcScopes.EMAIL)
				.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
				.tokenSettings(getTokenSettings(7200L, 86400L))
				.build();
	}

	private TokenSettings getTokenSettings(Long accessTokenDurationOfSeconds, Long refreshTokenDurationOfSeconds) {
		return TokenSettings.builder()
				.accessTokenFormat(OAuth2TokenFormat.REFERENCE)
				.accessTokenTimeToLive(Duration.ofSeconds(accessTokenDurationOfSeconds))
				.refreshTokenTimeToLive(Duration.ofSeconds(refreshTokenDurationOfSeconds))
				.build();
	}
}
