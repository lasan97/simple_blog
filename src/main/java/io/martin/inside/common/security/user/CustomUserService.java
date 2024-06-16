package io.martin.inside.common.security.user;

import io.martin.inside.domain.account.Account;
import io.martin.inside.domain.account.AccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Transactional(readOnly=true)
public class CustomUserService implements UserDetailsService {

	private final AccountRepository accountRepository;

	public CustomUserService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Account account = accountRepository.findByName(username).orElseThrow(() -> new OAuth2AuthenticationException("invalid account"));
		return new CustomUser(account, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
	}
}
