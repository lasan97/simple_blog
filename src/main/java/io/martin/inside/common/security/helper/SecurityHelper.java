package io.martin.inside.common.security.helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class SecurityHelper {

	public static Long getUserId() {

		SecurityContext context = SecurityContextHolder.getContext();

		if (context == null) {
			return null;
		}

		Authentication authentication = context.getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}

		Object principal = authentication.getPrincipal();
		if (principal instanceof DefaultOAuth2AuthenticatedPrincipal defaultOAuth2AuthenticatedPrincipal) {
			return defaultOAuth2AuthenticatedPrincipal.getAttribute("id");
		}

		return null;
	}
}
