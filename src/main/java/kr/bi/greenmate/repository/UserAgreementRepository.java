package kr.bi.greenmate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.bi.greenmate.entity.UserAgreement;
import kr.bi.greenmate.entity.UserAgreementId;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, UserAgreementId> {
	List<UserAgreement> findByUserAgreementIdUserIdAndUserAgreementIdAgreementIdIn(
		Long userId, Collection<Long> agreementIds);
}
