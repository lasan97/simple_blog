package io.martin.inside.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author martin
 * @description
 * @since 2024.06.16
 **********************************************************************************************************************/
@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findByName(String name);
}
