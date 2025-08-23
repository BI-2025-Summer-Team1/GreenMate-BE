package kr.bi.greenmate.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import kr.bi.greenmate.entity.UserAgreement;
import kr.bi.greenmate.entity.UserAgreementId;

public interface UserAgreementRepository extends JpaRepository<UserAgreement, UserAgreementId> {
	@Modifying
	@Query(value = """
		MERGE INTO user_agreement ua
		USING (SELECT :userId AS user_id,
		              :agreementId AS agreement_id,
		              :acceptedAt AS accepted_at
		       FROM dual) src
		ON (ua.user_id = src.user_id AND ua.agreement_id = src.agreement_id)
		WHEN MATCHED THEN
		  UPDATE SET ua.is_accepted = 1,
		             ua.accepted_at = CASE
		                                WHEN ua.accepted_at IS NULL OR ua.accepted_at < src.accepted_at
		                                  THEN src.accepted_at
		                                ELSE ua.accepted_at
		                              END
		  WHERE ua.is_accepted <> 1
		     OR ua.accepted_at IS NULL
		     OR ua.accepted_at < src.accepted_at
		WHEN NOT MATCHED THEN
		  INSERT (user_id, agreement_id, is_accepted, accepted_at)
		  VALUES (src.user_id, src.agreement_id, 1, src.accepted_at)
		""", nativeQuery = true)
	void upsert(@Param("userId") Long userId,
		@Param("agreementId") Long agreementId,
		@Param("acceptedAt") LocalDateTime acceptedAt);
}
