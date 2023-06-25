package com.touramigo.tour


import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includeFields = true)
@AutoClone
@CompileStatic
class TourSearchModel implements Serializable {

    boolean refineSearch

    List<String> whereTo = []

    String departureMonth = ""

    String numberPax = "1"

    String ageRange = ""

    String departDate

    Integer minDays = 1

    String minDaysDisplay = "1 Day"

    Integer maxDays = 99

    String maxDaysDisplay = "99 Days"

    String theme

    List<String> themeCategory = new ArrayList<String>()

    List<String> tourOperator = ["ALL"]

    String accommodation

    BigDecimal priceMin = BigDecimal.ONE

    BigDecimal priceMax = new BigDecimal("100000")

    String currencyCode = "AUD"

    String startCity

    String endCity

    Boolean specifiedDestination

    String groupSize = 0

    String sortBy = ""

    String groupRange

    String moreCategories

    Integer pageNumber = 0 //

    Integer pageSize = 20 //

    String getCurrentQuery() {
        def query = ""
        def tourOperatorsParam = tourOperator.collect { "tourOperator=$it" }.join('&')

        query += getQuery(whereTo, "whereTo") + "sortBy=${sortBy}&departureMonth=${departureMonth}" +
                "&minDaysDisplay=${minDaysDisplay.replace(" ", "+")}&minDays=${minDays}" +
                "&maxDaysDisplay=${maxDaysDisplay.replace(" ", "+")}&maxDays=${maxDays}" +
                "&numberPax=${numberPax}&ageRange=${ageRange}&" +
                getQuery(themeCategory, "themeCategory") +
                "moreCategories=${moreCategories != null ? moreCategories : ""}" +
                "&priceMin=${priceMin}&priceMax=${priceMax}" +
                "&startCity=${startCity != null ? startCity : ""}" +
                "&endCity=${endCity != null ? endCity : ""}&accommodation=${accommodation != null ? accommodation : ""}"
        if (specifiedDestination != null) {
            query += "&specifiedDestination=${specifiedDestination}"
        }
        query += "&${tourOperatorsParam}"
        return query
    }

    private String getQuery(List<String> params, String name) {
        String query = ""
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                query += "&"
            }
            query += name + "=${params[i]}"
        }
        if (params.size() > 0) {
            query += "&"
        }
        return query
    }

}
