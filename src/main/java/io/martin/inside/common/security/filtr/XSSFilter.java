package io.martin.inside.common.security.filtr;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.text.StringEscapeUtils;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;
import org.springframework.stereotype.Component;

import java.io.*;

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
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
	}

	@Override
	public void destroy() {
	}

	private static class XSSRequestWrapper extends HttpServletRequestWrapper {

		private String body;
		private static final PolicyFactory POLICY = Sanitizers.FORMATTING
				.and(Sanitizers.STYLES)
				.and(Sanitizers.LINKS)
				.and(Sanitizers.IMAGES)
				.and(Sanitizers.TABLES)
				.and(Sanitizers.BLOCKS);

		public XSSRequestWrapper(HttpServletRequest servletRequest) {
			super(servletRequest);
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {

			body = sanitizeRequestBody(super.getInputStream());

			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
			return new ServletInputStream() {
				@Override
				public int read() throws IOException {
					return byteArrayInputStream.read();
				}

				@Override
				public boolean isFinished() {
					return byteArrayInputStream.available() == 0;
				}

				@Override
				public boolean isReady() {
					return true;
				}

				@Override
				public void setReadListener(ReadListener readListener) {
				}
			};
		}

		private String sanitizeRequestBody(ServletInputStream servletInputStream) throws IOException {
			StringBuilder stringBuilder = new StringBuilder();
			try  {
				BufferedReader bufferedReader = servletInputStream != null ? new BufferedReader(new InputStreamReader(servletInputStream)) : null;
				if (bufferedReader != null) {
					char[] charBuffer = new char[128];
					int bytesRead;
					while ((bytesRead = bufferedReader.read(charBuffer)) != -1) {
						stringBuilder.append(charBuffer, 0, bytesRead);
					}
				}
			} catch (IOException ex) {
				throw ex;
			}
			return stripXSS(stringBuilder.toString());
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
			if(value == null) {
				return null;
			}
			String passingValue = StringEscapeUtils.unescapeHtml4(value);
			return StringEscapeUtils.unescapeHtml4(POLICY.sanitize(passingValue));
		}
	}
}
