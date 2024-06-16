package io.martin.inside.domain.post;

import io.martin.inside.domain.tag.Tag;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @since       2024.06.16
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Entity
@Getter
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class BlogPost {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String title;

	@NotBlank
	private String content;

	@OneToMany
	@JoinTable(name="blog_post_tag", joinColumns=@JoinColumn(name = "blog_post_id"), inverseJoinColumns=@JoinColumn(name="tag_id"))
	private List<Tag> tags = new ArrayList<>();

	@Builder
	private BlogPost(String title, String content, List<Tag> tags) {
		this.title = title;
		this.content = content;
		this.tags = tags;
	}

	private void modify(String title, String content, List<Tag> tags) {
		this.title = title;
		this.content = content;
		this.tags.clear();
		this.tags.addAll(tags);
	}
}
