package com.touramigo.search

import com.touramigo.security.SecurityUtils
import com.touramigo.tour.PagedResult
import com.touramigo.tour.SearchResult
import com.touramigo.tour.TourDataService
import com.touramigo.tour.TourOperator
import com.touramigo.tour.TourSearchModel
import com.touramigo.util.LocalDateFormatter
import groovy.transform.PackageScope
import org.apache.commons.lang.ObjectUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mobile.device.Device
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

import java.util.stream.Collectors

@Controller
@RequestMapping(value = '/search-tours')
class SearchController {

    private final TourDataService tourDataService
    private final SearchUtils searchUtils

    @Autowired
    SearchController(TourDataService tourDataService, SearchUtils searchUtils) {
        this.tourDataService = tourDataService
        this.searchUtils = searchUtils
    }

    @RequestMapping(method = RequestMethod.GET)
    String results(TourSearchModel searchModel, @RequestParam('compareTour') Optional<String> compareTourId, Model model, Device device) {
        searchModel.accommodation = searchModel.accommodation ?: null
        searchModel.startCity = searchModel.startCity ?: null
        searchModel.endCity = searchModel.endCity ?: null
        searchModel.moreCategories = searchModel.moreCategories ?: null
        searchModel.groupSize = searchModel.groupSize ?: null
        TourSearchModel realSearchModel = getWithNotListedDestinationsReplaces(searchModel, searchUtils)
        if ('ALL' in realSearchModel.tourOperator) {
            realSearchModel.tourOperator = new ArrayList<>(SecurityUtils.authenticatedUserInfo.assignedSupplierCodes)
        }

        PagedResult<SearchResult> tours
        if (SecurityUtils.authenticatedUserInfo.assignedSupplierCodes) {
            tours = tourDataService.findTours(realSearchModel)
        } else {
            tours = new PagedResult<>()
        }

        model.addAttribute('searchModel', searchModel)
        model.addAttribute('departureMonths', searchUtils.buildListOfDepartureMonths(18))
        model.addAttribute('numberGuests', searchUtils.buildListOfNumberOfGuests(1, 10))
        model.addAttribute('ageRanges', searchUtils.buildListOfAgeRanges())
        model.addAttribute('sortOptions', searchUtils.buildListOfSortOptions())
        model.addAttribute('accommodations', searchUtils.buildListOfAccommodations())
        model.addAttribute('moreCategories', searchUtils.buildListOfMoreCategories())
        model.addAttribute('groupSizes', searchUtils.buildListOfGroupSizes())
        model.addAttribute('destinationKeyWords', searchUtils.buildListOfDestinationKeyWords())
        model.addAttribute('tours', tours)
        model.addAttribute('deviceType', device?.deviceType)
        model.addAttribute('compareTourId', compareTourId.orElse(''))

        List<TourOperator> tourOperators = tourDataService.getAllTourOperators()
        if(!tourOperators) {
            tourOperators = []
        } else {
            // reinit as collection is unmodifiable
            tourOperators = new ArrayList<>(tourOperators.findAll { it.code in SecurityUtils.authenticatedUserInfo.assignedSupplierCodes })
        }
        tourOperators.add(0, new TourOperator(code: 'ALL', name: 'All tour operators'))
        model.addAttribute('tourOperators', tourOperators)
        return 'search/results'
    }

    @ResponseBody
    @RequestMapping(value = '/destinations.json', method = RequestMethod.GET, produces = 'application/json')
    List<SelectOption> destinationSuggestions(@RequestParam(value='destinationFilter', required = false) String destinationFilter) {
        if (destinationFilter == null || destinationFilter.empty) {
            return destinationSuggestions.findAll()
        } else {
            return destinationSuggestions.findFiltered(destinationFilter)
        }
    }

    private void clearCitiesSearch(TourSearchModel searchModel) {
        if (searchModel.startCity == '') {
            searchModel.startCity = null
        }
        if (searchModel.endCity == '') {
            searchModel.endCity = null
        }
    }

    @ResponseBody
    @RequestMapping(value = '/startCities.json', method = RequestMethod.GET, produces = 'application/json')
    List<SelectOption> startCities(TourSearchModel searchModel) {
        clearCitiesSearch(searchModel)
        List<String> startCities = tourDataService.findToursStartCity(searchModel)
        return startCities.stream()
                .map({startCity -> new SelectOption(name: startCity)})
                .collect(Collectors.toList())
    }

    @ResponseBody
    @RequestMapping(value = '/endCities.json', method = RequestMethod.GET, produces = 'application/json')
    List<SelectOption> finishCities(TourSearchModel searchModel) {
        clearCitiesSearch(searchModel)
        List<String> finishCities = tourDataService.findToursFinishCity(searchModel)
        return finishCities.stream()
                .map({finishCity -> new SelectOption(name: finishCity)})
                .collect(Collectors.toList())
    }

    @ResponseBody
    @RequestMapping(value = '/departure', method = RequestMethod.GET, produces = 'application/json')
    List<String> nextDepartures(@RequestParam('tourId') String tourId) {

        return tourDataService.getNextTourDepartures(tourId)
            .stream()
            .map({ tourDeparture -> tourDeparture.startDate.format(LocalDateFormatter.longDateFormatter)})
            .collect(Collectors.toList())
    }

    @ResponseBody
    @RequestMapping(value = '/top', method = RequestMethod.GET, produces = 'application/json')
    List<TopTour> topTours(TourSearchModel searchModel) {
        if (searchModel.whereTo.size() > 0) {
            return tourDataService.findTopToursForDestination(searchModel).collect { new TopTour(it, searchModel.whereTo) }
        } else {
            return []
        }
    }

    @PackageScope
    static class TopTour {
        final String id
        final String sku
        final String title
        final String tourOperatorCode
        final String tourOperatorLogoUrl
        final String primaryImageUrl
        final List<String> countries
        final String startCity
        final String finishCity
        final int duration
        final BigDecimal price
        final String currency
        final String accommodationType
        final String transport
        final String tourPageUrl
        final String tourBookingUrl
        final String tourCompareUrl

        TopTour(SearchResult result, List<String> whereTo) {
            this.id = result.id
            this.sku = result.sku
            this.title = result.title
            this.tourOperatorCode = result.tourOperatorCode
            this.tourOperatorLogoUrl = result.tourOperatorLogoUrl
            this.primaryImageUrl = result.primaryImageUrl
            this.countries = result.countries ?: []
            this.startCity = result.startCity
            this.finishCity = result.finishCity
            this.duration = result.duration
            this.price = result.price
            this.currency = result.currency
            this.accommodationType = result.accommodationType
            this.transport = result.transport

            this.tourPageUrl = "https://touramigo.com/tour/${result.slug}?sku=${result.sku}&org=${result.tourOperatorCode}".trim()
            this.tourBookingUrl = "https://touramigo.com/booking/${result.tourOperatorCode}?sku=${result.sku}".trim()
            def whereToUrl = whereTo.collect { "whereTo=$it" }.join('&')
            this.tourCompareUrl = "https://touramigo.com/search-tours?${whereToUrl}&departureMonth=&compareTour=${result.id}".trim()
        }
    }
}
