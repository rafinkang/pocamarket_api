package com.venvas.pocamarket.service.pokemon.api.controller;

import com.venvas.pocamarket.common.aop.trim.TrimInput;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFormDto;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.exception.PokemonErrorCode;
import com.venvas.pocamarket.service.pokemon.api.validator.PokemonStrParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포켓몬 카드 컨트롤러
 * 포켓몬 카드 관련 HTTP 요청을 처리하는 REST 컨트롤러
 */
@Tag(name = "PokemonCard-API", description = "포켓몬 카드 관련 API")
@TrimInput
@RestController
@RequestMapping("/api/pokemon-card")
@RequiredArgsConstructor
public class PokemonCardController {
    
    private final PokemonCardService pokemonCardService;
    private final PokemonCardUpdateService pokemonCardUpdateService;

    @GetMapping("/list")
    @Operation(summary = "포켓몬 리스트", description = "filter 값에 따라 포켓몬 리스트를 조회 API")
    public ResponseEntity<ApiResponse<Page<PokemonCardListDto>>> getPokemonCardListData(
            @ModelAttribute @Valid PokemonCardListFormDto condition,
            @PageableDefault(size = 30)Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getListData(condition, pageable)));
    }

    @GetMapping("/detail/{code}")
    @Operation(summary = "포켓몬 디테일", description = "code로 포켓몬의 자세한 값을 가져오는 API")
    public ResponseEntity<ApiResponse<PokemonCardDetailDto>> getPokemonDataByCode(
            @PathVariable
            @PokemonStrParam(
                    errorCode = PokemonErrorCode.POKEMON_CODE_INVALID,
                    pattern = "^\\w{2,3}-[0-9]{3}$"
            ) String code
    ) {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getCardByCode(code)));
    }

    @PostMapping("/update/card/{fileName}/{packSet}")
    @Operation(summary = "포켓몬 데이터 update", description = "포켓몬 json 데이터 DB에 update")
    public ResponseEntity<ApiResponse<List<PokemonCard>>> updateCard(
            @PathVariable
            @PokemonStrParam(
                    errorCode = PokemonErrorCode.POKEMON_FILE_NAME_INVALID,
                    pattern = "^[\\w\\-]+$"
            ) String fileName,
            @PathVariable
            @PokemonStrParam(
                    errorCode = PokemonErrorCode.POKEMON_PARAM_EMPTY,
                    pattern = "^[a-zA-Z0-9]{1,3}$"
            ) String packSet
    ) {
        List<PokemonCard> result = pokemonCardUpdateService.upsertJsonData(fileName, packSet);
        return ResponseEntity.ok(ApiResponse.success(result, "카드가 데이터가 성공적으로 업데이트 되었습니다."));
    }

//    @PostMapping("/update/card2/{version}")
//    public ResponseEntity<ApiResponse<List<PokemonCard>>> updateCard2(@PathVariable String version) {
//        try {
//            List<PokemonCard> updatedCards = pokemonCardUpdateService2.updateJsonData(version);
//            return ResponseEntity.ok(ApiResponse.success(updatedCards));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error("Failed to update cards: " + e.getMessage(), "UPDATE_FAILED"));
//        }
//    }
} 