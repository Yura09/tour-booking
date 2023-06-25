package com.touramigo.util

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import groovy.transform.AnnotationCollector

@JsonSerialize(using = LocalDateSerializer.class)
@AnnotationCollector
@interface JsonLocalDateSerializeToString {}