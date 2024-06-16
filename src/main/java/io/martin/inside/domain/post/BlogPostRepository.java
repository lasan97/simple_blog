package io.martin.inside.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author martin
 * @description
 * @since 2024.06.17
 **********************************************************************************************************************/
@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

}
