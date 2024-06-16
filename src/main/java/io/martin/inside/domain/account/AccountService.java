package io.martin.inside.domain.account;

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

	public void get(Long accountId) {
		accountRepository.findById(accountId).orElse(null);
	}
}
