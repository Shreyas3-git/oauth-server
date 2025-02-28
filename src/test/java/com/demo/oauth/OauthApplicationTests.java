package com.demo.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class OauthApplicationTests {

	@Test
	public void testBase64Decoding() {
		String base64Secret = "lZMDLijHbydrtz3MYXS42zTNqc5AYrfig6mMRsiwSYgdRSJHMCzLsC2uUuXqNnzje6fIW+v2WoDGC2n5o9P39g==";
		assertDoesNotThrow(() -> Base64.getDecoder().decode(base64Secret));
	}

}
