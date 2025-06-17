package com.venvas.pocamarket;

import com.venvas.pocamarket.config.TestConfig;
import com.venvas.pocamarket.config.TestQueryDslConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import({TestConfig.class, TestQueryDslConfig.class})
class PocamarketApplicationTests {

    @Test
    void contextLoads() {
    }

}
