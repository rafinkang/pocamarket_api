package com.venvas.pocamarket.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Slf4j
public class ReadDataListJson<T> {

    @Setter
    private String url;
    @Setter
    private String fileName;
    private List<T> jsonList;

    public ReadDataListJson(String fileName) {
        this.url = "/sample";
        this.fileName = fileName;
    }

    public ReadDataListJson<T> readJson(Class<T> clazz) {
        // JSON 데이터를 Java 객체로 변환하기 위한 ObjectMapper 인스턴스 생성
        ObjectMapper mapper = new ObjectMapper();
        // resources/sample 디렉토리에서 version.json 파일을 읽어옴
        InputStream inputStream = getClass().getResourceAsStream(url + "/" + fileName + ".json");
        // JSON 데이터를 List<PokemonCardDto> 타입으로 변환하기 위한 타입 참조 생성
        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, clazz);

        jsonList = convertJsonToList(mapper, inputStream, listType);

        return this;
    }

    public Optional<List<T>> getJsonList() {
        return Optional.ofNullable(jsonList);
    }

    private List<T> convertJsonToList(ObjectMapper mapper, InputStream inputStream, CollectionType listType) {
        try {
            return mapper.readValue(inputStream, listType);
        } catch (IOException e) {
            log.error("JSON 파싱 실패", e);
            throw new IllegalArgumentException("json 파일 읽기 또는 리스트 변환 실패", e);
        } catch (IllegalArgumentException e) {
            log.info("경로 읽기 실패 경로 = {}", url + "/" + fileName + ".json");
            throw new IllegalArgumentException("json 파일 경로 설정 error", e);
        }
    }
}
