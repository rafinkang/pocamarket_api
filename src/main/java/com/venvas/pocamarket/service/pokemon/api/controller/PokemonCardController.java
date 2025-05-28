package com.venvas.pocamarket.service.pokemon.api.controller;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDetailDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListFilterSearchCondition;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService2;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 포켓몬 카드 컨트롤러
 * 포켓몬 카드 관련 HTTP 요청을 처리하는 REST 컨트롤러
 */
@RestController
@RequestMapping("/api/pokemon-cards")
@RequiredArgsConstructor
public class PokemonCardController {
    
    private final PokemonCardService pokemonCardService;
    private final PokemonCardUpdateService pokemonCardUpdateService;
    private final PokemonCardUpdateService2 pokemonCardUpdateService2;

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<PokemonCardListDto>>> getPokemonCardListData(
            @ModelAttribute PokemonCardListFilterSearchCondition condition,
            @PageableDefault(size = 30, page = 0)Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getListData(condition, pageable)));
    }

    @GetMapping("/detail/{code}")
    public ResponseEntity<ApiResponse<PokemonCardDetailDto>> getPokemonDataByCode(
            @PathVariable @Pattern(regexp = "^\\w{2,3}-[0-9]{3}$") String code
    ) {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getCardByCode(code)));
    }

    @PostMapping("/update/card/{fileName}")
    public ResponseEntity<ApiResponse<List<PokemonCard>>> updateCard(@PathVariable @NotBlank(message = "파일 이름을 입력해주세요") String fileName) {
        List<PokemonCard> result = pokemonCardUpdateService.updateJsonData(fileName);
        return ResponseEntity.ok(ApiResponse.success(result, "카드가 데이터가 성공적으로 업데이트 되었습니다."));
    }

    @PostMapping("/update/card2/{version}")
    public ResponseEntity<ApiResponse<List<PokemonCard>>> updateCard2(@PathVariable String version) {
        try {
            List<PokemonCard> updatedCards = pokemonCardUpdateService2.updateJsonData(version);
            return ResponseEntity.ok(ApiResponse.success(updatedCards));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Failed to update cards: " + e.getMessage(), "UPDATE_FAILED"));
        }
    }
} 