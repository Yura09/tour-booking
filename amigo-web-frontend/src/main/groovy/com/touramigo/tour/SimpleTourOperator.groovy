package com.touramigo.tour;

import groovy.transform.CompileStatic;
import groovy.transform.Immutable;
import groovy.transform.ToString;

import java.math.BigDecimal;

@CompileStatic
@Immutable
@ToString(includeFields = true)
public class SimpleTourOperator {
    String code
    String name
    String logoURL
}
