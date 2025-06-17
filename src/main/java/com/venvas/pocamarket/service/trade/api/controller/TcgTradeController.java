package com.venvas.pocamarket.service.trade.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeCreateRequest;
import com.venvas.pocamarket.service.trade.application.service.TcgTradeService;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@Tag(name = "TcgTrade-API", description = "카드 교환 관련 API")
@RestController
@RequestMapping("/api/tcg-trade")
@RequiredArgsConstructor
public class TcgTradeController {
    
    private final TcgTradeService tcgTradeService;
    
    @PostMapping("")
    @Operation(summary = "카드 교환 요청 생성", description = "새로운 카드 교환 요청을 생성합니다.")
    public ResponseEntity<ApiResponse<Boolean>> createTcgTrade(
            @Valid @RequestBody TcgTradeCreateRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        
        log.info("카드 교환 요청: myCard={}, wantCards={}, tcgCode={}", 
                request.getMyCardCode(), request.getWantCardCode(), request.getTcgCode());
        
        Boolean result = tcgTradeService.createTrade(request, userDetailDto.getUuid());
        return ResponseEntity.ok(ApiResponse.success(result, "카드 교환 요청이 성공적으로 등록되었습니다."));
    }
    
}
