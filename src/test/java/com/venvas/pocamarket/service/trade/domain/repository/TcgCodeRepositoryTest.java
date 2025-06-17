package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.config.TestConfig;
import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
/**
 * TcgCode 레포지토리 테스트
 * 실제 DB를 사용하여 친구 코드 관련 쿼리 기능을 테스트합니다.
 */
@DataJpaTest
@ActiveProfiles("test")
@Import({TestConfig.class})
class TcgCodeRepositoryTest {

    @Autowired
    private TcgCodeRepository tcgCodeRepository;

    private final String TEST_UUID = "test-uuid";
    private final int STATUS_ACTIVE = 1;

    /**
     * 사용자별 활성화된 친구 코드 개수 조회 테스트
     * 시나리오:
     * 1. 두 개의 활성화된 친구 코드 저장
     * 2. 해당 사용자의 활성화된 코드 개수가 2개여야 함
     */
    @Test
    @DisplayName("UUID와 상태로 코드 개수 조회")
    void countByUuidAndStatus() {
        // given
        tcgCodeRepository.save(new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모1"));
        tcgCodeRepository.save(new TcgCode(null, "6543210987654321", TEST_UUID, STATUS_ACTIVE, "메모2"));

        // when
        Long count = tcgCodeRepository.countByUuidAndStatus(TEST_UUID, STATUS_ACTIVE);

        // then
        assertThat(count).isEqualTo(2);
    }

    /**
     * 사용자별 활성화된 친구 코드 목록 조회 테스트
     * 시나리오:
     * 1. 두 개의 활성화된 코드와 한 개의 비활성화된 코드 저장
     * 2. 활성화된 코드 2개만 조회되어야 함
     */
    @Test
    @DisplayName("UUID와 상태로 모든 코드 조회")
    void findAllByUuidAndStatus() {
        // given
        tcgCodeRepository.save(new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모1"));
        tcgCodeRepository.save(new TcgCode(null, "6543210987654321", TEST_UUID, STATUS_ACTIVE, "메모2"));
        tcgCodeRepository.save(new TcgCode(null, "9999999999999999", TEST_UUID, 0, "삭제된 메모"));

        // when
        List<TcgCode> results = tcgCodeRepository.findAllByUuidAndStatus(TEST_UUID, STATUS_ACTIVE);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(code -> code.getStatus() == STATUS_ACTIVE);
    }

    /**
     * 수정할 친구 코드 조회 테스트
     * 시나리오:
     * 1. 하나의 활성화된 친구 코드 저장
     * 2. ID, UUID, 상태로 정확한 코드가 조회되어야 함
     */
    @Test
    @DisplayName("ID, UUID, 상태로 수정할 코드 조회")
    void findUpdateCode() {
        // given
        TcgCode savedCode = tcgCodeRepository.save(
                new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모")
        );

        // when
        Optional<TcgCode> result = tcgCodeRepository.findUpdateCode(
                savedCode.getId(), TEST_UUID, STATUS_ACTIVE
        );

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTcgCode()).isEqualTo("1234567890123456");
        assertThat(result.get().getUuid()).isEqualTo(TEST_UUID);
        assertThat(result.get().getStatus()).isEqualTo(STATUS_ACTIVE);
    }
}