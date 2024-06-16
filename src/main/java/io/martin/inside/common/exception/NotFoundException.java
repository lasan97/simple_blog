package io.martin.inside.common.exception;

import lombok.Getter;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Getter
public class NotFoundException extends RuntimeException{

	private ExceptionCode code;

	public NotFoundException(){
		super(ExceptionCode.E00000002.name());
		code = ExceptionCode.E00000002;
	}

	public NotFoundException(ExceptionCode exceptionCode){
		super(exceptionCode.name());
		code = exceptionCode;
	}

	public NotFoundException(ExceptionCode exceptionCode, Exception exception){
		super(exceptionCode.name(), exception);
		code = exceptionCode;
	}
}
