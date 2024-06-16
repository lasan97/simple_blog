package io.martin.inside.common.security.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.martin.inside.domain.account.Account;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@JsonDeserialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomUser implements UserDetails, CredentialsContainer {

	public CustomUser() {
	}

	public CustomUser(String username, String password) {
		this.username = username;
		this.password = password;
		this.authorities = new ArrayList<>();
	}

	public CustomUser(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
	}

	public CustomUser(Account account, Collection<? extends GrantedAuthority> authorities) {
		this.id = account.getId();
		this.username = account.getName();
		this.password = account.getPassword();
		this.authorities = authorities;
	}

	private Long id;

	private String username;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Long getId() {
		return id;
	}

	@Override
	public void eraseCredentials() {
		this.password = null;
	}
}
