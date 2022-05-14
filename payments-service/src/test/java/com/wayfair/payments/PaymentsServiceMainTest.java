package com.wayfair.payments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class PaymentsServiceMainTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void swagger() {
        ResponseEntity<String> swaggerResponse = testRestTemplate.getForEntity("/swagger", String.class);
        assertThat(swaggerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(swaggerResponse.getBody()).contains("swagger-ui");
        assertThat(swaggerResponse.getHeaders().getContentType()).isEqualTo(MediaType.TEXT_HTML);
    }
}