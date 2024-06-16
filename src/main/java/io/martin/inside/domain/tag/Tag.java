package io.martin.inside.domain.tag;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Entity
public class Tag {

	@Id
	private String id;
}
