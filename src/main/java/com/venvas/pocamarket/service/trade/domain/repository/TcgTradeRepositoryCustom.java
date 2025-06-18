package com.venvas.pocamarket.service.trade.domain.repository;

import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListRequest;
import com.venvas.pocamarket.service.trade.application.dto.TcgTradeListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TcgTradeRepositoryCustom {

    Page<TcgTradeListResponse> searchFilterList(TcgTradeListRequest request, Pageable pageable, String userUuid, boolean isAdmin);
}
