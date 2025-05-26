package com.venvas.pocamarket.common.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MappingData {

    public static <T, R> List<R> mappingDataList(List<T> dtoList, Function<T, R> mapper) {
        if (dtoList == null || dtoList.isEmpty()) return null;
        return dtoList.stream()
                .filter(Objects::nonNull)
                .map(mapper)
                .toList();
    }
}
