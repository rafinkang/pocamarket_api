package com.venvas.pocamarket.service.user.application.dto;
import java.time.LocalDateTime;
import com.venvas.pocamarket.service.user.domain.entity.UserReport;
import lombok.Getter;

@Getter
public class UserReportResponse {
    private Long id;
    private Long refId;
    private String refType;
    private Integer refStatus;
    private String link;
    private String uuid;
    private String content;
    private LocalDateTime createdAt;
    private Integer status;
    private String reportResult;
    private String adminUuid;
    private LocalDateTime resultAt;

    private UserReportResponse(
        Long id,
        Long refId,
        String refType,
        String link,
        String uuid,
        String content,
        Integer refStatus,
        LocalDateTime createdAt,
        Integer status,
        String reportResult,
        String adminUuid,
        LocalDateTime resultAt
    ) {
        this.id = id;
        this.refId = refId;
        this.refType = refType;
        this.link = link;
        this.uuid = uuid;
        this.content = content;
        this.refStatus = refStatus;
        this.createdAt = createdAt;
        this.status = status;
        this.reportResult = reportResult;
        this.adminUuid = adminUuid;
        this.resultAt = resultAt;
    }

    public static UserReportResponse from(UserReport userReport) {
        return new UserReportResponse(
            userReport.getId(),
            userReport.getRefId(),
            userReport.getRefType(),
            userReport.getLink(),
            userReport.getUuid(),
            userReport.getContent(),
            userReport.getRefStatus(),
            userReport.getCreatedAt(),
            userReport.getStatus(),
            userReport.getReportResult(),
            userReport.getAdminUuid(),
            userReport.getResultAt()
        );
    }
}
