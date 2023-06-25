package com.touramigo.util

import groovy.transform.CompileStatic
import org.apache.commons.collections.ListUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut

@Aspect
@CompileStatic
class ImmutableCollectionModifierAspect {

    @Pointcut("execution(* com.touramigo.tour.TourDataClient.*(..))")
    void annotatedMethod() {

    }

    @Around('annotatedMethod()')
    Object modifyValue(ProceedingJoinPoint jp) throws Throwable {
        Object result = jp.proceed()
        if(result instanceof List) {
            return ListUtils.unmodifiableList((List) result)
        }
        return result
    }
}
