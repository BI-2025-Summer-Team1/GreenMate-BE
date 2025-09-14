package kr.bi.greenmate.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import kr.bi.greenmate.entity.RecruitmentPostImage;
import kr.bi.greenmate.repository.ObjectStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageDeleteService {

    private final ObjectStorageRepository objectStorageRepository;

    @Async
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void deleteImages(List<RecruitmentPostImage> images) {
        images.forEach(image -> {
            try {
                URI url = new URI(image.getImageUrl());
                String path = url.getPath();
    
                if (path == null || path.length() <= 1) {
                    log.warn("URL에 유효한 경로가 없어 S3 키를 추출할 수 없습니다: {}", image.getImageUrl());
                    return;
                }
                
                String key = path.substring(1);
    
                objectStorageRepository.delete(key);
                log.info("S3 파일 삭제 성공: {}", key);
            } catch (URISyntaxException e) {
                log.error("잘못된 형식의 URL로 S3 파일 삭제에 실패했습니다: {}", image.getImageUrl(), e);
            } catch (Exception e) {
                log.error("S3 파일 삭제 중 예상치 못한 오류 발생: {}", image.getImageUrl(), e);
            }
        });
    }
}
