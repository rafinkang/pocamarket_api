package com.venvas.pocamarket.service.pokemon.application.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonCard;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAbility;
import com.venvas.pocamarket.service.pokemon.domain.entity.PokemonAttack;
import com.venvas.pocamarket.service.pokemon.domain.repository.PokemonCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PokemonCardUpdateService2 {
    private final PokemonCardRepository pokemonCardRepository;
    private final ObjectMapper objectMapper;

    /**
     * JSON 파일에서 포켓몬 카드 데이터를 읽어와 데이터베이스에 저장
     * @param version JSON 파일 버전
     * @return 저장된 포켓몬 카드 목록
     */
    public List<PokemonCard> updateJsonData(String version) {
        try {
            // 1. JSON 파일을 Map 리스트로 읽기
            List<Map<String, Object>> rawCards = readJsonFile(version);
            log.info("1. JSON 파일에서 읽어온 카드 수: {}", rawCards.size());
            if (!rawCards.isEmpty()) {
                log.debug("첫 번째 카드 데이터 예시: {}", rawCards.get(0));
            }
            
            // 2. 데이터 변환 및 저장
            List<PokemonCard> processedCards = rawCards.stream()
                .map(this::processCardData)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            
            log.info("2. 처리된 카드 수: {}", processedCards.size());
            if (!processedCards.isEmpty()) {
                PokemonCard firstCard = processedCards.get(0);
                log.debug("첫 번째 처리된 카드 상세: code={}, name={}, attacks={}, abilities={}", 
                    firstCard.getCode(),
                    firstCard.getName(),
                    firstCard.getAttacks() != null ? firstCard.getAttacks().size() : 0,
                    firstCard.getAbilities() != null ? firstCard.getAbilities().size() : 0);
            }
            return processedCards;
                
        } catch (Exception e) {
            log.error("Failed to update card data: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to update card data", e);
        }
    }

    /**
     * JSON 파일을 읽어 Map 리스트로 변환
     * @param version JSON 파일 버전
     * @return Map 리스트 형태의 카드 데이터
     * @throws IOException 파일 읽기 실패 시
     */
    private List<Map<String, Object>> readJsonFile(String version) throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/sample/" + version + ".json")) {
            if (inputStream == null) {
                throw new FileNotFoundException("JSON file not found: " + version);
            }
            return objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {});
        }
    }

    /**
     * Map 형태의 카드 데이터를 PokemonCard 엔티티로 변환
     * @param rawCard Map 형태의 카드 데이터
     * @return 변환된 PokemonCard 엔티티
     */
    private PokemonCard processCardData(Map<String, Object> rawCard) {
        try {
            String code = (String) rawCard.get("code");
            log.debug("3. 카드 처리 시작: code={}", code);
            
            if (!StringUtils.hasText(code)) {
                log.warn("코드가 비어있는 카드 건너뜀");
                return null;
            }

            Optional<PokemonCard> existingCard = pokemonCardRepository.findByCode(code);
            PokemonCard card = existingCard.orElseGet(PokemonCard::new);
            log.debug("4. 카드 상태: code={}, isNew={}", code, !existingCard.isPresent());

            updateBasicInfo(card, rawCard);
            log.debug("5. 기본 정보 업데이트 완료: code={}, name={}", code, card.getName());
            
            updateAttacks(card, rawCard);
            log.debug("6. 공격 정보 업데이트 완료: code={}, attacks={}", code, 
                card.getAttacks() != null ? card.getAttacks().size() : 0);
            
            updateAbilities(card, rawCard);
            log.debug("7. 특성 정보 업데이트 완료: code={}, abilities={}", code, 
                card.getAbilities() != null ? card.getAbilities().size() : 0);

            return card;
        } catch (Exception e) {
            log.error("Error processing card data for code {}: {}", rawCard.get("code"), e.getMessage());
            return null;
        }
    }

    /**
     * 카드의 기본 정보 업데이트
     * @param card 업데이트할 PokemonCard 엔티티
     * @param rawCard Map 형태의 카드 데이터
     */
    private void updateBasicInfo(PokemonCard card, Map<String, Object> rawCard) {
//        card.setCode((String) rawCard.get("code"));
//        card.setName((String) rawCard.get("name"));
//        card.setNameKo((String) rawCard.get("name_ko"));
//        card.setElement((String) rawCard.get("element"));
//        card.setType((String) rawCard.get("type"));
//        card.setSubtype((String) rawCard.get("subtype"));
//        card.setHealth(parseInteger(rawCard.get("health")));
//        card.setPackSet((String) rawCard.get("set"));
//        card.setPack((String) rawCard.get("pack"));
//        card.setRetreatCost(parseInteger(rawCard.get("retreatCost")));
//        card.setWeakness((String) rawCard.get("weakness"));
//        card.setEvolvesFrom((String) rawCard.get("evolvesFrom"));
//        card.setRarity((String) rawCard.get("rarity"));
    }

    /**
     * 카드의 기술 정보 업데이트
     * @param card 업데이트할 PokemonCard 엔티티
     * @param rawCard Map 형태의 카드 데이터
     */
    @SuppressWarnings("unchecked")
    private void updateAttacks(PokemonCard card, Map<String, Object> rawCard) {
        List<Map<String, Object>> rawAttacks = (List<Map<String, Object>>) rawCard.get("attacks");
        log.debug("8. 공격 데이터 처리: code={}, rawAttacks={}", card.getCode(), 
            rawAttacks != null ? rawAttacks.size() : 0);
        
        if (rawAttacks == null) {
//            card.setAttacks(new ArrayList<>());
            return;
        }

        List<PokemonAttack> attacks = rawAttacks.stream()
            .map(attack -> {
                try {
                    return new PokemonAttack(
                        null,
                        card.getCode(),
                        (String) attack.get("name"),
                        (String) attack.get("name_ko"),
                        (String) attack.get("effect"),
                        (String) attack.get("effect_ko"),
                        (String) attack.get("damage"),
                        formatCost((List<String>) attack.get("cost")),
                        card
                    );
                } catch (Exception e) {
                    log.error("Error processing attack for card {}: {}", card.getCode(), e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
//        card.setAttacks(attacks);
    }

    /**
     * 카드의 특성 정보 업데이트
     * @param card 업데이트할 PokemonCard 엔티티
     * @param rawCard Map 형태의 카드 데이터
     */
    @SuppressWarnings("unchecked")
    private void updateAbilities(PokemonCard card, Map<String, Object> rawCard) {
        List<Map<String, Object>> rawAbilities = (List<Map<String, Object>>) rawCard.get("abilities");
        log.debug("9. 특성 데이터 처리: code={}, rawAbilities={}", card.getCode(), 
            rawAbilities != null ? rawAbilities.size() : 0);
        
        if (rawAbilities == null) {
//            card.setAbilities(new ArrayList<>());
            return;
        }

        List<PokemonAbility> abilities = rawAbilities.stream()
            .map(ability -> {
                try {
                    return new PokemonAbility(
                        null,
                        card.getCode(),
                        (String) ability.get("name"),
                        (String) ability.get("name_ko"),
                        (String) ability.get("effect"),
                        (String) ability.get("effect_ko"),
                        card
                    );
                } catch (Exception e) {
                    log.error("Error processing ability for card {}: {}", card.getCode(), e.getMessage());
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        
//        card.setAbilities(abilities);
    }

    /**
     * Object를 Integer로 변환
     * @param value 변환할 Object
     * @return 변환된 Integer 값 (변환 실패 시 null)
     */
    private Integer parseInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                log.warn("Failed to parse integer value: {}", value);
                return null;
            }
        }
        return null;
    }

    /**
     * 기술 비용 리스트를 문자열로 변환
     * @param costs 기술 비용 리스트
     * @return 쉼표로 구분된 비용 문자열
     */
    private String formatCost(List<String> costs) {
        if (costs == null || costs.isEmpty()) {
            return "COLORLESS";
        }
        return String.join(",", costs);
    }
} 