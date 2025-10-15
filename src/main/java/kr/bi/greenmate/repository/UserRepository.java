package kr.bi.greenmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import kr.bi.greenmate.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmailAndDeletedAtIsNull(String email);

	boolean existsByNicknameAndDeletedAtIsNull(String nickname);
}
