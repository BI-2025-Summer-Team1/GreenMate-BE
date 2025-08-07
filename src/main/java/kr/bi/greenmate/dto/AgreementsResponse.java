package kr.bi.greenmate.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgreementsResponse {

    private List<AgreementResponse> requiredAgreements;
    private List<AgreementResponse> optionalAgreements;
}
