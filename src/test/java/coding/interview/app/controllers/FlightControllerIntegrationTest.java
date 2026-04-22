package coding.interview.app.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:flight-controller-integration-test.properties")
class FlightControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // -------------------------------------------------------------------------
    // GET /flights
    // -------------------------------------------------------------------------

    @Test
    void getAllFlights_returnsAllSeededFlights() throws Exception {
        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].code", is("FL10")))
                .andExpect(jsonPath("$[1].code", is("FL11")))
                .andExpect(jsonPath("$[2].code", is("FL12")))
                .andExpect(jsonPath("$[3].code", is("FL13")))
                .andExpect(jsonPath("$[4].code", is("FL14")));
    }

    @Test
    void getAllFlights_responseContainsExpectedFields() throws Exception {
        mockMvc.perform(get("/flights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].origin", is("GRU")))
                .andExpect(jsonPath("$[0].destination", is("ATL")))
                .andExpect(jsonPath("$[0].status", is("confirmed")));
    }

    // -------------------------------------------------------------------------
    // GET /flights/{id}
    // -------------------------------------------------------------------------

    @Test
    void getFlightById_existingId_returnsFlightWith200() throws Exception {
        mockMvc.perform(get("/flights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("FL10")))
                .andExpect(jsonPath("$.origin", is("GRU")))
                .andExpect(jsonPath("$.destination", is("ATL")))
                .andExpect(jsonPath("$.status", is("confirmed")));
    }

    @Test
    void getFlightById_anotherExistingId_returnsCorrectFlight() throws Exception {
        mockMvc.perform(get("/flights/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.code", is("FL12")))
                .andExpect(jsonPath("$.origin", is("IST")))
                .andExpect(jsonPath("$.destination", is("LHR")))
                .andExpect(jsonPath("$.status", is("cancelled")));
    }

    @Test
    void getFlightById_nonExistingId_returns404() throws Exception {
        mockMvc.perform(get("/flights/999")).andExpect(status().isNotFound());
    }

    // -------------------------------------------------------------------------
    // PUT /flights/{id}
    // -------------------------------------------------------------------------

    @Test
    void updateFlight_existingId_returns200WithUpdatedData() throws Exception {
        String requestBody = """
                {
                    "code": "FL10-UPD",
                    "origin": "GRU",
                    "destination": "JFK",
                    "status": "delayed"
                }
                """;

        mockMvc.perform(put("/flights/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.code", is("FL10-UPD")))
                .andExpect(jsonPath("$.origin", is("GRU")))
                .andExpect(jsonPath("$.destination", is("JFK")))
                .andExpect(jsonPath("$.status", is("delayed")));
    }

    @Test
    void updateFlight_persistsChanges() throws Exception {
        String requestBody = """
                {
                    "code": "FL11-UPD",
                    "origin": "DXB",
                    "destination": "SYD",
                    "status": "delayed"
                }
                """;

        mockMvc.perform(put("/flights/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/flights/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is("FL11-UPD")))
                .andExpect(jsonPath("$.destination", is("SYD")))
                .andExpect(jsonPath("$.status", is("delayed")));
    }

    @Test
    void updateFlight_nonExistingId_returns404() throws Exception {
        String requestBody = """
                {
                    "code": "XX99",
                    "origin": "AAA",
                    "destination": "BBB",
                    "status": "confirmed"
                }
                """;

        mockMvc.perform(put("/flights/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }
}
