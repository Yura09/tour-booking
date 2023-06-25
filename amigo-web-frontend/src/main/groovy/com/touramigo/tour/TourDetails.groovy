package com.touramigo.tour

import com.github.slugify.Slugify
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString
import org.springframework.beans.factory.annotation.Value

@Immutable
@ToString(includeFields = true)
class TourDetails implements Serializable {
    String id
    String title
    String description
    String sku
    String operatorCode
    String operatorLogoUrl
    String operatorName
    String operatorDescription
    List<String> imageUrls
    String primaryImageUrl
    BigDecimal price
    BigDecimal promotionPrice
    Integer discount
    BigDecimal startTourPrice
    String startTourPriceCurrencyCode
    Boolean isStartTourPrice
    Integer duration
    List<String> countries
    private List<String> countryCodes
    String startCity
    String finishCity
    String ageRange
    List<String> cities
    List<String> highlights
    List<String> itinerary
    String fitness
    List<String> activitiesIncluded
    List<String> activitiesExtra
    List<String> transport
    List<String> accommodationType
    List<String> accommodationDetail
    List<String> itineraryHeaders
    List<String> itineraryDetails
    List<String> highlightsDetails
    String mealsDetail
    String theme
    Integer maxGroupSize
    String linkHref
    String tourPersonnel
    List<String> departureMonths
    String currency
    Long lastUpdated

    String getSlug() {
        return new Slugify(true).slugify(title)
    }

    List<String> getCountryCodes() {
        Map<String,Locale> locales = new HashMap<String,Locale>()
        for (Locale locale : Locale.getAvailableLocales()) {
            locales.put(locale.getDisplayCountry(), locale)
        }
        List<String> countryCodes = new ArrayList<String>()
        for(String country: this.countries) {
            def locale = locales.get(country)
            if (locale != null) {
                countryCodes.push(locale.getISO3Country())
            }
        }

        return countryCodes
    }

    static final getResizedImageUrl(String url, Integer width, Integer height, Integer multiplier = 1) {
        try {
            if (!url.contains('cloudinary.com')) return url
            // force using JPEG image
            url = url.replaceAll(/\.(gif|jpg|jpeg|tiff|png)$/, ".jpg")
            String[] urlParts = url.split('/')
            if (urlParts.length > 0) {
                String imageVersion = urlParts[urlParts.length - 2]
                String replacement
                if ((width != null) && (height != null)) {
                    replacement = "w_${width * multiplier},h_${height * multiplier},c_fit"
                } else if ((width == null) && (height != null)) {
                    replacement = "h_${height * multiplier},c_fit"
                } else if ((width != null) && (height == null)) {
                    replacement = "w_${width * multiplier},c_fit"
                } else {
                    replacement = "c_fit"
                }
                // force using quality 80 for jpeg images
                return url.replace(imageVersion, "$replacement,q_80")
            }
        } catch (Exception e) {
            return url
        }
    }
}