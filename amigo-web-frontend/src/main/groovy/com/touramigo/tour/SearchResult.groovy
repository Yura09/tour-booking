package com.touramigo.tour

import com.github.slugify.Slugify
import groovy.transform.Immutable
import groovy.transform.ToString

@ToString(includeFields = true)
@Immutable
class SearchResult implements Serializable {

    UUID id

    String sku

    String title

    String tourOperatorCode;

    String tourOperatorLogoUrl

    String primaryImageUrl

    List<String> countries

    private List<String> countryCodes

    String startCity

    String finishCity

    Integer duration

    BigDecimal price

    Integer discount

    String currency

    String accommodationType

    String transport;

    List<String> itinerary

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

    String getSlug() {
        return new Slugify(true).slugify(title)
    }

    String getUrl() {
        return "https://touramigo.com/tour/" + this.getSlug() + "?sku=" + this.sku + "&org=" + this.tourOperatorCode
    }

    private static final LinkedHashMap<String, LinkedHashMap<String, Integer>> TOUR_IMAGE_SIZES = [
            NORMAL  : [w: 262, h: 276],
            TABLET  : [w: 720, h: 720],
            MOBILE  : [w: 576, h: 576]
    ]

    String getResizedImageUrl(String deviceType, Integer multiplier = 1) {
        try {
            if (!primaryImageUrl.contains('cloudinary.com')) return primaryImageUrl

            String[] urlParts = primaryImageUrl.split('/')
            if (urlParts.length > 0) {
                String imageVersion = urlParts[urlParts.length - 2]
                return primaryImageUrl.replace(imageVersion, "w_${TOUR_IMAGE_SIZES[deviceType].w * multiplier},h_${TOUR_IMAGE_SIZES[deviceType].h * multiplier},c_fill")
            }
        } catch (Exception e) {
            return primaryImageUrl
        }
    }
}
