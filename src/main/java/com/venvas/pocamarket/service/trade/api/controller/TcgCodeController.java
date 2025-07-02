package com.venvas.pocamarket.service.trade.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgCodeSimpleDto;
import com.venvas.pocamarket.service.trade.application.service.TcgCodeService;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "TcgCode-API", description = "유저 친구 코드 관련 API")
@RestController
@RequestMapping("/tcg-code")
@RequiredArgsConstructor
public class TcgCodeController {

    private final TcgCodeService tcgCodeService;

    @PostMapping("")
    @Operation(summary = "친구 코드 추가", description = "유저 친구 코드 생성")
    public ResponseEntity<ApiResponse<TcgCodeSimpleDto>> createTcgCode(
            @RequestBody @Valid TcgCodeSimpleDto tcgCodeSimpleDto,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.createTcgCode(tcgCodeSimpleDto, uuid)));
    }

    @GetMapping("")
    @Operation(summary = "친구 코드 목록 가져오기", description = "유저 친구 코드 목록 조회")
    public ResponseEntity<ApiResponse<List<TcgCodeSimpleDto>>> getTcgCodeList(
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;
        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.getTcgCodeList(uuid)));
    }

    @PutMapping("/{codeId}")
    @Operation(summary = "친구 코드 수정", description = "유저 친구 코드 수정")
    public ResponseEntity<ApiResponse<TcgCodeSimpleDto>> updateTcgCode(
            @PathVariable("codeId") Long codeId,
            @RequestBody @Valid TcgCodeSimpleDto tcgCodeSimpleDto,
            @AuthenticationPrincipal UserDetailDto userDetailDto,
            Errors errors
    ) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;
        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.updateTcgCode(codeId, tcgCodeSimpleDto, uuid)));
    }

    @DeleteMapping("/{codeId}")
    @Operation(summary = "친구 코드 삭제", description = "유저 친구 코드 삭제")
    public ResponseEntity<ApiResponse<Boolean>> deleteTcgCode(
            @PathVariable("codeId") Long codeId,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.deleteTcgCode(codeId, userDetailDto.getUuid())));
    }

    @GetMapping("/{tradeId}/{requestId}")
    @Operation(summary = "교환에 필요한 친구 코드", description = "교환에 필요한 친구 코드 반환")
    public ResponseEntity<ApiResponse<String>> getTcgTradeRequest(
            @PathVariable("tradeId") Long tradeId,
            @PathVariable("requestId") Long requestId,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.getTcgTradeCode(tradeId, requestId, uuid)));
    }
}
