package io.martin.inside.common.security.filtr;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Component
public class XSSFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {
	}

	private static class XSSRequestWrapper extends HttpServletRequestWrapper {

		public XSSRequestWrapper(HttpServletRequest servletRequest) {
			super(servletRequest);
		}

		@Override
		public String[] getParameterValues(String parameter) {
			String[] values = super.getParameterValues(parameter);
			if (values == null) {
				return null;
			}

			int count = values.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = stripXSS(values[i]);
			}

			return encodedValues;
		}

		@Override
		public String getParameter(String parameter) {
			String value = super.getParameter(parameter);
			return stripXSS(value);
		}

		@Override
		public String getHeader(String name) {
			String value = super.getHeader(name);
			return stripXSS(value);
		}

		private String stripXSS(String value) {
			if (value != null) {

				// Avoid null characters
				value = value.replaceAll("\0", "");

				// Remove all sections that match a pattern
				value = value.replaceAll("(?i)<script.*?>.*?</script.*?>", ""); // script tags
				value = value.replaceAll("(?i)<.*?javascript:.*?>.*?</.*?>", ""); // javascript tags
				value = value.replaceAll("(?i)<.*?\\s+on.*?>.*?</.*?>", ""); // onload/onerror/on* attributes
			}
			return value;
		}
	}
}
