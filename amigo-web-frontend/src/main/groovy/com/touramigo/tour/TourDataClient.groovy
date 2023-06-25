package com.touramigo.tour

import com.touramigo.booking.TourDeparture
import com.touramigo.info.TermsAndConditions
import org.springframework.cache.annotation.Cacheable
import org.springframework.cloud.netflix.feign.FeignClient
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

import java.time.LocalDate

@Profile("!e2e")
@FeignClient(value = "amigo-tourdata-service")
interface TourDataClient {

    @RequestMapping(method = RequestMethod.GET, value = "/search/countryRegionData", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "countryRegionData", key = "#root.methodName", unless = "#result.isEmpty()")
    List<CountryRegion> getCountryRegionData()

    @RequestMapping(method = RequestMethod.GET, value = "/search/tourCities", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "citiesAssociatedWithTours", key = "#root.methodName", unless = "#result.isEmpty()")
    List<String> getCitiesAssociatedWithTours()

    @RequestMapping(method = RequestMethod.GET, value = "/search/tourThemes",  consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    List<String> getThemesAssociatedWithTours()

    @RequestMapping(value = "/search/tourSearch", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    PagedResult<SearchResult> findTours(@RequestBody TourSearchModel tourSearchModel)

    /**
     * This method only used as fallback to display total count when {@link #findTours} takes too long
     * It returns number of total elements, but empty content list
     *
     * TODO remove this method when {@link #findTours(TourSearchModel)} is optimized in tour-data-service
     */
    @RequestMapping(value = "/search/countTours", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    PagedResult<SearchResult> countTours(@RequestBody TourSearchModel tourSearchModel)

    @RequestMapping(value = "/search/tourSearch/startCity", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    List<String> findToursStartCity(@RequestBody TourSearchModel tourSearchModel)

    @RequestMapping(value = "/search/tourSearch/finishCity", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    List<String> findToursFinishCity(@RequestBody TourSearchModel tourSearchModel)

    @RequestMapping(value = "/tourDetails/getTourDetails", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "tourDetails", unless = "#result.id == null")
    TourDetails getTourDetails(@RequestParam("sku") String sku, @RequestParam("tourOperatorCode") String tourOperatorCode)

    @RequestMapping(value = "/tourDetails/getTourByTourOperator", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    List<TourDetails> findTourOperatorTours(@RequestParam("tourOperatorCode") String tourOperatorCode)

    @RequestMapping(value = "/tourDetails/getTourDetailsByIds", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "tourDetailsByIds", condition = "#root.args[0] != null && #root.args[0].size() <= 10", unless = "#result.isEmpty()")
    List<TourDetails> getTourDetailsByIds(@RequestParam("tourIds[]") List<String> tourIds)

    @RequestMapping(value = "/tourDepartures/getAllDeparturesByStartDate", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "allDeparturesByStartDate", unless = "#result.isEmpty()")
    List<TourDeparture> getAllDeparturesByStartDate(@RequestParam("tourId") String tourId, @RequestParam("startDate") String startDate)

    @RequestMapping(value = "/tourDepartures/getNextDepartures", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "nextTourDepartures", unless = "#result.isEmpty()")
    List<TourDeparture> getNextTourDepartures(@RequestParam("tourId") String tourId)

    @RequestMapping(value = "/tourDepartures/getDetailedDepartures", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "detailedTourDepartures", unless = "#result.isEmpty()")
    List<TourDeparture> getDetailedTourDepartures(@RequestParam("tourId") String tourId)

    @RequestMapping(value = "/tourDepartures/getAllDepartures", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "allTourDepartures", unless = "#result.isEmpty()")
    List<TourDeparture> getAllTourDepartures(@RequestParam("tourId") String tourId)

    @RequestMapping(value = "/tourDepartures/getTourDeparture", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "tourDeparture", unless = "#result.id == null")
    TourDeparture getTourDeparture(@RequestParam("tourId") String tourId, @RequestParam("tourDepartureId") String tourDepartureId)

    @RequestMapping(value = "/tourDepartures/getAllDeparturesStartDates", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "allDeparturesStartDates", unless = "#result.isEmpty()")
    List<LocalDate> getAllDeparturesStartDates(@RequestParam("tourId") String tourId)

    @RequestMapping(value = "/tourOperator/{tourOperatorCode}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "tourOperator", unless = "#result.code == null")
    TourOperator getTourOperator(@PathVariable("tourOperatorCode") String tourOperatorCode)

    @RequestMapping(value = "/tourOperator/", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "allTourOperators", key = "#root.methodName", unless = "#result.isEmpty()")
    List<TourOperator> getAllTourOperators()

    @RequestMapping(value = "/tourOperator/{tourOperatorCode}/terms", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    @Cacheable(value = "tourOperatorTermsAndConditions", unless = "#result.name == null")
    TermsAndConditions getTourOperatorTermsAndConditions(@PathVariable("tourOperatorCode") String tourOperatorCode)

    @RequestMapping(value = "/stats/continent", method = RequestMethod.GET)
    @Cacheable(value = "counterByContinent", key = "#root.methodName", unless = "#result.isEmpty()")
    CounterByContinent counterByContinent(@RequestParam(value = 'tourOperatorCodes[]', required = false) List<String> tourOperatorCodes)

    @RequestMapping(method = RequestMethod.GET, value = "/tourDepartures/findAllByTourId", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "allTourDepartures", unless = "#result.isEmpty()")
    List<TourDeparture> findAllTourDepartures(@RequestParam("tourId") UUID tourId);

    @RequestMapping(value = "/feeds/facebook.xml", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = [MediaType.APPLICATION_JSON_VALUE])
    byte[] getFacebookFeedFile(@RequestBody TourSearchModel tourSearchModel);
}
