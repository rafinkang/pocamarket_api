package com.venvas.pocamarket.service.pokemon.domain.repository;

import com.venvas.pocamarket.service.pokemon.application.dto.CardCodeName;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 포켓몬 카드 레포지토리
 * 포켓몬 카드 엔티티의 데이터베이스 접근을 담당하는 인터페이스
 */
@Repository
public interface PokemonCardRepository extends JpaRepository<PokemonCard, Long>, PokemonCardRepositoryCustom, QuerydslPredicateExecutor<PokemonCard> {
    /**
     * 카드 코드로 포켓몬 카드를 조회
     * @param code 카드 코드
     * @return 조회된 포켓몬 카드 (Optional)
     */
    Optional<PokemonCard> findByCode(String code);
    
    /**
     * 한글 이름으로 포켓몬 카드를 검색
     * @param name 검색할 한글 이름 (부분 일치)
     * @return 검색된 포켓몬 카드 목록
     */
    List<PokemonCard> findByNameKoContaining(String name);
    
    /**
     * 속성으로 포켓몬 카드를 조회
     * @param element 포켓몬 속성
     * @return 조회된 포켓몬 카드 목록
     */
    List<PokemonCard> findByElement(String element);
    
    /**
     * 확장팩으로 포켓몬 카드를 조회
     * @param packSet 확장팩 이름
     * @return 조회된 포켓몬 카드 목록
     */
    List<PokemonCard> findByPackSet(String packSet);
    
    /**
     * 레어도로 포켓몬 카드를 조회
     * @param rarity 카드 레어도
     * @return 조회된 포켓몬 카드 목록
     */
    List<PokemonCard> findByRarity(String rarity);

    /**
     * 카드 코드 리스트로 카드 코드와 한글 이름을 조회
     * @param cardCodes 카드 코드 리스트
     * @return 카드 코드와 한글 이름이 포함된 Projection 리스트
     */
    @Query("SELECT p.code as code, p.nameKo as nameKo FROM PokemonCard p WHERE p.code IN :cardCodes")
    List<CardCodeName> findByCodeInGetCodeAndNameKo(@Param("cardCodes") List<String> cardCodes);
}