package kr.bi.greenmate.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.dto.AgreementResponse;
import kr.bi.greenmate.dto.AgreementsResponse;
import kr.bi.greenmate.entity.Agreement;
import kr.bi.greenmate.repository.AgreementRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgreementService {

    private final AgreementRepository agreementRepository;

    public AgreementsResponse getAllAgreements() {
        List<Agreement> agreements = agreementRepository.findAll();

        Map<Boolean, List<Agreement>> partitionedAgreements = agreements.stream()
            .collect(Collectors.partitioningBy(Agreement::isRequired));
        
        List<AgreementResponse> requiredAgreements = partitionedAgreements.get(true).stream()
            .map(agreement -> AgreementResponse.builder()
                .id(agreement.getId())
                .title(agreement.getTitle())
                .content(agreement.getContent())
                .isRequired(agreement.isRequired())
                .createdAt(agreement.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        List<AgreementResponse> optionalAgreements = partitionedAgreements.get(false).stream()
            .map(agreement -> AgreementResponse.builder()
                .id(agreement.getId())
                .title(agreement.getTitle())
                .content(agreement.getContent())
                .isRequired(agreement.isRequired())
                .createdAt(agreement.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        return AgreementsResponse.builder()
            .requiredAgreements(requiredAgreements)
            .optionalAgreements(optionalAgreements)
            .build();
    }
}
