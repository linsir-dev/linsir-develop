package com.linsir.service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface OperationService {

    void set(String key, String value, long timeOut, TimeUnit timeUnit);

    void setString(String key, String value);

    void setString(String key, String value, long offset);

    String getString(String key);

    String getAndSet(String key, String value);

    void appendString(String key, String value);

    Long getRedisStrSize(String key);

    long leftPush(String key, String value);

    Long pushAll(String key, List<String> values);

    long Listsize(String key);

    void setSet(String key, Set<String> value);

    void getSet(String key, Set<String> value);

    void setHash(String key, String value);
}
