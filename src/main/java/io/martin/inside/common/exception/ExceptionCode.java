package io.martin.inside.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author martin
 * @since 2024.06.17
 * @description
 **********************************************************************************************************************/
@Getter
@AllArgsConstructor
public enum ExceptionCode {

	E00000001("오류가 발생하였습니다."),
	E00000002("찾을 수 없습니다."),

	// Account
	E00001001("사용자가 존재하지 않습니다.");


	private final String fescription;

}
