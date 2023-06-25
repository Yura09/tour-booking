package com.touramigo.util

import groovy.transform.CompileStatic
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionCallbackWithoutResult
import org.springframework.transaction.support.TransactionSynchronizationAdapter
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.transaction.support.TransactionTemplate

@CompileStatic
abstract class TransactionUtils {

    /**
     * Registers closure
     * @param c
     */
    static void afterTxnCommit(Closure c) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            void afterCommit() {
                c()
            }
        })
    }

    /**
     * Executes closure inside transaction template
     * @param transactionManager
     * @param closure
     */
    static void inTransaction(PlatformTransactionManager transactionManager, Closure closure) {
        new TransactionTemplate(transactionManager).execute({
            closure?.call()
        } as TransactionCallbackWithoutResult)
    }
}