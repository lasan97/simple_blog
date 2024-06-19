package io.martin.inside.controller;

import io.martin.inside.common.security.helper.SecurityHelper;
import io.martin.inside.controller.form.AccountControllerForm.Response;
import io.martin.inside.domain.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@RestController
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	// TODO - Form Validation 추가, 테스트 코드 작성
	@GetMapping("/accounts/me")
	@PreAuthorize("hasRole('USER')")
	public Response.PublicInfoAccount getMyInfo() {
		return new Response.PublicInfoAccount(accountService.get(SecurityHelper.getUserId()));
	}
}
