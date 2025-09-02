package kr.bi.greenmate.service;

import java.net.URI;

import org.springframework.stereotype.Service;

import kr.bi.greenmate.repository.ObjectStorageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageDeleteService {

    private final ObjectStorageRepository objectStorageRepository;

    public void deleteImage(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return; 
        }

        try {
            URI url = new URI(fileUrl);
            String path = url.getPath();
            String key = path.startsWith("/") ? path.substring(1) : path;
            
            objectStorageRepository.delete(key);
            log.info("S3 파일 삭제 성공: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 중 오류 발생: {}", fileUrl, e);
        }
    }
}
