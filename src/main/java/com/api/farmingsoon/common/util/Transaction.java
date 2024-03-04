package com.api.farmingsoon.common.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Transaction {
    @Transactional
    public <T> T invoke(TransactionFunction<T> transactionFunction){
        return transactionFunction.apply();
    }
}
