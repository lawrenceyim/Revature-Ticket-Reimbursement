package com.revature.ticket_reimbursement;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revature.ticket_reimbursement.entity.Ticket;
import com.revature.ticket_reimbursement.enums.ReimbursementType;
import com.revature.ticket_reimbursement.enums.TicketStatus;
import com.revature.ticket_reimbursement.utils.StatusCodeTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class GetTicketTests {
    ApplicationContext app;
    HttpClient webClient;
    ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() throws InterruptedException {
        webClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        String[] args = new String[]{};
        app = SpringApplication.run(TicketReimbursementApplication.class, args);
        Thread.sleep(500);
    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(500);
        SpringApplication.exit(app);
    }

    @Test
    public void GetAllTicketTest() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tickets/"))
                .build();
        HttpResponse<String> response = webClient.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        StatusCodeTest.assertEquals(200, status);

        List<Ticket> expectedResult = new ArrayList<Ticket>();
        expectedResult.add(new Ticket(9997, 9997, "Test example of a pending ticket.",
                ReimbursementType.TRAVEL, TicketStatus.PENDING, new BigDecimal("999.99")));
        expectedResult.add(new Ticket(9998, 9997, "Test example of a denied ticket.",
                ReimbursementType.TRAVEL, TicketStatus.DENIED, new BigDecimal("999.99")));
        expectedResult.add(new Ticket(9999, 9997, "Test example of an approved ticket.",
                ReimbursementType.TRAVEL, TicketStatus.APPROVED, new BigDecimal("999.99")));

        List<Ticket> actualResult = objectMapper.readValue(response.body(), new TypeReference<>() {});
        Assertions.assertEquals(expectedResult, actualResult, "Expected = " + expectedResult +
                ", Actual = " + actualResult);
    }
}
