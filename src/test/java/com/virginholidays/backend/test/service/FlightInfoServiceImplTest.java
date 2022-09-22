package com.virginholidays.backend.test.service;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.repository.FlightInfoRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.time.LocalTime.parse;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * The FlightInfoServiceImpl unit tests
 *
 * @author Geoff Perks
 */

@ExtendWith(MockitoExtension.class)
class FlightInfoServiceImplTest {

    @Mock
    private FlightInfoRepositoryImpl repository;

    @InjectMocks
    private FlightInfoServiceImpl flightInfoServiceImpl;

    @Test
    public void testFindFlightByDateReturnsDataByDayOfWeek() throws ExecutionException, InterruptedException {
        // prepare
        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(fligthData()));

        // act
        Optional<List<Flight>> maybeFlights = flightInfoServiceImpl
                .findFlightByDate(LocalDate.of(2022, Month.SEPTEMBER, 21))
                .toCompletableFuture()
                .get();

        // assert
        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get().size(), equalTo(1));
        assertThat(maybeFlights.get().get(0).destination(), equalTo("Las Vegas"));
    }

    @Test
    public void testFindFlightByDateReturnsSortedData() throws ExecutionException, InterruptedException {
        // prepare
        when(repository.findAll()).thenReturn(CompletableFuture.completedFuture(fligthData()));

        // act
        Optional<List<Flight>> maybeFlights = flightInfoServiceImpl
                .findFlightByDate(LocalDate.of(2022, Month.SEPTEMBER, 20))
                .toCompletableFuture()
                .get();

        // assert
        assertThat(maybeFlights.isPresent(), equalTo(true));
        assertThat(maybeFlights.get().size(), equalTo(3));
        assertThat(maybeFlights.get().get(0).destination(), equalTo("Antigua"));
        assertThat(maybeFlights.get().get(1).destination(), equalTo("Dubai"));
        assertThat(maybeFlights.get().get(2).destination(), equalTo("Las Vegas"));
    }

    private Optional<List<Flight>> fligthData() {
        var flightList = List.of(new Flight(parse("10:35"), "Las Vegas", "LAS", "VS043", List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY)),
                new Flight(parse("09:00"), "Antigua", "ANU", "VS033", List.of(DayOfWeek.TUESDAY)),
                new Flight(parse("19:00"), "Dubai", "DXB", "VS036", List.of(DayOfWeek.TUESDAY))
        );
        return Optional.ofNullable(flightList);
    }
}