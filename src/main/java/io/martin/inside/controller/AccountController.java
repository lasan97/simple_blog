package io.martin.inside.controller;

import io.martin.inside.domain.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@RestController("/accounts")
@RequiredArgsConstructor
public class AccountController {

	private final AccountService accountService;

	@GetMapping("/accounts/me")
	@PreAuthorize("hasRole('USER')")
	public String getMyInfo() {
		return "Hello World";
	}
}
