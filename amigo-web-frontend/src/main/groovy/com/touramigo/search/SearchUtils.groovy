package com.touramigo.search

import com.touramigo.tour.TourDataService
import org.springframework.stereotype.Service

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class SearchUtils {

    private final TourDataService tourDataService;

    SearchUtils(TourDataService tourDataService) {
        this.tourDataService = tourDataService
    }

    HashMap<String, String> buildListOfDepartureMonths(int numberOfMonths) {
        HashMap<String, String> departureMonths = new LinkedHashMap<>()
        departureMonths.put("", "All departure months")

        LocalDate now = LocalDate.now()
        SimpleDateFormat monthAndYear = new SimpleDateFormat("MMMM YYYY")
        SimpleDateFormat code = new SimpleDateFormat("MM-YYYY")

        // build a list of departure months
        for (Integer i = 0; i < numberOfMonths; i++) {
            String monthYearCode = code.format(java.sql.Date.valueOf(now))
            String monthAndYearDesc = monthAndYear.format(java.sql.Date.valueOf(now))

            departureMonths.put(monthYearCode, "Departing in " + monthAndYearDesc)
            now = now.plus(1, ChronoUnit.MONTHS)
        }

        return departureMonths
    }

    HashMap<Integer, String> buildListOfNumberOfGuests(int minGuestNumber, int maxGuestNumber) {
        HashMap<Integer, String> numberOfGuests = new LinkedHashMap<>()
        for (int i = minGuestNumber; i <= maxGuestNumber; i++) {
            if (i == 1) {
                numberOfGuests.put(i, i + " traveller")
            } else {
                numberOfGuests.put(i, i + " travellers")
            }
        }
        return numberOfGuests
    }

    HashMap<String, String> buildListOfAgeRanges() {
        HashMap<String, String> ageRanges = new LinkedHashMap<>()
        ageRanges.put("", "All age ranges")


        return ageRanges
    }

    HashMap<String, String> buildListOfSortOptions() {
        HashMap<String, String> sortBy = new LinkedHashMap<>()
        sortBy.put("", "Select")
        sortBy.put(SortOrder.BEST_DEALS.toString(), "Best Deals")
        sortBy.put(SortOrder.LOWEST_PRICE.toString(), "Lowest price")
        sortBy.put(SortOrder.HIGHEST_PRICE.toString(), "Highest price")
        sortBy.put(SortOrder.SHORTEST_DURATION.toString(), "Shortest Duration")
        sortBy.put(SortOrder.LONGEST_DURATION.toString(), "Longest Duration")
        return sortBy
    }

    HashMap<String, String> buildListOfAccommodations() {
        HashMap<String, String> accommodations = new LinkedHashMap<>()
        accommodations.put("", "Accommodation type")

        return accommodations
    }

    HashMap<String, String> buildListOfMoreCategories() {
        HashMap<String, String> moreCategories = new LinkedHashMap<>()
        moreCategories.put("", "Trip Style")
        List<String> themes = tourDataService.getThemesAssociatedWithTours()
        themes.forEach({ t -> moreCategories.put(t, t) })

        return moreCategories
    }

    HashMap<String, String> buildListOfGroupSizes() {
        HashMap<String, String> groupSizes = new LinkedHashMap<>()
        groupSizes.put("", "All group sizes")

        return groupSizes
    }

    HashMap<String, HashMap<String, String>> buildListOfDestinationKeyWords() {
        HashMap<String, HashMap<String, String>> destinationKeyWords = new LinkedHashMap<>()
        destinationKeyWords.put('europe', new LinkedHashMap<>())
        destinationKeyWords.put('asia', new LinkedHashMap<>())
        destinationKeyWords.put('america', new LinkedHashMap<>())
        destinationKeyWords.put('africa', new LinkedHashMap<>())


        return destinationKeyWords
    }

    Map<String, List<String>> getNotListedDestinations() {
        Map<String, List<String>> destinations = [:]

        destinations.put 'Scotland', ['United Kingdom of Great Britain and Northern Ireland']
        destinations.put 'Wales', ['United Kingdom of Great Britain and Northern Ireland']
        destinations.put 'England', ['United Kingdom of Great Britain and Northern Ireland']
        destinations.put 'UK', ['United Kingdom of Great Britain and Northern Ireland']
        destinations.put 'USA', ['United States of America']
        destinations.put 'Bali', ['Indonesia']

        return destinations
    }
}

enum SortOrder {
    LOWEST_PRICE, HIGHEST_PRICE, SHORTEST_DURATION, LONGEST_DURATION, BEST_DEALS
}