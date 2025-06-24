package com.venvas.pocamarket.service.trade.api.controller;

import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.trade.application.dto.*;
import com.venvas.pocamarket.service.trade.application.service.TcgTradeRequestService;
import com.venvas.pocamarket.service.trade.application.service.TcgTradeService;
import com.venvas.pocamarket.service.trade.application.service.TcgTradeUserService;
import com.venvas.pocamarket.service.user.application.dto.UserDetailDto;
import com.venvas.pocamarket.service.user.domain.enums.UserGrade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "TcgTrade-API", description = "카드 교환 관련 API")
@RestController
@RequestMapping("/api/tcg-trade")
@RequiredArgsConstructor
public class TcgTradeController {

    private final TcgTradeService tcgTradeService;
    private final TcgTradeRequestService tcgTradeRequestService;
    private final TcgTradeUserService tcgTradeUserService;
    
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
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        Page<TcgTradeListResponse> tradeList = tcgTradeService.getTradeList(request, pageable, uuid, isAdmin, false);
        return ResponseEntity.ok(ApiResponse.success(tradeList));
    }

    @GetMapping("/my")
    @Operation(summary = "내 카드 교환 리스트", description = "내 카드 교환 리스트를 가져옵니다. 인증 필요함")
    public ResponseEntity<ApiResponse<Page<TcgTradeListResponse>>> getMyTcgTradeList(
            @Valid @ModelAttribute TcgTradeListRequest request,
            @PageableDefault(size = 30) Pageable pageable,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        Page<TcgTradeListResponse> tradeList = tcgTradeService.getTradeList(request, pageable, uuid, isAdmin, true);
        return ResponseEntity.ok(ApiResponse.success(tradeList));
    }

    @GetMapping("/my/info")
    @Operation(summary = "내 카드 교환 정보", description = "내 카드 교환 정보를 가져옵니다. 인증 필요함")
    public ResponseEntity<ApiResponse<TcgMyInfoResponse>> getMyTcgTradeInfo(
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        log.info("내 카드 교환 정보: uuid={}", userDetailDto.getUuid());

        TcgMyInfoResponse response = tcgTradeUserService.getMyTcgTradeInfo(userDetailDto.getUuid());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/refresh/{tradeId}")
    @Operation(summary = "내 카드 리스트 끌어올리기", description = "내 교환 카드 리스트 중 하나의 updated_at을 최신으로 갱신합니다.")
    public ResponseEntity<ApiResponse<Boolean>> patchMyListRefresh(
            @PathVariable("tradeId") Long tradeId,
            @Valid @RequestBody TcgTradeRefreshRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        Boolean result = tcgTradeService.tcgTradeRefreshList(tradeId, request, userDetailDto.getUuid());
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @GetMapping("/{tradeId}")
    @Operation(summary = "카드 교환 상세", description = "카드 교환 상세 내용 조회합니다.")
    public ResponseEntity<ApiResponse<TcgTradeDetailResponse>> getTcgTradeDetail(
            @PathVariable("tradeId") Long tradeId,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        TcgTradeDetailResponse response = tcgTradeService.getTcgTradeById(tradeId, userDetailDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{tradeId}")
    @Operation(summary = "카드 교환 요청 수정", description = "카드 교환 요청을 수정합니다.")
    public ResponseEntity<ApiResponse<Boolean>> updateTcgTrade(
            @PathVariable("tradeId") Long tradeId,
            @Valid @RequestBody TcgTradeCreateRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        Boolean result = tcgTradeService.updateTrade(tradeId, request, userDetailDto.getUuid());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/{tradeId}")
    @Operation(summary = "카드 교환 삭제", description = "카드 교환을 삭제합니다.")
    public ResponseEntity<ApiResponse<Boolean>> deleteTcgTrade(
            @PathVariable("tradeId") Long tradeId,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        Boolean result = tcgTradeService.deleteTrade(tradeId, userDetailDto.getUuid());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/{tradeId}/request")
    @Operation(summary = "카드 교환 요청 추가", description = "카드에 대한 교환 요청을 생성합니다.")
    public ResponseEntity<ApiResponse<Boolean>> createTcgTradeRequest(
            @PathVariable("tradeId") Long tradeId,
            @Valid @RequestBody TcgTradeRequestCreateRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        Boolean result = tcgTradeRequestService.createTcgTradeRequest(tradeId, request, uuid);
        return ResponseEntity.ok(ApiResponse.success(result, "카드 교환 요청이 성공적으로 등록되었습니다."));
    }

    @GetMapping("/request/{tradeId}")
    @Operation(summary = "카드 교환 요청 리스트 가져오기", description = "카드 교환 리스트를 가져옵니다.")
    public ResponseEntity<ApiResponse<List<TcgTradeRequestGetResponse>>> getTcgTradeRequestList(
            @PathVariable("tradeId") Long tradeId,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        List<TcgTradeRequestGetResponse> result = tcgTradeRequestService.getTcgTradeRequestList(tradeId, uuid, isAdmin);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PatchMapping("/request/{tradeId}")
    @Operation(summary = "카드 교환 요청 수정", description = "카드 교환 요청의 상태 값을 바꿉니다..")
    public ResponseEntity<ApiResponse<Boolean>> patchTcgTradeRequestList(
            @PathVariable("tradeId") Long tradeId,
            @Valid @RequestBody TcgTradeRequestPatchRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        return ResponseEntity.ok(ApiResponse.success(tcgTradeRequestService.patchTcgTradeRequest(tradeId, request, uuid)));
    }

    @DeleteMapping("/request/{tradeId}")
    @Operation(summary = "카드 교환 요청 제거", description = "카드 교환 요청의 상태 값을 0(삭제)로 바꿔줍니다.")
    public ResponseEntity<ApiResponse<Boolean>> deleteTcgTradeRequest(
            @PathVariable("tradeId") Long tradeId,
            @Valid @RequestBody TcgTradeRequestDeleteRequest request,
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        boolean isAdmin = userDetailDto != null && userDetailDto.getGrade() == UserGrade.ADMIN;
        String uuid = userDetailDto != null ? userDetailDto.getUuid() : null;

        return ResponseEntity.ok(ApiResponse.success(tcgTradeRequestService.deleteTcgTradeRequest(tradeId, request, uuid, isAdmin)));
    }
}
