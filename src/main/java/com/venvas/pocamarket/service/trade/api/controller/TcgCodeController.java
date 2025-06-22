package com.venvas.pocamarket.service.trade.api.controller;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgCodeSimpleDto;
import com.venvas.pocamarket.service.trade.application.service.TcgCodeService;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeException;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "TcgCode-API", description = "유저 친구 코드 관련 API")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TcgCodeController {

    private final TcgCodeService tcgCodeService;

    @PostMapping("/tcg-code")
    @Operation(summary = "친구 코드 추가", description = "유저 친구 코드 생성")
    public ResponseEntity<ApiResponse<TcgCodeSimpleDto>> createTcgCode(
            @RequestBody @Valid TcgCodeSimpleDto tcgCodeSimpleDto,
            @AuthenticationPrincipal UserDetailDto userDetailDto,
            Errors errors
    ) {
        if(errors.hasErrors()) {
            String errorMessage = errors.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("입력값이 올바르지 않습니다.");
            return ResponseEntity.ok(ApiResponse.error(errorMessage, "400"));
        }

        return executeWithErrorHandling(
            () -> tcgCodeService.createTcgCode(tcgCodeSimpleDto, userDetailDto.getUuid()),
            "신규 친구 코드가 등록 되었습니다.",
            "친구 코드 생성에 실패했습니다."
        );
    }

    @GetMapping("/tcg-code")
    @Operation(summary = "친구 코드 목록 가져오기", description = "유저 친구 코드 목록 조회")
    public ResponseEntity<ApiResponse<List<TcgCodeSimpleDto>>> getTcgCodeList(
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        return executeWithErrorHandling(
            () -> tcgCodeService.getTcgCodeList(userDetailDto.getUuid()),
            null,
            "친구 코드 목록 조회에 실패했습니다."
        );
    }

    @PutMapping("/tcg-code/{codeId}")
    @Operation(summary = "친구 코드 수정", description = "유저 친구 코드 수정")
    public ResponseEntity<ApiResponse<TcgCodeSimpleDto>> updateTcgCode(
            @PathVariable("codeId") Long codeId,
            @RequestBody @Valid TcgCodeSimpleDto tcgCodeSimpleDto,
            @AuthenticationPrincipal UserDetailDto userDetailDto,
            Errors errors
    ) {
        if(errors.hasErrors()) {
            String errorMessage = errors.getFieldErrors()
                    .stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("입력값이 올바르지 않습니다.");
            return ResponseEntity.ok(ApiResponse.error(errorMessage, "400"));
        }

        return executeWithErrorHandling(
            () -> tcgCodeService.updateTcgCode(codeId, tcgCodeSimpleDto, userDetailDto.getUuid()),
            null,
            "친구 코드 수정에 실패했습니다."
        );
    }

    @DeleteMapping("/tcg-code/{codeId}")
    @Operation(summary = "친구 코드 삭제", description = "유저 친구 코드 삭제")
    public ResponseEntity<ApiResponse<Boolean>> deleteTcgCode(
            @PathVariable("codeId") Long codeId,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        return executeWithErrorHandling(
            () -> tcgCodeService.deleteTcgCode(codeId, userDetailDto.getUuid()),
            null,
            "친구 코드 삭제에 실패했습니다."
        );
    }

    /**
     * 공통 예외 처리 메서드
     * @param execution 실행할 비즈니스 로직
     * @param successMessage 성공 시 메시지 (null 가능)
     * @param errorMessage 실패 시 메시지
     * @return ResponseEntity
     */
    private <T> ResponseEntity<ApiResponse<T>> executeWithErrorHandling(
            Supplier<T> execution,
            String successMessage,
            String errorMessage
    ) {
        try {
            T result = execution.get();
            return ResponseEntity.ok(
                successMessage != null 
                    ? ApiResponse.success(result, successMessage)
                    : ApiResponse.success(result)
            );
        } catch (Exception e) {
            if (e instanceof TcgCodeException tcgError) {
                TcgCodeErrorCode errorCode = tcgError.getErrorCode();
                return ResponseEntity.ok(ApiResponse.error(errorCode.getMessage(), errorCode.getCode()));
            }
            return ResponseEntity.ok(ApiResponse.error(errorMessage, "500"));
        }
    }
}
