package com.venvas.pocamarket.service.user.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.user.application.dto.UserCreateRequest;
import com.venvas.pocamarket.service.user.application.service.UserService;
import com.venvas.pocamarket.service.user.domain.entity.User;
import com.venvas.pocamarket.service.user.domain.exception.UserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관련 HTTP 요청을 처리하는 REST 컨트롤러
 * 사용자 생성, 조회, 수정 등의 엔드포인트를 제공
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 새로운 사용자 생성
     * 
     * @param request 사용자 생성 요청 데이터
     * @return 생성된 사용자 정보와 성공 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("사용자 생성 요청: loginId={}", request.getLoginId());
        
        try {
            User createdUser = userService.createUser(request);
            return ResponseEntity.ok(ApiResponse.success(createdUser, "사용자가 성공적으로 생성되었습니다."));
        } catch (ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "ERR_02"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), "ERR_03"));
        } catch (UserException e) {
            log.error("사용자 생성 실패: loginId={}, error={}", request.getLoginId(), e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage(), e.getErrorCode()));
        }
    }
}
