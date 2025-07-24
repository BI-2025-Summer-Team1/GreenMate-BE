package kr.bi.greenmate.service;

import kr.bi.greenmate.exception.FileEmptyException;
import kr.bi.greenmate.exception.InvalidImageTypeException;
import kr.bi.greenmate.exception.MissingImageTypeException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ImageUploadService {
    private String baseDirectory = "base/"; // 임의의 이미지 업로드 상위 폴더

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
        if (!extension.matches("\\.(jpg|jpeg|png)")){
            throw new InvalidImageTypeException();
        }
        String savedFileName = UUID.randomUUID() + extension;

        String uploadDir = baseDirectory + type + "/";

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File dest = new File(directory, savedFileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드가 실패했습니다.", e);
        }

        return uploadDir + savedFileName;
    }
}
