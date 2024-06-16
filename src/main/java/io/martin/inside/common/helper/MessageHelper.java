package io.martin.inside.common.helper;

import io.martin.inside.common.exception.ExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Component
public class MessageHelper {

	private static MessageSource messageSource = null;

	@Autowired
	private MessageHelper(MessageSource messageSource) {
		MessageHelper.messageSource = messageSource;
	}

	public static String getMessage(ExceptionCode exceptionCode){
		return messageSource.getMessage(exceptionCode.name(), null, null);
	}
}
