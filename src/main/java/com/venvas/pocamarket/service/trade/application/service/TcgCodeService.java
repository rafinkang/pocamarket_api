package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.trade.application.dto.TcgCodeSimpleDto;
import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeErrorCode;
import com.venvas.pocamarket.service.trade.domain.exception.TcgCodeException;
import com.venvas.pocamarket.service.trade.domain.repository.TcgCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class TcgCodeService {

    private final TcgCodeRepository tcgCodeRepository;
    private final int STATUS_ACTIVE = 1;
    private final int MAX_TCG_CODE = 5;

    @Transactional
    public TcgCodeSimpleDto createTcgCode(TcgCodeSimpleDto tcgCodeSimpleDto, String userUuid) {
        Long count = tcgCodeRepository.countByUuidAndStatus(userUuid, STATUS_ACTIVE);

        if(count >= MAX_TCG_CODE) {
            throw new TcgCodeException(TcgCodeErrorCode.TCG_CODE_MAX_COUNT_OVER);
        }

        TcgCode newTcgCode = new TcgCode(null, tcgCodeSimpleDto.getTcgCode(), userUuid, STATUS_ACTIVE, tcgCodeSimpleDto.getMemo());
        TcgCode saveTcgCode = tcgCodeRepository.save(newTcgCode);
        return new TcgCodeSimpleDto(saveTcgCode.getId(), saveTcgCode.getTcgCode(), saveTcgCode.getMemo());
    }

    public List<TcgCodeSimpleDto> getTcgCodeList(String userUuid) {
        return tcgCodeRepository.findAllByUuidAndStatus(userUuid, STATUS_ACTIVE)
                .stream()
                .map(code -> new TcgCodeSimpleDto(code.getId(), code.getTcgCode(), code.getMemo()))
                .toList();
    }

    @Transactional
    public TcgCodeSimpleDto updateTcgCode(Long codeId, TcgCodeSimpleDto tcgCodeSimpleDto, String userUuid) {
        if(!codeId.equals(tcgCodeSimpleDto.getTcgCodeId())) {
            throw new TcgCodeException(TcgCodeErrorCode.TCG_CODE_NOT_EQUALS);
        }

        TcgCode findTcgCode = tcgCodeRepository.findUpdateCode(codeId, userUuid, STATUS_ACTIVE)
                .orElseThrow(() -> new TcgCodeException(TcgCodeErrorCode.TCG_CODE_NOT_FOUND));

        authorizedCheck(findTcgCode.getUuid(), userUuid);

        findTcgCode.updateTcgCode(tcgCodeSimpleDto.getTcgCode(), tcgCodeSimpleDto.getMemo());
        return new TcgCodeSimpleDto(findTcgCode.getId(), findTcgCode.getTcgCode(), findTcgCode.getMemo());
    }

    @Transactional
    public Boolean deleteTcgCode(Long codeId, String userUuid) {
        TcgCode findTcgCode = tcgCodeRepository.findUpdateCode(codeId, userUuid, STATUS_ACTIVE)
                .orElseThrow(() -> new TcgCodeException(TcgCodeErrorCode.TCG_CODE_NOT_FOUND));

        authorizedCheck(findTcgCode.getUuid(), userUuid);

        findTcgCode.deleteTcgCode();
        return true;
    }

    /**
     * 인증 체크, 친구 코드에 등록된 UUID와 유저 UUID 비교
     */
    private void authorizedCheck(String targetUuid, String requestUserUuid) {
        if(!targetUuid.equals(requestUserUuid)) {
            throw new TcgCodeException(TcgCodeErrorCode.INSUFFICIENT_PERMISSION);
        }
    }
}
