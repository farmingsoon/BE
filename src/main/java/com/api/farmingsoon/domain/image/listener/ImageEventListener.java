package com.api.farmingsoon.domain.image.listener;

import com.api.farmingsoon.domain.image.event.UploadImagesRollbackEvent;
import com.api.farmingsoon.common.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ImageEventListener {
    private final S3Service s3Service;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void rollbackUploadImages(UploadImagesRollbackEvent event)
    {
        event.getSavedFileUrls().forEach(s3Service::delete);
    }
}
