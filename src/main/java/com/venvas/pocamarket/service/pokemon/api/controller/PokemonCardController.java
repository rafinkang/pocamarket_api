package com.venvas.pocamarket.service.pokemon.api.controller;

import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardListDto;
import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokeCardSearchListFilterCondition;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService2;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
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
    
    /**
     * 모든 포켓몬 카드를 조회하는 엔드포인트
     * @return 전체 포켓몬 카드 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PokemonCard>>> getAllCards() {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getAllCards()));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Page<List<PokemonCardListDto>>>> getListData(
            @ModelAttribute PokeCardSearchListFilterCondition condition,
            @PageableDefault(size = 30, page = 0)Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.success(pokemonCardService.getListData(condition, pageable)));
    }

    @PostMapping("/update/card/{fileName}")
    public ResponseEntity<ApiResponse<List<PokemonCard>>> updateCard(@PathVariable String fileName) {
        ApiResponse<List<PokemonCard>> result = pokemonCardUpdateService.updateJsonData(fileName);
        if(result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(result);
        }
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