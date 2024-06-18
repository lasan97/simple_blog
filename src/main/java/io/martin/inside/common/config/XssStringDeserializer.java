package io.martin.inside.common.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.martin.inside.common.helper.XssHelper;

import java.io.IOException;

/**
 * @since       2024.06.19
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class XssStringDeserializer extends StdDeserializer<String> {

	public XssStringDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
		return XssHelper.sanitizeXSS(jsonParser.getValueAsString());
	}
}
