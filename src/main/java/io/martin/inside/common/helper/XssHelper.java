package io.martin.inside.common.helper;

import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

/**
 * @since       2024.06.19
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class XssHelper {

	private static final PolicyFactory POLICY = Sanitizers.FORMATTING
			.and(Sanitizers.STYLES)
			.and(Sanitizers.LINKS)
			.and(Sanitizers.IMAGES)
			.and(Sanitizers.TABLES)
			.and(Sanitizers.BLOCKS);

	public static String sanitizeXSS(String value) {
		if(value == null) {
			return null;
		}
		String passingValue = StringEscapeUtils.unescapeHtml4(value);
		return StringEscapeUtils.unescapeHtml4(POLICY.sanitize(passingValue));
	}
}
