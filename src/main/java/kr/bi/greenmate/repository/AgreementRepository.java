package kr.bi.greenmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.Agreement;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
	List<Agreement> findByIsRequiredTrue();
}
