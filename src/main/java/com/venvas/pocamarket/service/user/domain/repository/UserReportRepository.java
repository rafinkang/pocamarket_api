package com.venvas.pocamarket.service.user.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.venvas.pocamarket.service.user.domain.entity.UserReport;

@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {
    Page<UserReport> findByUuid(String uuid, Pageable pageable);
}