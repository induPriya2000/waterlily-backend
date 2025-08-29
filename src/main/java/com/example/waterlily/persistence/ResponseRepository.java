package com.example.waterlily.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResponseRepository extends JpaRepository<ResponseEntity, Long> {
    List<ResponseEntity> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
