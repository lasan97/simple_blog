package io.martin.inside.common.exception;

import lombok.Getter;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Getter
public class ExceptionForm {

	private ExceptionCode code;
	private String        message;

	private ExceptionForm(ExceptionCode code, String message) {
		if (code == null) {
			throw new IllegalArgumentException("ExceptionCode cannot be null");
		}

		this.code = code;
		this.message = message;
	}

	public static ExceptionForm create(ExceptionCode code){
		return new ExceptionForm(code, null);
	}

	public static ExceptionForm create(ExceptionCode code, String message){
		return new ExceptionForm(code, message);
	}
}
