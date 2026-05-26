package com.queuewise.repository;

import com.queuewise.entity.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QueueRepository extends JpaRepository<Queue, Long> {

    // Active queues anni fetch cheyyi
    List<Queue> findByIsActiveTrue();

    // Admin created queues fetch cheyyi
    List<Queue> findByCreatedById(Long userId);
}
