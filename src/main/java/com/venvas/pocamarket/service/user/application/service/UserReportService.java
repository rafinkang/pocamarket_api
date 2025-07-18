package com.venvas.pocamarket.service.user.application.service;

import com.venvas.pocamarket.common.dto.PageResponse;
import com.venvas.pocamarket.service.user.application.dto.UserReportRequest;
import com.venvas.pocamarket.service.user.application.dto.UserReportResponse;
import com.venvas.pocamarket.service.user.domain.entity.UserReport;
import com.venvas.pocamarket.service.user.domain.repository.UserReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserReportService {
    private final UserReportRepository UserReportRepository;

    public UserReportResponse saveReport(UserReportRequest reportRequest) throws Exception {
        UserReport userReport = UserReport.createFromRequest(reportRequest);
        UserReportResponse response = UserReportResponse.from(UserReportRepository.save(userReport));
        return response;
    }

    public PageResponse<UserReportResponse> getReportsByUuid(Pageable pageable, String uuid) throws Exception {
        Page<UserReport> userReportPage = UserReportRepository.findByUuid(uuid, pageable);
        return PageResponse.of(userReportPage.map(UserReportResponse::from));
    }
}
