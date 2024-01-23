package com.api.farmingsoon.domain.image.repository;

import com.api.farmingsoon.domain.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
