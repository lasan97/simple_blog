package io.martin.inside.domain.account;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

	@NotBlank(message="V00001001")
	@Size(max=10, message="V00001002")
	private String name;

	@NotBlank(message="V00001003")
	@Size(max=100, message="V00001004")
	private String password;

	public Account(String name, String password) {
		this.name = name;
		this.password = password;
	}
}
