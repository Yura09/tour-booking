package com.touramigo.service.entity.enumeration;

import lombok.Getter;

@Getter
public enum Permission {

    ADMINISTRATE_PARTNERS("administrate-partners"),
    MANAGE_USERS("manage-users"),
    ADMINISTRATE_SUPPLIERS("administrate-suppliers"),
    MANAGE_SUPPLIERS("manage-supplier"),
    ADMINISTRATE_BOOKINGS("administrate-bookings"),
    MANAGE_BOOKINGS("manage-bookings"),
    ADMINISTRATE_GLOBAL("administrate-global");

    private String codeKey;


    Permission(String codeKey) {
        this.codeKey = codeKey;
    }

    public static class Authority {
        public static final String OR = " or ";
        public static final String ADMINISTRATE_PARTNERS = "hasAuthority('administrate-partners')";
        public static final String MANAGE_USERS = "hasAuthority('manage-users')";
        public static final String ADMINISTRATE_SUPPLIERS = "hasAuthority('administrate-suppliers')";
        public static final String MANAGE_SUPPLIERS = "hasAuthority('manage-supplier')";
        public static final String ADMINISTRATE_BOOKINGS = "hasAuthority('administrate-bookings')";
        public static final String MANAGE_BOOKINGS = "hasAuthority('manage-bookings')";
        public static final String ADMINISTRATE_GLOBAL = "hasAuthority('administrate-global')";
    }


}
