package kr.bi.greenmate.service;

import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.dto.CommunityPostDetailResponse;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.ImageCountExceedException;
import kr.bi.greenmate.exception.error.ImageSizeExceedException;
import kr.bi.greenmate.exception.error.PostNotFoundException;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityPostService {
    private final CommunityPostRepository communityPostRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public CommunityPostCreateResponse createPost(User user, CommunityPostCreateRequest request, List<MultipartFile> images){
        CommunityPost post = CommunityPost.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build();

        final long MAX_IMAGE_SIZE = 1024 * 1024;
        if(images != null && !images.isEmpty()){
            if(images.size() >= 10) throw new ImageCountExceedException();
            for(MultipartFile image : images){
                if(image.getSize() > MAX_IMAGE_SIZE){
                    throw new ImageSizeExceedException();
                }
            }
            List<CommunityPostImage> imageEntities = images.stream()
                    .map(image -> {
                            String imageUrl = imageUploadService.upload(image, "community");
                            return CommunityPostImage.builder()
                                    .communityPost(post)
                                    .imageUrl(imageUrl)
                                    .build();
                    }).toList();

            post.getImages().addAll(imageEntities);
        }

        CommunityPost savedPost = communityPostRepository.save(post);

        return new CommunityPostCreateResponse(savedPost.getId());
    }

    @Transactional(readOnly = true)
    public CommunityPostDetailResponse getPost(Long postId){
        CommunityPost post = communityPostRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        List<String> imageUrls = post.getImages().stream()
                .map(CommunityPostImage::getImageUrl)
                .collect(Collectors.toList());

        return CommunityPostDetailResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrls(imageUrls)
                .authorNickname(post.getUser().getNickname())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
