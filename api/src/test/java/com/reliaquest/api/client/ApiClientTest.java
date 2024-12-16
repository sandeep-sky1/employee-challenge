package com.reliaquest.api.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.reliaquest.api.config.ClientConfig;
import com.reliaquest.api.model.*;
import java.util.UUID;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = ClientConfig.class)
class ApiClientTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private ApiClient apiClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String id_1 = "74dfebc1-9a57-464f-a27c-c66a04ec2c87";

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8080));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);
    }

    @AfterAll
    static void teardownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void testGetAllEmployees() {
        EmployeeDataResponse mockResponse = new EmployeeDataResponse(
                Lists.list(
                        new Employee(UUID.fromString(id_1), "John Doe", 50000, 30, "Developer", "johndoe@example.com")),
                "Successfully processed request.");

        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/employee"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\"data\":[{\"id\":\"74dfebc1-9a57-464f-a27c-c66a04ec2c87\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"johndoe@example.com\"}], \"status\": \"Successfully processed request.\"}")));

        EmployeeDataResponse response = apiClient.getAllEmployees();

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/employee")));

        assertNotNull(response);
        assertEquals(mockResponse.data().size(), response.data().size());
        assertEquals(mockResponse.status(), response.status());
        assertEquals(mockResponse.data().get(0).name(), response.data().get(0).name());
        assertEquals(mockResponse.data().get(0).age(), response.data().get(0).age());
        assertEquals(mockResponse.data().get(0).title(), response.data().get(0).title());
        assertEquals(mockResponse.data().get(0).email(), response.data().get(0).email());
        assertEquals(mockResponse.data().get(0).id(), response.data().get(0).id());
    }

    @Test
    void testGetEmployeeById() {
        EmployeeResponse mockResponse = new EmployeeResponse(
                new Employee(UUID.fromString(id_1), "John Doe", 50000, 30, "Developer", "johndoe@example.com"),
                "Successfully processed request.");

        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/employee/" + id_1))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                "{\"data\":{\"id\":\"74dfebc1-9a57-464f-a27c-c66a04ec2c87\",\"employee_name\":\"John Doe\",\"employee_salary\":50000,\"employee_age\":30,\"employee_title\":\"Developer\",\"employee_email\":\"johndoe@example.com\"}, \"status\": \"Successfully processed request.\"}")));

        EmployeeResponse response = apiClient.getEmployeeById(id_1);

        wireMockServer.verify(getRequestedFor(urlEqualTo("/api/v1/employee/" + id_1)));

        assertNotNull(response);
        assertEquals(mockResponse.data(), response.data());
        assertEquals(mockResponse.status(), response.status());
        assertEquals(mockResponse.data().name(), response.data().name());
        assertEquals(mockResponse.data().age(), response.data().age());
        assertEquals(mockResponse.data().title(), response.data().title());
        assertEquals(mockResponse.data().email(), response.data().email());
        assertEquals(mockResponse.data().id(), response.data().id());
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeInput input = new EmployeeInput("Jane Doe", 60000, 25, "Analyst");
        String mockResponseJson =
                "{\"id\":\"f52c4ad1-85f8-4d6f-b29a-d8c38d2e50cd\",\"employee_name\":\"Jane Doe\",\"employee_salary\":60000,\"employee_age\":25,\"employee_title\":\"Analyst\",\"employee_email\":\"janedoe@example.com\"}";
        String inputJson = objectMapper.writeValueAsString(input);

        String id_2 = "f52c4ad1-85f8-4d6f-b29a-d8c38d2e50cd";
        Employee mockEmployee =
                new Employee(UUID.fromString(id_2), "Jane Doe", 60000, 25, "Analyst", "janedoe@example.com");

        wireMockServer.stubFor(post(urlEqualTo("/api/v1/employee"))
                .withRequestBody(containing(inputJson))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(mockResponseJson)));

        Employee response = apiClient.createEmployee(input);

        wireMockServer.verify(postRequestedFor(urlEqualTo("/api/v1/employee")).withRequestBody(containing(inputJson)));

        assertNotNull(response);
        assertEquals(mockEmployee.name(), response.name());
        assertEquals(mockEmployee.salary(), response.salary());
        assertEquals(mockEmployee.age(), response.age());
        assertEquals(mockEmployee.title(), response.title());
        assertEquals(mockEmployee.email(), response.email());
        assertEquals(mockEmployee.id(), response.id());
    }

    @Test
    void testDeleteEmployeeByName() throws Exception {
        DeleteEmployeeResponse mockResponse = new DeleteEmployeeResponse(true, "success", null);

        wireMockServer.stubFor(delete(urlEqualTo("/api/v1/employee"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(mockResponse))));

        DeleteEmployeeResponse deleteEmployeeResponse =
                apiClient.deleteEmployeeByName(new DeleteEmployeeInput("John Doe"));

        wireMockServer.verify(deleteRequestedFor(urlEqualTo("/api/v1/employee"))
                .withRequestBody(containing("{\"name\":\"John Doe\"}")));

        assertEquals(mockResponse.status(), deleteEmployeeResponse.status());
    }
}
