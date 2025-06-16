package com.venvas.pocamarket.service.trade.application.service;

import com.venvas.pocamarket.service.trade.application.dto.TcgCodeSimpleDto;
import com.venvas.pocamarket.service.trade.domain.entity.TcgCode;
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
    private final Integer STATUS_ACTIVE = 1;

    @Transactional
    public TcgCodeSimpleDto createTcgCode(TcgCodeSimpleDto tcgCodeSimpleDto, String userUUID) {
        try {
            TcgCode newTcgCode = new TcgCode(null, tcgCodeSimpleDto.getTcgCode(), userUUID, STATUS_ACTIVE, tcgCodeSimpleDto.getMemo());
            validateTcgCode(newTcgCode);
            TcgCode saveTcgCode = tcgCodeRepository.save(newTcgCode);
            return new TcgCodeSimpleDto(saveTcgCode.getId(), saveTcgCode.getTcgCode(), saveTcgCode.getMemo());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<TcgCodeSimpleDto> getTcgCodeList(String userUUID) {
        return tcgCodeRepository.findAllByUuidAndStatus(userUUID, STATUS_ACTIVE)
                .stream()
                .map(code -> new TcgCodeSimpleDto(code.getId(), code.getTcgCode(), code.getMemo()))
                .toList();
    }

    private void validateTcgCode(TcgCode tcgCode) {

    }
}
