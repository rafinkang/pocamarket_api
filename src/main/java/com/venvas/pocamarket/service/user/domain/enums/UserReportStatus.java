package com.venvas.pocamarket.service.user.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserReportStatus {
    RECEIVED(1, "신고 접수"),
    COMPLETE(0, "처리 완료"),
    PENDING(2, "처리 보류");

    private final int code;
    private final String description;
    
    public static UserReportStatus fromCode(int code) {
        for (UserReportStatus status : UserReportStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }
    
    public static Integer toCode(UserReportStatus status) {
        if (status == null) {
            return RECEIVED.getCode();
        }
        return status.getCode();
    }

    public static UserReportStatus fromDbCode(Integer code) {
        if (code == null) {
            return RECEIVED;
        }
        
        UserReportStatus status = fromCode(code);
        return status != null ? status : RECEIVED;
    }
}
