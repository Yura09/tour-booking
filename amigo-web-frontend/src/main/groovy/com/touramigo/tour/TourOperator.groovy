package com.touramigo.tour

import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

@CompileStatic
@Immutable
@ToString(includeFields = true)
class TourOperator implements Serializable {
    String code
    String name
    String description
    String logoURL
    boolean active = true
    BigDecimal minDepositTotal
    BigDecimal minDepositPerPerson
    BigDecimal minDepositTourPercent
    Integer fullPaymentDays
}
