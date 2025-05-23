package com.venvas.pocamarket.service.pokemon.api.controller;

import com.venvas.pocamarket.service.pokemon.application.dto.PokemonCardListDto;
import com.venvas.pocamarket.service.pokemon.application.dto.PokeCardSearchListFilterCondition;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardService;
import com.venvas.pocamarket.service.pokemon.application.service.PokemonCardUpdateService;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCardDto;
import lombok.RequiredArgsConstructor;
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
    
    /**
     * 모든 포켓몬 카드를 조회하는 엔드포인트
     * @return 전체 포켓몬 카드 목록
     */
    @GetMapping
    public ResponseEntity<List<PokemonCard>> getAllCards() {
        return ResponseEntity.ok(pokemonCardService.getAllCards());
    }
    
    /**
     * 카드 코드로 특정 포켓몬 카드를 조회하는 엔드포인트
     * @param code 카드 코드
     * @return 조회된 포켓몬 카드 (존재하지 않는 경우 404 응답)
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<PokemonCard> getCardByCode(@PathVariable String code) {
        return pokemonCardService.getCardByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 한글 이름으로 포켓몬 카드를 검색하는 엔드포인트
     * @param name 검색할 한글 이름
     * @return 검색된 포켓몬 카드 목록
     */
    @GetMapping("/search")
    public ResponseEntity<List<PokemonCard>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(pokemonCardService.searchByName(name));
    }
    
    /**
     * 특정 속성의 포켓몬 카드를 조회하는 엔드포인트
     * @param element 포켓몬 속성
     * @return 조회된 포켓몬 카드 목록
     */
    @GetMapping("/element/{element}")
    public ResponseEntity<List<PokemonCard>> getCardsByElement(@PathVariable String element) {
        return ResponseEntity.ok(pokemonCardService.getCardsByElement(element));
    }
    
    /**
     * 특정 확장팩의 포켓몬 카드를 조회하는 엔드포인트
     * @param packSet 확장팩 이름
     * @return 조회된 포켓몬 카드 목록
     */
    @GetMapping("/pack-set/{packSet}")
    public ResponseEntity<List<PokemonCard>> getCardsByPackSet(@PathVariable String packSet) {
        return ResponseEntity.ok(pokemonCardService.getCardsByPackSet(packSet));
    }
    
    /**
     * 특정 레어도의 포켓몬 카드를 조회하는 엔드포인트
     * @param rarity 카드 레어도
     * @return 조회된 포켓몬 카드 목록
     */
    @GetMapping("/rarity/{rarity}")
    public ResponseEntity<List<PokemonCard>> getCardsByRarity(@PathVariable String rarity) {
        return ResponseEntity.ok(pokemonCardService.getCardsByRarity(rarity));
    }

    @GetMapping("/list")
    public ResponseEntity<List<PokemonCardListDto>> getListData(@ModelAttribute PokeCardSearchListFilterCondition condition) {
        return ResponseEntity.ok(pokemonCardService.getListData(condition));
    }

    @PostMapping("/update/card/{version}")
    public ResponseEntity<List<PokemonCard>> updateCard(@PathVariable String version) {
        return ResponseEntity.ok(pokemonCardUpdateService.updateJsonData(version));
    }
} 