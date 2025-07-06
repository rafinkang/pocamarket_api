package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.config.RepositoryTestAnnotations;
import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TcgCode 레포지토리 테스트
 * H2 인메모리 DB를 사용하여 친구 코드 관련 쿼리 기능을 테스트합니다.
 */
@RepositoryTestAnnotations
class TcgCodeRepositoryTest {

    @Autowired
    private TcgCodeRepository tcgCodeRepository;
    
    @Autowired
    private TestEntityManager testEntityManager;

    private final String TEST_UUID = "test-uuid";
    private final int STATUS_ACTIVE = 1;

    @Test
    @DisplayName("UUID와 상태로 코드 개수 조회")
    void countByUuidAndStatus() {
        // given
        TcgCode code1 = new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모1");
        TcgCode code2 = new TcgCode(null, "6543210987654321", TEST_UUID, STATUS_ACTIVE, "메모2");
        
        testEntityManager.persistAndFlush(code1);
        testEntityManager.persistAndFlush(code2);

        // when
        Long count = tcgCodeRepository.countByUuidAndStatus(TEST_UUID, STATUS_ACTIVE);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("UUID와 상태로 모든 코드 조회")
    void findAllByUuidAndStatus() {
        // given
        TcgCode code1 = new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모1");
        TcgCode code2 = new TcgCode(null, "6543210987654321", TEST_UUID, STATUS_ACTIVE, "메모2");
        TcgCode code3 = new TcgCode(null, "9999999999999999", TEST_UUID, 0, "삭제된 메모");
        
        testEntityManager.persistAndFlush(code1);
        testEntityManager.persistAndFlush(code2);
        testEntityManager.persistAndFlush(code3);

        // when
        List<TcgCode> results = tcgCodeRepository.findAllByUuidAndStatus(TEST_UUID, STATUS_ACTIVE);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(code -> code.getStatus() == STATUS_ACTIVE);
    }

    @Test
    @DisplayName("ID, UUID, 상태로 수정할 코드 조회")
    void findUpdateCode() {
        // given
        TcgCode code = new TcgCode(null, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모");
        TcgCode savedCode = testEntityManager.persistAndFlush(code);

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