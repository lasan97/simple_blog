package io.martin.inside.common.exception;

import io.martin.inside.common.helper.MessageHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@RestControllerAdvice
public class RestExceptionHandler {

	// TODO - Validation Exception 잡기
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<?> handleBindException(BindException exception){
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handleNotFoundException(NotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ExceptionForm.create(exception.getCode(), MessageHelper.getMessage(exception.getCode())));
	}
}
