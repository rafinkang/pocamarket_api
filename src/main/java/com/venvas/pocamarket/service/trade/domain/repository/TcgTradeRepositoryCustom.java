package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListDto;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TcgTradeRepositoryCustom {

    Page<TcgTradeListDto> searchFilterList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin);
}
