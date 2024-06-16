package io.martin.inside.domain.account.form;

import io.martin.inside.domain.account.Account;
import lombok.Getter;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class AccountServiceForm {

	public static class Response {

		@Getter
		public static class PublicInfoAccount {

			private Long id;

			private String name;

			public PublicInfoAccount(Account account) {
				this.id = account.getId();
				this.name = account.getName();
			}
		}
	}
}
