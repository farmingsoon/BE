package com.api.farmingsoon.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Transaction {
    @Transactional
    public void invoke(TransactionFunction transactionFunction){
        transactionFunction.apply();
    }
}
