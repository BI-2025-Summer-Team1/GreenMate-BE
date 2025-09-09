package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.bi.greenmate.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	boolean existsByNickname(String nickname);

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		  update User u
		  set u.deletedAt = CURRENT_TIMESTAMP
		  where u.id = :userId
		    and u.deletedAt is null
		""")
	int markDeletedIfNotYet(Long userId);
}
