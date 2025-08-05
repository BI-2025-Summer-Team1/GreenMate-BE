package kr.bi.greenmate.service;

import jakarta.transaction.Transactional;
import kr.bi.greenmate.dto.CommunityPostCreateRequest;
import kr.bi.greenmate.dto.CommunityPostCreateResponse;
import kr.bi.greenmate.entity.CommunityPost;
import kr.bi.greenmate.entity.CommunityPostImage;
import kr.bi.greenmate.entity.User;
import kr.bi.greenmate.exception.error.ImageCountExceedException;
import kr.bi.greenmate.repository.CommunityPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

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

        if(images != null && !images.isEmpty()){
            if(images.size() >= 10) throw new ImageCountExceedException();
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

        return CommunityPostCreateResponse.builder()
                .postId(savedPost.getId())
                .build();
    }
}
