package io.martin.inside.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @since       2024.06.17
 * @author      martin
 * @description 
 **********************************************************************************************************************/
@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class BlogPostService {

	private final BlogPostRepository blogPostRepository;
}
