package io.martin.inside.controller.form;

import io.martin.inside.domain.account.form.AccountServiceForm;
import lombok.Data;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
public class AccountControllerForm {

	public class Response {

		@Data
		public static class PublicInfoAccount {

			private Long id;

			private String name;

			public PublicInfoAccount(AccountServiceForm.Response.PublicInfoAccount publicInfoAccount) {
				this.id = publicInfoAccount.getId();
				this.name = publicInfoAccount.getName();
			}
		}
	}
}
