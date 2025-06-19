package com.venvas.pocamarket.service.trade.api.controller;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListResponse;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

import java.util.List;


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

    @GetMapping("")
    @Operation(summary = "카드 교환 리스트", description = "카드 교환 리스트를 가져옵니다.")
    public ResponseEntity<ApiResponse<Page<TcgTradeListResponse>>> getTcgTradeList(
            @Validated @ModelAttribute TcgTradeListRequest request,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;

        Page<TcgTradeListResponse> tradeList = tcgTradeService.getTradeList(request, pageable, null, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(tradeList));
    }

    @GetMapping("/my")
    @Operation(summary = "내 카드 교환 리스트", description = "내 카드 교환 리스트를 가져옵니다. 인증 필요함")
    public ResponseEntity<ApiResponse<Page<TcgTradeListResponse>>> getMyTcgTradeList(
            @Valid @ModelAttribute TcgTradeListRequest request,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        Page<TcgTradeListResponse> tradeList = tcgTradeService.getTradeList(request, pageable, uuid, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(tradeList));
    }
}
