package com.virginholidays.backend.test.resource;

import com.virginholidays.backend.test.api.Flight;
import com.virginholidays.backend.test.service.FlightInfoService;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletionStage;
import javax.validation.constraints.NotEmpty;
import static org.springframework.http.CacheControl.noCache;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.springframework.http.ResponseEntity.status;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Geoff Perks
 *
 * The FlightInfoResource
 */
@RestController
public class FlightInfoResource {

    private final FlightInfoService flightInfoService;

    /**
     * The constructor
     *
     * @param flightInfoService the flightInfoService
     */
    public FlightInfoResource(FlightInfoService flightInfoService) {
        this.flightInfoService = flightInfoService;
    }

    /**
     * Resource method for returning flight results
     *
     * @param date the chosen date in ISO 8601 format
     * @return flights for the day of the chosen date
     */
    @RequestMapping(method = RequestMethod.GET, path = "/{date}/results")
    public CompletionStage<ResponseEntity<?>> getResults(
            @PathVariable("date") @NotEmpty @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return flightInfoService.findFlightByDate(date).thenApply(maybeResults -> {

            // no results, no content
            if (maybeResults.isEmpty()) {
                return status(HttpStatus.NO_CONTENT).cacheControl(noCache()).build();
            }

            List<Flight> results = maybeResults.get();

            return status(HttpStatus.OK).cacheControl(noCache()).body(results);
        });
    }
}
