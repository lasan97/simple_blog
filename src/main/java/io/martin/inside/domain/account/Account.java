package io.martin.inside.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class Account {

	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String password;

	public Account(String name, String password) {
		this.name = name;
		this.password = password;
	}
}
