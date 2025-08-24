package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.UserAgreement;
import kr.bi.greenmate.entity.UserAgreementId;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, UserAgreementId> {
	List<UserAgreement> findByUserAgreementIdUserId(Long id);
}
