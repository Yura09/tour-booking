package com.touramigo.tour

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mobile.device.Device
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

import java.util.stream.Collectors

@Controller
@CompileStatic
@RequestMapping(value = '/tour')
class TourDetailsController {

    static final String VIEW_TOUR_DETAILS = 'tour/details'

    private final TourDataService tourDataService

    @Autowired
    TourDetailsController(TourDataService tourDataService) {
        this.tourDataService = tourDataService
    }

    @GetMapping(value = "/{slug}")
    @CompileStatic(TypeCheckingMode.SKIP)
    def details(@RequestParam("org") String operator,
                @RequestParam("sku") String sku,
                Model model, Device device) {
        def assignedSupplierCodes = SecurityUtils.authenticatedUserInfo?.assignedSupplierCodes;
        if (!assignedSupplierCodes?.contains(operator)) {
            return
        }
        TourDetails tour = tourDataService.getTourDetails(sku, operator)
        if(tour?.title == null) {
            return
        }
        def tourMap = tourDataService.tourToMap(tour)

        model.addAttribute('tour', tour)
        def availableDepartures = tourDataService.getTourDeparturesWithLowestPricePerDate(tour.id)
        model.addAttribute('availableDates', availableDepartures)

        TourSearchModel searchModel = new TourSearchModel()
        if (tour.countries != null && tour.countries.size() > 0) {
            searchModel.whereTo.add(tour.countries.get(0))
            searchModel.tourOperator = assignedSupplierCodes;
            List<SearchResult> tours = tourDataService.findTours(searchModel).content
            tours.removeIf( { SearchResult sr -> sr.id.toString() == tour.id })
            model.addAttribute('tours', tours)
        } else {
            model.addAttribute('tours', [])
        }

        model.addAttribute('deviceType', device?.deviceType)
        model.addAttribute('supplier', supplier)
        return VIEW_TOUR_DETAILS
    }

    @ResponseBody
    @RequestMapping(value = '/tourOperator', method = RequestMethod.GET, produces = 'application/json')
    List<SimpleTourOperator> tourOperatorList() {
        def tourOperators = tourDataService.getAllTourOperators()
        def simpleTourOperators = tourOperators
                .stream()
                .filter { TourOperator tour -> tour.active}
                .map { TourOperator tour -> new SimpleTourOperator(tour.code, tour.name, tour.logoURL) }
                .collect(Collectors.toList())
        return simpleTourOperators
    }

    @ResponseBody
    @RequestMapping(value = '/tourOperator/{tourOperatorCode}', method = RequestMethod.GET, produces = 'application/json')
    TourOperator tourOperator(@PathVariable("tourOperatorCode") String tourOperatorCode) {
        return tourDataService.getTourOperator(tourOperatorCode)
    }
}
