package com.teamnative.relayplus.domain.notice.repository;

import com.teamnative.relayplus.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    
    Page<Notice> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
