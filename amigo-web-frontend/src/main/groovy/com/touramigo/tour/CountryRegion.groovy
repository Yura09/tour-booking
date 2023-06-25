package com.touramigo.tour

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

@CompileStatic
@Immutable
@ToString(includeFields = true)
class CountryRegion implements Serializable {
    String name
    String alpha2
    String alpha3
    String countryCode
    String iso3166_2
    String region
    String subRegion
    String regionCode
    String subRegionCode
}
