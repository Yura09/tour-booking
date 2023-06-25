package com.touramigo.tour


import groovy.transform.AutoClone
import groovy.transform.CompileStatic
import groovy.transform.ToString

@ToString(includeFields = true)
@AutoClone
@CompileStatic
class TourDeparturePriceViewModel implements Serializable {

    String id;
    String date;
    String startDate;
    String dateBookingFormat;
    String finishDate;
    BigDecimal price;
    BigDecimal promotionPrice;
    Integer discount;
    Integer availability;
    String status;
    String productCode;
    Integer minBookingSize;
    Integer maxBookingSize;
}
