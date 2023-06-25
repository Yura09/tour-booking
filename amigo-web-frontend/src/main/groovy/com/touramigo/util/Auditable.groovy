package com.touramigo.util

import groovy.transform.CompileStatic
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate

import javax.persistence.Column
import javax.persistence.Version
import java.time.LocalDateTime

@CompileStatic
trait Auditable {

    @CreatedDate
    @Column(name = "date_created")
    LocalDateTime dateCreated = LocalDateTime.now()

    @Version
    @Column(name = "date_updated")
    @LastModifiedDate
    LocalDateTime dateUpdated
}
