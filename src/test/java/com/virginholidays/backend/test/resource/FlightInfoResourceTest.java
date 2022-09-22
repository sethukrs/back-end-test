package com.virginholidays.backend.test.resource;

import static java.time.LocalTime.parse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.service.FlightInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * The FlightInfoResource unit tests
 *
 * @author Geoff Perks
 */

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FlightInfoResource.class)
class FlightInfoResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FlightInfoService flightInfoService;


    @Test
    public void shouldReturnBadRequestWhenDateNotISOformat() throws Exception {

        mockMvc.perform(get("/20-09-2022/results")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestWhenDateIsNull() throws Exception {

        mockMvc.perform(get("/null/results")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnDataForValidRequest() throws Exception {

        when(flightInfoService.findFlightByDate(any())).thenReturn(CompletableFuture.completedFuture(fligthData()));

        MvcResult mvcResult = this.mockMvc.perform(get("/2022-09-20/results"))
                .andExpect(request().asyncStarted())
                .andReturn();

        this.mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value("3"))
                .andExpect(jsonPath("$.[0].destination").value("Las Vegas"))
                .andExpect(jsonPath("$.[1].destination").value("Antigua"))
                .andExpect(jsonPath("$.[2].destination").value("Dubai"));

    }

    private Optional<List<Flight>> fligthData() {
        var flightList = List.of(new Flight(parse("10:35"), "Las Vegas", "LAS", "VS043", List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)),
                new Flight(parse("11:00"), "Antigua", "ANU", "VS033", List.of(DayOfWeek.TUESDAY)),
                new Flight(parse("19:00"), "Dubai", "DXB", "VS036", List.of(DayOfWeek.TUESDAY))
        );
        return Optional.ofNullable(flightList);
    }
}

