package com.venvas.pocamarket.service.trade.api.controller;

import com.venvas.pocamarket.common.aop.trim.TrimInput;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        log.info("tcgCodeCreateDto : {}", tcgCodeSimpleDto.toString());
        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.createTcgCode(tcgCodeSimpleDto, userDetailDto.getUuid()),
                "신규 친구 코드가 등록 되었습니다."));
    }

    @GetMapping("/tcg-code")
    @Operation(summary = "친구 코드 목록 가져오기", description = "유저 친구 코드 목록 조회")
    public ResponseEntity<ApiResponse<List<TcgCodeSimpleDto>>> getTcgCodeList(
            @AuthenticationPrincipal UserDetailDto userDetailDto
    ) {
        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.getTcgCodeList(userDetailDto.getUuid())));
    }

//    @PutMapping("/tcg-code/{codeId}")
//    @Operation(summary = "친구 코드 수정", description = "유저 친구 코드 수정")
//    public ResponseEntity<ApiResponse<PokemonCardListDto>> getPokemonCardListData(
//            @ModelAttribute @Valid PokemonCardListFormDto condition,
//            @PageableDefault(size = 30) Pageable pageable
//    ) {
//        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.getListData(condition, pageable)));
//    }
//
//    @DeleteMapping("/tcg-code/{codeId}")
//    @Operation(summary = "친구 코드 삭제", description = "유저 친구 코드 삭제")
//    public ResponseEntity<ApiResponse<PokemonCardListDto>> getPokemonCardListData(
//            @ModelAttribute @Valid PokemonCardListFormDto condition,
//            @PageableDefault(size = 30) Pageable pageable
//    ) {
//        return ResponseEntity.ok(ApiResponse.success(tcgCodeService.getListData(condition, pageable)));
//    }
}
