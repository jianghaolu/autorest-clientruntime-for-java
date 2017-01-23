/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.rest.credentials;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceClientCredentials is the abstraction for credentials used by
 * ServiceClients accessing REST services.
 */
public class MemoryDataStoreFactory implements DataStore.Factory {

    public static final MemoryDataStoreFactory INSTANCE = new MemoryDataStoreFactory();

    @Override
    public <V extends Serializable> DataStore<V> create() {
        return new MemoryDataStore<V>();
    }

    private static class MemoryDataStore<T extends Serializable> implements DataStore<T> {
        private ConcurrentHashMap<String, T> map;

        private MemoryDataStore() {
            map = new ConcurrentHashMap<>();
        }

        @Override
        public T get(String key) {
            return map.get(key);
        }

        @Override
        public T delete(String key) {
            return map.remove(key);
        }

        @Override
        public boolean contains(String key) {
            return map.containsKey(key);
        }

        @Override
        public DataStore<T> set(String key, T value) {
            map.put(key, value);
            return this;
        }
    }
}
