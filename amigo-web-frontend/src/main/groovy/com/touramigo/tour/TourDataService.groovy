package com.touramigo.tour

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty
import com.touramigo.booking.TourDeparture
import com.touramigo.booking.TourDeparturePrice
import com.touramigo.info.TermsAndConditions
import com.touramigo.util.LocalDateFormatter
import groovy.util.logging.Slf4j
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

import java.time.LocalDate
import java.util.stream.Collectors

/**
 * This service delegates manually execution to {@link TourDataClient}
 * and adds {@link HystrixCommand} support - when {@link org.springframework.cloud.netflix.feign.FeignClient}
 * call fails due to e.g. service unavailability then fallback method is being called.
 */
@Slf4j
@Service
class TourDataService implements TourDataClient {

    private final TourDataClient tourDataClient

    @Autowired
    TourDataService(TourDataClient tourDataClient) {
        this.tourDataClient = tourDataClient
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetCountryRegionData")
    List<CountryRegion> getCountryRegionData() {
        return tourDataClient.getCountryRegionData()
    }

    List<CountryRegion> fallbackGetCountryRegionData() {
        log.warn("fallbackGetCountryRegionData() called...")
        return []
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetCitiesAssociatedWithTours")
    List<String> getCitiesAssociatedWithTours() {
        return tourDataClient.getCitiesAssociatedWithTours()
    }

    List<String> fallbackGetCitiesAssociatedWithTours() {
        log.warn("fallbackGetCitiesAssociatedWithTours() called...")
        return []
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetThemesAssociatedWithTours")
    List<String> getThemesAssociatedWithTours() {
        return tourDataClient.getThemesAssociatedWithTours();
    }

    List<String> fallbackGetThemesAssociatedWithTours() {
        log.warn("fallbackGetThemesAssociatedWithTours() called...")
        return []
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackFindTours", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "true")
    ])
    PagedResult<SearchResult> findTours(@RequestBody TourSearchModel tourSearchModel) {
        return tourDataClient.findTours(tourSearchModel)
    }

    @HystrixCommand(fallbackMethod = "fallbackCountTours", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5500"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "true")
    ])
    PagedResult<SearchResult> fallbackFindTours(TourSearchModel tourSearchModel) {
        log.warn("fallbackFindTours(TourSearchModel tourSearchModel) called...")
        return countTours(tourSearchModel)
    }

    /**
     * This method only used as fallback to display total count when {@link #findTours} takes too long
     * It returns number of total elements, but empty content list
     *
     * TODO remove this method when {@link #findTours(TourSearchModel)} is optimized in tour-data-service
     */
    @Override
    @HystrixCommand(fallbackMethod = "fallbackCountTours", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "true")
    ])
    PagedResult<SearchResult> countTours(TourSearchModel tourSearchModel) {

        // returns paged result containing number of total elements, but empty content
        PagedResult<SearchResult> results = tourDataClient.countTours(tourSearchModel)

        // removes Next page button on front-end
        results.last = true;

        return results;
    }

    PagedResult<SearchResult> fallbackCountTours(TourSearchModel tourSearchModel) {
        log.warn("fallbackCountTours(TourSearchModel tourSearchModel) called...")
        return new PagedResult<SearchResult>()
    }

    @HystrixCommand(fallbackMethod = "fallbackFindTourOperatorTours", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000"),
            @HystrixProperty(name = "execution.timeout.enabled", value = "false")
    ])
    List<TourDetails> findTourOperatorTours(String tourOperatorCode) {
        return tourDataClient.findTourOperatorTours(tourOperatorCode)
    }

    List<TourDetails> fallbackFindTourOperatorTours(String tourOperatorCode) {
        log.warn("fallbackFindTourOperatorTours(String tourOperatorCode) called...")
        return new ArrayList<SearchResult>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetTourDetails", commandProperties = [
             @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
    ])
    TourDetails getTourDetails(
            @RequestParam("sku") String sku, @RequestParam("tourOperatorCode") String tourOperatorCode) {
        return tourDataClient.getTourDetails(sku, tourOperatorCode)
    }

    TourDetails fallbackGetTourDetails(String sku, String tourOperatorCode) {
        log.warn("fallbackGetTourDetails({}, {}) called...", sku, tourOperatorCode)
        return new TourDetails([:])
    }

    Map<String, String> tourToMap(TourDetails tour) {
        HashMap<String, String> tourMap = new HashMap<String, String>();
        tourMap.put('name', tour.title)
        tourMap.put('duration', tour.duration)
        tourMap.put('startCity', tour.startCity)
        tourMap.put('finishCity', tour.finishCity)
        tourMap.put('bookingUrl', '../booking/' + tour.operatorCode + '?sku=' + tour.sku)
        return tourMap;
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetTourDetailsByIds")
    List<TourDetails> getTourDetailsByIds(@RequestParam("tourIds[]") List<String> tourIds) {
        return tourDataClient.getTourDetailsByIds(tourIds)
    }

    List<TourDetails> fallbackGetTourDetailsByIds(List<String> tourIds) {
        log.warn("fallbackGetTourDetailsByIds({}) called...", tourIds)
        return new ArrayList<TourDetails>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetTourDeparture")
    TourDeparture getTourDeparture(
            @RequestParam("tourId") String tourId, @RequestParam("tourDepartureId") String tourDepartureId) {
        return tourDataClient.getTourDeparture(tourId, tourDepartureId);
    }

    TourDeparture fallbackGetTourDeparture(String tourId, String tourDepartureId) {
        log.warn("fallbackGetTourDeparture({}, {}) called...", tourId, tourDepartureId)
        return new TourDeparture([:])
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetAllDeparturesByStartDate", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<TourDeparture> getAllDeparturesByStartDate(String tourId, String startDate) {
        return tourDataClient.getAllDeparturesByStartDate(tourId, startDate)
    }

    List<TourDeparture> fallbackGetAllDeparturesByStartDate(String tourId, String startDate) {
        log.warn("fallbackGetAllDeparturesByStartDate({}, {}) called...", tourId, startDate)
        return Collections.emptyList()
    }

    TourDeparture getTourDepartureWithLowestPriceByStartDate(String tourId, String startDate) {
        List<TourDeparture> departures = getAllDeparturesByStartDate(tourId, startDate)
        departures.forEach({TourDeparture tourDeparture ->
            tourDeparture.departurePrices = tourDeparture.departurePrices
                    //.findAll({ dp -> dp.currencyCode?.toLowerCase() == 'aud' })
                    .sort({it.priceType.toLowerCase()})
                    .sort({ it.amount })
        })

        return departures.findAll({td -> CollectionUtils.isNotEmpty(td.departurePrices)})
                .min({td -> td.departurePrices.get(0).amount})
    }

    //@Override
    @HystrixCommand(fallbackMethod = "fallbackGetAllTourDepartures", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<TourDeparture> getAllTourDepartures(String tourId) {
        return tourDataClient.getAllTourDepartures(tourId)
    }

    List<TourDeparture> fallbackGetAllTourDepartures(String tourId) {
        log.warn("fallbackGetAllTourDepartures({}) called...", tourId)
        return new ArrayList<TourDeparture>()
    }

    //@Override
    @HystrixCommand(fallbackMethod = "fallbackGetDetailedTourDepartures", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<TourDeparture> getDetailedTourDepartures(String tourId) {
        return tourDataClient.getDetailedTourDepartures(tourId)
    }

    List<TourDeparture> fallbackGetDetailedTourDepartures(String tourId) {
        log.warn("fallbackGetAllTourDepartures({}) called...", tourId)
        return new ArrayList<TourDeparture>()
    }

    @HystrixCommand(fallbackMethod = "fallbackGetTourDeparturesWithLowestPricePerDate", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
    ])
    List<TourDeparturePriceViewModel> getTourDeparturesWithLowestPricePerDate(String tourId) {
        List<TourDeparture> detailedTourDepartures = this.getDetailedTourDepartures(tourId)
        if (detailedTourDepartures == null) {
            return Collections.emptyList();
        }

        List<TourDeparturePriceViewModel> tourDepartures = detailedTourDepartures.stream()
                .filter({TourDeparture tourDeparture -> tourDeparture.startDate.isAfter(LocalDate.now())})
                .map({ TourDeparture tourDeparture ->
                    TourDeparturePriceViewModel view = new TourDeparturePriceViewModel()

                    view.id = tourDeparture.getId();
                    view.date = tourDeparture.startDate.format(LocalDateFormatter.queryDateFormatter)
                    view.startDate = tourDeparture.startDate.format(LocalDateFormatter.queryDateFormatter)
                    view.dateBookingFormat = tourDeparture.startDate.format(LocalDateFormatter.inputDateFormatter)
                    view.finishDate = tourDeparture.finishDate.format(LocalDateFormatter.queryDateFormatter)
                    view.price = tourDeparture.departurePrices.findAll({ TourDeparturePrice dp -> dp.currencyCode?.toLowerCase() == 'aud' })
                            .min({ TourDeparturePrice dp -> dp.amount })?.amount ?: BigDecimal.ZERO
                    view.promotionPrice = tourDeparture.departurePrices.findAll({ TourDeparturePrice dp -> dp.currencyCode?.toLowerCase() == 'aud' })
                            .min({ TourDeparturePrice dp -> dp.promotionPrice })?.promotionPrice ?: BigDecimal.ZERO
                    view.discount = tourDeparture.departurePrices.findAll({ TourDeparturePrice dp -> dp.currencyCode?.toLowerCase() == 'aud' })
                            .min({ TourDeparturePrice dp -> dp.promotionPrice })?.calculatedDiscount ?: 0
                    view.availability = tourDeparture.totalAvailability
                    view.status = tourDeparture.status
                    view.productCode = tourDeparture.productCode
                    view.minBookingSize = tourDeparture.minBookingSize
                    view.maxBookingSize = tourDeparture.maxBookingSize

                    return view
                })
                .collect(Collectors.toList())

        Map<String, List<TourDeparturePriceViewModel>> tourDeparturesGroupedByDate = tourDepartures.groupBy { td -> td.date}

        List<TourDeparturePriceViewModel> result = new ArrayList<>();
        tourDeparturesGroupedByDate.values().forEach({tourDeparturesByDate ->
            result.add(tourDeparturesByDate.min {td -> td.price})
        })

        return result;
    }

    List<TourDeparturePriceViewModel> fallbackGetTourDeparturesWithLowestPricePerDate(String tourId) {
        log.warn("fallbackGetTourDeparturesWithLowestPricePerDate({}) called...", tourId)
        return Collections.emptyList();
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetAllDeparturesStartDates", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<LocalDate> getAllDeparturesStartDates(String tourId) {
        return tourDataClient.getAllDeparturesStartDates(tourId)
    }

    List<LocalDate> fallbackGetAllDeparturesStartDates(String tourId) {
        log.warn("fallbackGetAllDeparturesStartDates({}) called...", tourId)
        return new ArrayList<LocalDate>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetNextTourDepartures")
    List<TourDeparture> getNextTourDepartures(String tourId) {
        return tourDataClient.getNextTourDepartures(tourId)
    }

    List<TourDeparture> fallbackGetNextTourDepartures(String tourId) {
        log.warn("fallbackGetNextTourDepartures({}) called...", tourId)
        return new ArrayList<TourDeparture>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackFindToursStartCity", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<String> findToursStartCity(@RequestBody TourSearchModel tourSearchModel) {
        return tourDataClient.findToursStartCity(tourSearchModel)
    }

    List<String> fallbackFindToursStartCity(TourSearchModel tourSearchModel) {
        log.warn("fallbackFindToursStartCity(TourSearchModel tourSearchModel) called...")
        return new ArrayList<String>();
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackFindToursFinishCity", commandProperties = [
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "5000")
    ])
    List<String> findToursFinishCity(@RequestBody TourSearchModel tourSearchModel) {
        return tourDataClient.findToursFinishCity(tourSearchModel)
    }

    List<String> fallbackFindToursFinishCity(TourSearchModel tourSearchModel) {
        log.warn("fallbackFindToursFinishCity(TourSearchModel tourSearchModel) called...")
        return new ArrayList<String>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetTourOperator")
    TourOperator getTourOperator(String tourOperatorCode) {
        return tourDataClient.getTourOperator(tourOperatorCode)
    }

    TourOperator fallbackGetTourOperator(String tourOperatorCode) {
        log.warn("fallbackGetTourOperator({}) called...", tourOperatorCode)
        return new TourOperator([:])
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetAllTourOperators")
    List<TourOperator> getAllTourOperators() {
        return tourDataClient.allTourOperators
    }

    List<TourOperator> fallbackGetAllTourOperators() {
        log.warn("fallbackGetAllTourOperators() called...")
        return new ArrayList<TourOperator>()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackGetTourOperatorTermsAndConditions")
    TermsAndConditions getTourOperatorTermsAndConditions(String tourOperatorCode) {
        return tourDataClient.getTourOperatorTermsAndConditions(tourOperatorCode)
    }

    TermsAndConditions fallbackGetTourOperatorTermsAndConditions(String tourOperatorCode) {
        log.warn("fallbackGetTourOperatorTermsAndConditions({}) called...", tourOperatorCode)
        return new TermsAndConditions(code: tourOperatorCode)
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackCounterByContinent")
    CounterByContinent counterByContinent(List<String> tourOperatorCodes) {
        return tourDataClient.counterByContinent(tourOperatorCodes)
    }

    CounterByContinent fallbackCounterByContinent(List<String> tourOperatorCodes) {
        return CounterByContinent.empty()
    }

    @Override
    @HystrixCommand(fallbackMethod = "fallbackFindAllTourDepartures")
    List<TourDeparture> findAllTourDepartures(@RequestParam("tourId") UUID tourId) {
        return tourDataClient.findAllTourDepartures(tourId)
    }

    List<TourDeparture> fallbackFindAllTourDepartures(@RequestParam("tourId") UUID tourId) {
        return []
    }

    @HystrixCommand(fallbackMethod = "fallbackFindTopToursForDestination")
    List<SearchResult> findTopToursForDestination(TourSearchModel tourSearchModel) {
        TourSearchModel searchModel = new TourSearchModel(whereTo: tourSearchModel.whereTo, pageSize: 10, departureMonth: '')
        return findTours(searchModel)?.content
    }

    List<SearchResult> fallbackFindTopToursForDestination(TourSearchModel tourSearchModel) {
        return []
    }

    @Override
    @HystrixCommand(fallbackMethod = "getFacebookFeedFileFallback")
    byte[] getFacebookFeedFile(TourSearchModel tourSearchModel) {
        return tourDataClient.getFacebookFeedFile(tourSearchModel)
    }

    byte[] getFacebookFeedFileFallback(TourSearchModel tourSearchModel) {
        return new byte[0]
    }
}
