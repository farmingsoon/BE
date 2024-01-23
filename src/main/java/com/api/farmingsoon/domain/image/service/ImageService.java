package com.api.farmingsoon.domain.image.service;

import com.api.farmingsoon.common.event.UploadImagesRollbackEvent;
import com.api.farmingsoon.common.s3.service.S3Service;
import com.api.farmingsoon.common.util.JwtUtils;
import com.api.farmingsoon.domain.image.domain.Image;
import com.api.farmingsoon.domain.image.repository.ImageRepository;
import com.api.farmingsoon.domain.item.domain.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {


    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    public void createImage(Image image){
        imageRepository.save(image);
    }

    public List<String> uploadItemImages(MultipartFile thumbnailImage, List<MultipartFile> images) {
        List<String> imageUrls = new ArrayList<>();

        String thumbnailImageUrl = s3Service.upload(thumbnailImage, "Item");

        imageUrls.add(thumbnailImageUrl);
        images.stream().map(image -> s3Service.upload(image, "Item"))
                .forEach(imageUrls::add);

        return imageUrls;
    }
}
