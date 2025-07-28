package kr.bi.greenmate.service;

import kr.bi.greenmate.exception.error.FileEmptyException;
import kr.bi.greenmate.exception.error.InvalidImageTypeException;
import kr.bi.greenmate.exception.error.MissingImageTypeException;
import kr.bi.greenmate.repository.ObjectStorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadService {
    private final ObjectStorageRepository objectStorageRepository;
    private static final Set<String> EXTENSION_SET = Set.of(".jpg", ".jpeg", ".png");

    public String upload(MultipartFile file, String type) {
        if (file == null || file.isEmpty()) {
            throw new FileEmptyException();
        }
        if (type == null || type.isBlank()) {
            throw new MissingImageTypeException();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";

        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        if (!EXTENSION_SET.contains(extension.toLowerCase())){
            throw new InvalidImageTypeException();
        }

        String savedFileName = UUID.randomUUID() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            objectStorageRepository.upload(type, savedFileName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

        return objectStorageRepository.getDownloadUrl(type + "/" + savedFileName);
    }
}
