package com.touramigo.backpack

import com.touramigo.tour.TourDataService
import com.touramigo.tour.TourDeparturePriceViewModel
import groovy.json.JsonBuilder
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
@CompileStatic
@RequestMapping(value = '/compare-your-tours')
class BackpackController {

    private final TourDataService tourDataService

    @Autowired
    BackpackController(TourDataService tourDataService) {
        this.tourDataService = tourDataService
    }

    static final String VIEW_INDEX = 'backpack/index'
    static final String VIEW_COMPARE_TOURS = 'backpack/compareTours'

    @RequestMapping(method = RequestMethod.GET)
    String index() {
        return VIEW_INDEX
    }

    @RequestMapping(method = RequestMethod.GET, path = '/getTours')
    String compareTours(@RequestParam("comparedTours[]") List<String> comparedToursIds, Model model) {
        if (comparedToursIds.size() > 0) {
            def comparedTours = tourDataService.getTourDetailsByIds(comparedToursIds)
            model.addAttribute('comparedTours', comparedTours)
            model.addAttribute('comparedToursJson', new JsonBuilder(comparedTours))

            Map<String, List<TourDeparturePriceViewModel>> availableDates = [:]
            comparedToursIds.each( { String tourId -> availableDates.put(tourId, tourDataService.getTourDeparturesWithLowestPricePerDate(tourId)) })
            model.addAttribute('availableDates', new JsonBuilder(availableDates))
        } else {
            model.addAttribute('comparedTours', [])
            model.addAttribute('comparedToursJson', [])
            model.addAttribute('availableDates', new JsonBuilder([]))
        }

        return VIEW_COMPARE_TOURS
    }
}
