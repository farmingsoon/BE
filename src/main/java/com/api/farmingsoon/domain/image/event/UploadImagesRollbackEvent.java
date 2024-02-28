package com.api.farmingsoon.domain.image.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UploadImagesRollbackEvent {

    private final List<String> savedFileUrls;

}