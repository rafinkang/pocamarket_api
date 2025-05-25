package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.common.util.ApiResponse;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonability.AbilityDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemonattack.AttacksDto;
import com.venvas.pocamarket.service.pokemon.application.dto.pokemoncard.PokemonCardDto;
import com.venvas.pocamarket.service.pokemon.domain.entity.*;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 포켓몬 카드 데이터 업데이트를 담당하는 서비스 클래스
 * JSON 파일에서 카드 데이터를 읽어와 데이터베이스에 저장하는 기능을 제공
 */
@Service  // Spring 서비스 빈으로 등록
@Transactional  // 모든 메서드에 트랜잭션 적용
@RequiredArgsConstructor  // final 필드에 대한 생성자 자동 생성
@Slf4j  // 로깅을 위한 Lombok 어노테이션
public class PokemonCardUpdateService {

    // JPA Repository 주입 (생성자 주입)
    private final PokemonCardRepository pokemonCardRepository;

    /**
     * JSON 파일에서 포켓몬 카드 데이터를 읽어와 데이터베이스에 저장하는 메서드
     * @param version JSON 파일의 버전 (파일명에 사용됨)
     * @return ApiResponse<List<PokemonCard>> 저장된 카드 목록과 처리 결과
     */
    public ApiResponse<List<PokemonCard>> updateJsonData(String version) {
        // JSON 데이터를 Java 객체로 변환하기 위한 ObjectMapper 인스턴스 생성
        ObjectMapper mapper = new ObjectMapper();
        // resources/sample 디렉토리에서 version.json 파일을 읽어옴
        InputStream inputStream = getClass().getResourceAsStream("/sample/" + version + ".json");
        // JSON 데이터를 List<PokemonCardDto> 타입으로 변환하기 위한 타입 참조 생성
        TypeReference<List<PokemonCardDto>> typeReference = new TypeReference<>() {};

        try {
            // JSON 파일을 읽어서 PokemonCardDto 리스트로 변환
            List<PokemonCardDto> pokemonCards = mapper.readValue(inputStream, typeReference);
            // 변환된 카드 데이터 로깅
            log.info("pokemon Card : {}", pokemonCards);

            // 엔티티로 변환할 카드 리스트 생성
            List<PokemonCard> cardList = new ArrayList<>();

            // 각 DTO를 엔티티로 변환하여 리스트에 추가
            for (PokemonCardDto card : pokemonCards) {
                cardList.add(mappingCardData(card));
            }

            // 변환된 카드 엔티티들을 데이터베이스에 저장하고 성공 응답 반환
            return ApiResponse.success(pokemonCardRepository.saveAll(cardList),
                    "카드 데이터가 성공적으로 업데이트 되었습니다.");

        } catch (IOException e) {
            // 파일 읽기 실패 시 에러 응답 반환 (에러 코드: 100)
            return ApiResponse.error(e.getMessage(), "100");
        } catch (Exception e) {
            // 기타 예외 발생 시 에러 응답 반환 (에러 코드: 101)
            return ApiResponse.error(e.getMessage(), "101");
        }
    }

    /**
     * AbilityDto 리스트를 PokemonAbility 엔티티 리스트로 변환하는 메서드
     * @param aDto 변환할 AbilityDto 리스트
     * @param cardCode 카드 코드 (외래 키로 사용)
     * @return PokemonAbility 엔티티 리스트 (null 가능)
     */
    private List<PokemonAbility> mappingAbilityData(List<AbilityDto> aDto, String cardCode) {
        // 입력이 null인 경우 null 반환
        if (aDto == null) return null;

        // 변환된 엔티티를 저장할 리스트 생성
        List<PokemonAbility> list = new ArrayList<>();
        // 각 DTO를 엔티티로 변환하여 리스트에 추가
        for (AbilityDto a : aDto) {
            list.add(new PokemonAbility(a, cardCode));
        }
        return list;
    }

    /**
     * AttacksDto 리스트를 PokemonAttack 엔티티 리스트로 변환하는 메서드
     * @param aDto 변환할 AttacksDto 리스트
     * @param cardCode 카드 코드 (외래 키로 사용)
     * @return PokemonAttack 엔티티 리스트 (null 가능)
     */
    private List<PokemonAttack> mappingAttackData(List<AttacksDto> aDto, String cardCode) {
        // 입력이 null인 경우 null 반환
        if (aDto == null) return null;

        // 변환된 엔티티를 저장할 리스트 생성
        List<PokemonAttack> list = new ArrayList<>();
        // 각 DTO를 엔티티로 변환하여 리스트에 추가
        for (AttacksDto a : aDto) {
            list.add(new PokemonAttack(a, cardCode));
        }
        return list;
    }

    /**
     * PokemonCardDto를 PokemonCard 엔티티로 변환하는 메서드
     * @param c 변환할 PokemonCardDto
     * @return 변환된 PokemonCard 엔티티
     */
    private PokemonCard mappingCardData(PokemonCardDto c) {
        // 공격 데이터와 특성 데이터를 각각 엔티티로 변환
        List<PokemonAttack> attackList = mappingAttackData(c.attacks(), c.code());
        List<PokemonAbility> abilityList = mappingAbilityData(c.abilities(), c.code());

        // 변환된 데이터로 PokemonCard 엔티티 생성하여 반환
        return new PokemonCard(c, attackList, abilityList);
    }
}
