package com.touramigo.tour

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

@CompileStatic
@Immutable
@ToString(includeNames = true)
class CounterByContinent {
    int antarctica
    int centralAmerica
    int africa
    int asia
    int europe
    int southAmerica
    int northAmerica
    int oceania
    int middleEast
    int arctic

    static CounterByContinent empty() {
        return new CounterByContinent(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
    }

    boolean isEmpty() {
        antarctica == 0 && centralAmerica == 0 && africa == 0 &&
                asia == 0 && southAmerica == 0 && northAmerica == 0 && oceania == 0 && middleEast == 0 && arctic == 0
    }
}
