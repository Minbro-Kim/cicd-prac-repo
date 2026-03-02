package com.sprint.mission.discodeit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DiscodeitApplicationTests {

	@Test
	@DisplayName("Application Context Loading Test")
	void contextLoads() {
		assertDoesNotThrow(()->{

		}, "No exception thrown");
	}

}
