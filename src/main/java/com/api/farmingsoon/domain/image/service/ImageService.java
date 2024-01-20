package com.api.farmingsoon.domain.image.service;

import com.api.farmingsoon.common.s3.service.S3Service;
import com.api.farmingsoon.domain.image.domain.Image;
import com.api.farmingsoon.domain.image.repository.ImageRepository;
import com.api.farmingsoon.domain.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Service s3Service;
    private final ImageRepository imageRepository;
    public String uploadAndCreateItemImages(Item item, MultipartFile thumbnailImage, List<MultipartFile> images, String kind) {
        // 컬럼값으로 가져간다면 썸네일url만 넘겨준다
        // 아니면 void
        String thumbnailImageUrl = s3Service.upload(thumbnailImage, kind);
        images.stream().map(image -> s3Service.upload(image, kind))
                .forEach(imageUrl
                        -> imageRepository.save(Image.of(imageUrl,item)
                )
        );

        return thumbnailImageUrl;
    }
}
