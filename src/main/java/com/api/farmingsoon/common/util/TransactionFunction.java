package com.api.farmingsoon.common.util;

@FunctionalInterface
public interface TransactionFunction<T> {
    T apply();
}