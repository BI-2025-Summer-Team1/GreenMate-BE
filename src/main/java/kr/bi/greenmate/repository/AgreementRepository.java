package kr.bi.greenmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import kr.bi.greenmate.entity.Agreement;

@Repository
public interface AgreementRepository extends JpaRepository<Agreement, Long> {
}
