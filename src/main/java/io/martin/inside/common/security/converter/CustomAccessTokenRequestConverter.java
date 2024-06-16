package io.martin.inside.common.security.converter;

import io.martin.inside.common.security.token.CustomUsernamePasswordAuthenticationToken;
import io.martin.inside.common.security.user.CustomUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class CustomAccessTokenRequestConverter implements AuthenticationConverter {

	private String ACCESS_TOKEN_REQUEST_ERROR_URI = "https://datatracker.ietf.org/doc/html/rfc6749#section-5.2";

	@Override
	public Authentication convert(HttpServletRequest request) {

		String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
		if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
			return null;
		}

		String username = request.getParameter(OAuth2ParameterNames.USERNAME);
		Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
		MultiValueMap<String, String> parameters = getParameters(request);
		String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);

		if (!StringUtils.hasText(password) ||
				parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
			throwError(
					OAuth2ErrorCodes.INVALID_REQUEST,
					OAuth2ParameterNames.PASSWORD,
					ACCESS_TOKEN_REQUEST_ERROR_URI);
		}

		Map<String, Object> additionalParameters = new HashMap<>();
		parameters.forEach((key, value) -> {
			if (!key.equals(OAuth2ParameterNames.GRANT_TYPE) &&
					!key.equals(OAuth2ParameterNames.CLIENT_ID) &&
					!key.equals(OAuth2ParameterNames.CODE) &&
					!key.equals(OAuth2ParameterNames.USERNAME) &&
					!key.equals(OAuth2ParameterNames.PASSWORD)) {
				additionalParameters.put(key, value.get(0));
			}
		});

		return new CustomUsernamePasswordAuthenticationToken(new CustomUser(username, password), clientPrincipal, additionalParameters);
	}

	static MultiValueMap<String, String> getParameters(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>(parameterMap.size());
		parameterMap.forEach((key, values) -> {
			if (values.length > 0) {
				for (String value : values) {
					parameters.add(key, value);
				}
			}
		});
		return parameters;
	}

	static void throwError(String errorCode, String parameterName, String errorUri) {
		OAuth2Error error = new OAuth2Error(errorCode, "OAuth 2.0 Parameter: " + parameterName, errorUri);
		throw new OAuth2AuthenticationException(error);
	}
}
