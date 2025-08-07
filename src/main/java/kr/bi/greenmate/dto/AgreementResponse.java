package kr.bi.greenmate.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AgreementResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isRequired;
    private LocalDateTime createdAt;
    
}
