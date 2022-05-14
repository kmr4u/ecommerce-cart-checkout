package com.wayfair.payments;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JsonContentAssert;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.wayfair.payments.PaymentException.ErrorCode.BASKET_DUPLICATE;
import static com.wayfair.payments.PaymentException.ErrorCode.TEMPORARY_PAYMENT_FAILURE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    private final static String SAMPLE_REQUEST = "{\"basketId\": \"1234-5678\",\"paymentValue\": 1234.11}";
    private static final String API_BASE_PATH_V1 = "/api/v1/payments";

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testPaymentSuccess() throws Exception {
        String paymentId = UUID.randomUUID().toString();
        when(paymentService.requestPayment(any(), any())).thenReturn(paymentId);
        mockMvc.perform(post(API_BASE_PATH_V1).contentType(APPLICATION_JSON).content(SAMPLE_REQUEST))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(
                        content().json(String.format("{\"basketId\":\"1234-5678\",\"paymentId\":\"%s\"}", paymentId)));
    }

    @Test
    void testPaymentServiceBasketDuplicate() throws Exception {
        when(paymentService.requestPayment(any(), any())).thenThrow(new PaymentException(BASKET_DUPLICATE, "b123", "Simulate basket duplicate"));
        mockMvc.perform(post(API_BASE_PATH_V1).contentType(APPLICATION_JSON).content(SAMPLE_REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(content().json("{\"errorMessage\":\"Simulate basket duplicate\"," + "\"errorCode\":\"BASKET_DUPLICATE\"}"));
    }

    @Test
    void testPaymentServiceTemporaryError() throws Exception {
        when(paymentService.requestPayment(any(), any())).thenThrow(
                new PaymentException(TEMPORARY_PAYMENT_FAILURE, "t123", "Simulate temporary error"));
        mockMvc.perform(post(API_BASE_PATH_V1).contentType(APPLICATION_JSON).content(SAMPLE_REQUEST))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().json("{\"errorMessage\":\"Simulate temporary error\"," + "\"errorCode\":\"TEMPORARY_PAYMENT_FAILURE\"}"));
    }

    @Test
    void testPaymentServiceUncaughtException() throws Exception {
        when(paymentService.requestPayment(any(), any())).thenThrow(new RuntimeException("BOOM!"));
        mockMvc.perform(post(API_BASE_PATH_V1).contentType(APPLICATION_JSON).content(SAMPLE_REQUEST))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"errorMessage\":\"BOOM!\",\"errorCode\":null}"));
    }

    @Test
    void testPaymentServiceNegativePayment(
            @Value("classpath:validationErrorSample.json") Resource validationErrorSampleJson) throws Exception {
        String invalidRequest = SAMPLE_REQUEST.replace("1234.11", "-1234.11");
        MockHttpServletResponse result = mockMvc.perform(
                post(API_BASE_PATH_V1).contentType(APPLICATION_JSON).content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();
        new JsonContentAssert(this.getClass(), result.getContentAsString()).isEqualToJson(validationErrorSampleJson);
        verifyNoInteractions(paymentService);

    }

}
