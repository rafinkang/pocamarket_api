package com.venvas.pocamarket.common.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest {
    
    @Min(value = 1, message = "페이지는 1부터 시작합니다.")
    private int page = 1; // 1-based 페이지 번호 (기본값: 1)
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 30, message = "페이지 크기는 30 이하여야 합니다.")
    private int size = 10; // 페이지 크기 (기본값: 10)
    
    private String sort; // 정렬 정보 (예: "name,asc" 또는 "createdAt,desc")
    
    /**
     * 1-based 페이지 번호를 0-based Spring Pageable로 변환
     */
    public Pageable toPageable() {
        int zeroBasedPage = Math.max(0, page - 1); // 1-based를 0-based로 변환
        
        if (sort != null && !sort.trim().isEmpty()) {
            return org.springframework.data.domain.PageRequest.of(
                zeroBasedPage, 
                size, 
                parseSortString(sort)
            );
        }
        
        return org.springframework.data.domain.PageRequest.of(zeroBasedPage, size);
    }
    
    /**
     * 정렬 문자열을 Sort 객체로 변환
     * 단일 정렬: "name,asc" -> Sort.by(Sort.Direction.ASC, "name")
     * 다중 정렬: "code,asc;name,desc" -> Sort.by(Sort.Direction.ASC, "code").and(Sort.by(Sort.Direction.DESC, "name"))
     */
    private Sort parseSortString(String sortStr) {
        if (sortStr == null || sortStr.trim().isEmpty()) {
            return Sort.unsorted();
        }
        
        // 세미콜론으로 여러 정렬 조건 분리
        String[] sortConditions = sortStr.split(";");
        Sort sort = Sort.unsorted();
        
        for (String condition : sortConditions) {
            String[] parts = condition.trim().split(",");
            if (parts.length == 0) continue;
            
            String property = parts[0].trim();
            if (property.isEmpty()) continue;
            
            // 방향 결정 (기본값: ASC)
            Sort.Direction direction = Sort.Direction.ASC;
            if (parts.length > 1) {
                String directionStr = parts[1].trim().toLowerCase();
                if ("desc".equals(directionStr)) {
                    direction = Sort.Direction.DESC;
                }
            }
            
            // 정렬 조건 추가
            Sort currentSort = Sort.by(direction, property);
            sort = sort.isUnsorted() ? currentSort : sort.and(currentSort);
        }
        
        return sort;
    }
} 