package kr.bi.greenmate.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserAgreementId implements Serializable {

    private Long userId;
    private Long agreementId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserAgreementId that = (UserAgreementId) o;
        return Objects.equals(getUserId(), that.getUserId()) &&
               Objects.equals(getAgreementId(), that.getAgreementId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getAgreementId());
    }
}
