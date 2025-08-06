package kr.bi.greenmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public abstract class BaseTimeEntity extends BaseCreatedEntity{

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
