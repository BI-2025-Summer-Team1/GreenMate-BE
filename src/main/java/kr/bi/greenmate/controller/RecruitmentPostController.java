package kr.bi.greenmate.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import kr.bi.greenmate.dto.RecruitmentPostCreationRequest;
import kr.bi.greenmate.service.ImageUploadService;
import kr.bi.greenmate.service.RecruitmentPostService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recruitment-posts")
@RequiredArgsConstructor
public class RecruitmentPostController {

    private final RecruitmentPostService recruitmentPostService;
    private final ImageUploadService imageUploadService;

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Long> createPost(
        @RequestPart @Valid RecruitmentPostCreationRequest request,
        @RequestPart(required = false) List<MultipartFile> images,
        @AuthenticationPrincipal Long userId) {

        List<String> imageUrls = null;
        if (images != null && !images.isEmpty()) {
            imageUrls = images.stream()
                .map(file -> imageUploadService.upload(file, "recruitment-post"))
                .collect(Collectors.toList());
        }

        Long postId = recruitmentPostService.createRecruitmentPost(request, imageUrls, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }
}
