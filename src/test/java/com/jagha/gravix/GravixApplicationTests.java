package com.jagha.gravix;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootTest
class GravixApplicationTests {

	@MockitoBean
	private KafkaTemplate<String, Object> kafkaTemplate;

	@Test
	void contextLoads() {
	}

}
