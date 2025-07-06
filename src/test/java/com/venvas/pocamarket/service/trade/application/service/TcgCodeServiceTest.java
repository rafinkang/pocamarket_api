package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.config.TestConfig;
import com.venvas.pocamarket.service.trade.application.dto.TcgCodeSimpleDto;
import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeException;
import com.venvas.pocamarket.service.trade.domain.repository.TcgCodeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * TcgCode 서비스 테스트
 * 친구 코드의 생성, 조회, 수정, 삭제 기능을 테스트합니다.
 */
@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
class TcgCodeServiceTest {

    @InjectMocks
    private TcgCodeService tcgCodeService;

    @Mock
    private TcgCodeRepository tcgCodeRepository;

    // 테스트에서 사용할 공통 상수
    private final String TEST_UUID = "test-uuid";
    private final int STATUS_ACTIVE = 1;

    /**
     * 친구 코드 생성 성공 테스트
     * 시나리오:
     * 1. 현재 등록된 코드 수가 최대값(5개) 미만일 때
     * 2. 유효한 친구 코드와 메모를 입력
     * 3. 정상적으로 저장되고 결과가 반환되어야 함
     */
    @Test
    @DisplayName("친구 코드 생성 - 성공")
    void createTcgCode_Success() {
        // given - 테스트 데이터 준비
        TcgCodeSimpleDto dto = new TcgCodeSimpleDto(null, "1234567890123456", "테스트 메모");
        TcgCode savedTcgCode = new TcgCode(1L, dto.getTcgCode(), TEST_UUID, STATUS_ACTIVE, dto.getMemo());

        // Mock 동작 정의
        when(tcgCodeRepository.countByUuidAndStatus(TEST_UUID, STATUS_ACTIVE)).thenReturn(0L);
        when(tcgCodeRepository.save(any(TcgCode.class))).thenReturn(savedTcgCode);

        // when - 테스트 실행
        TcgCodeSimpleDto result = tcgCodeService.createTcgCode(dto, TEST_UUID);

        // then - 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.getTcgCode()).isEqualTo(dto.getTcgCode());
        assertThat(result.getMemo()).isEqualTo(dto.getMemo());
        verify(tcgCodeRepository).save(any(TcgCode.class));
    }

    /**
     * 친구 코드 생성 실패 테스트 - 최대 개수 초과
     * 시나리오:
     * 1. 이미 5개의 활성화된 친구 코드가 있는 상태
     * 2. 새로운 친구 코드 추가 시도
     * 3. TCG_CODE_MAX_COUNT_OVER 예외가 발생해야 함
     */
    @Test
    @DisplayName("친구 코드 생성 - 최대 개수 초과 실패")
    void createTcgCode_MaxCountExceeded() {
        // given
        TcgCodeSimpleDto dto = new TcgCodeSimpleDto(null, "1234567890123456", "테스트 메모");
        when(tcgCodeRepository.countByUuidAndStatus(TEST_UUID, STATUS_ACTIVE)).thenReturn(5L);

        // when & then
        assertThatThrownBy(() -> tcgCodeService.createTcgCode(dto, TEST_UUID))
                .isInstanceOf(TcgCodeException.class)
                .hasFieldOrPropertyWithValue("errorCode", TcgCodeErrorCode.TCG_CODE_MAX_COUNT_OVER);
    }

    /**
     * 친구 코드 목록 조회 테스트
     * 시나리오:
     * 1. 사용자의 활성화된 친구 코드가 2개 있는 상태
     * 2. 목록 조회 시 2개의 코드가 정상적으로 반환되어야 함
     */
    @Test
    @DisplayName("친구 코드 목록 조회 - 성공")
    void getTcgCodeList_Success() {
        // given
        List<TcgCode> tcgCodes = Arrays.asList(
                new TcgCode(1L, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모1"),
                new TcgCode(2L, "6543210987654321", TEST_UUID, STATUS_ACTIVE, "메모2")
        );
        when(tcgCodeRepository.findAllByUuidAndStatus(TEST_UUID, STATUS_ACTIVE)).thenReturn(tcgCodes);

        // when
        List<TcgCodeSimpleDto> result = tcgCodeService.getTcgCodeList(TEST_UUID);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTcgCode()).isEqualTo("1234567890123456");
        assertThat(result.get(1).getTcgCode()).isEqualTo("6543210987654321");
    }

    /**
     * 친구 코드 수정 성공 테스트
     * 시나리오:
     * 1. 기존에 등록된 친구 코드가 있음
     * 2. 해당 코드의 소유자가 수정을 시도
     * 3. 정상적으로 수정되고 결과가 반환되어야 함
     */
    @Test
    @DisplayName("친구 코드 수정 - 성공")
    void updateTcgCode_Success() {
        // given
        Long codeId = 1L;
        TcgCodeSimpleDto dto = new TcgCodeSimpleDto(codeId, "1234567890123456", "수정된 메모");
        TcgCode existingCode = new TcgCode(codeId, "0000000000000000", TEST_UUID, STATUS_ACTIVE, "원래 메모");

        when(tcgCodeRepository.findUpdateCode(codeId, TEST_UUID, STATUS_ACTIVE))
                .thenReturn(Optional.of(existingCode));

        // when
        TcgCodeSimpleDto result = tcgCodeService.updateTcgCode(codeId, dto, TEST_UUID);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTcgCode()).isEqualTo(dto.getTcgCode());
        assertThat(result.getMemo()).isEqualTo(dto.getMemo());
    }

    /**
     * 친구 코드 수정 실패 테스트 - 권한 없음
     * 시나리오:
     * 1. 다른 사용자의 친구 코드에 대해 수정 시도
     * 2. INSUFFICIENT_PERMISSION 예외가 발생해야 함
     */
    @Test
    @DisplayName("친구 코드 수정 - 권한 없음 실패")
    void updateTcgCode_InsufficientPermission() {
        // given
        Long codeId = 1L;
        String differentUUID = "different-uuid";
        TcgCodeSimpleDto dto = new TcgCodeSimpleDto(codeId, "1234567890123456", "수정된 메모");
        TcgCode existingCode = new TcgCode(codeId, "0000000000000000", TEST_UUID, STATUS_ACTIVE, "원래 메모");

        when(tcgCodeRepository.findUpdateCode(codeId, differentUUID, STATUS_ACTIVE))
                .thenReturn(Optional.of(existingCode));

        // when & then
        assertThatThrownBy(() -> tcgCodeService.updateTcgCode(codeId, dto, differentUUID))
                .isInstanceOf(TcgCodeException.class)
                .hasFieldOrPropertyWithValue("errorCode", TcgCodeErrorCode.INSUFFICIENT_PERMISSION);
    }

    /**
     * 친구 코드 삭제 성공 테스트
     * 시나리오:
     * 1. 기존에 등록된 친구 코드가 있음
     * 2. 해당 코드의 소유자가 삭제를 시도
     * 3. 정상적으로 상태가 변경(삭제)되어야 함
     */
    @Test
    @DisplayName("친구 코드 삭제 - 성공")
    void deleteTcgCode_Success() {
        // given
        Long codeId = 1L;
        TcgCode existingCode = new TcgCode(codeId, "1234567890123456", TEST_UUID, STATUS_ACTIVE, "메모");

        when(tcgCodeRepository.findUpdateCode(codeId, TEST_UUID, STATUS_ACTIVE))
                .thenReturn(Optional.of(existingCode));

        // when
        Boolean result = tcgCodeService.deleteTcgCode(codeId, TEST_UUID);

        // then
        assertThat(result).isTrue();
        assertThat(existingCode.getStatus()).isZero();
    }
}