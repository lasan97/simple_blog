package io.martin.inside.domain.account;

import io.martin.inside.common.exception.ExceptionCode;
import io.martin.inside.common.exception.NotFoundException;
import io.martin.inside.domain.account.form.AccountServiceForm.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class AccountService {

	private final AccountRepository accountRepository;

	public Response.PublicInfoAccount get(Long accountId) {
		Account account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException(ExceptionCode.E00001001));
		return new Response.PublicInfoAccount(account);
	}
}
