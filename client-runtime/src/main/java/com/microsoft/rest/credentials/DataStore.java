/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.rest.credentials;

import java.io.Serializable;

/**
 * ServiceClientCredentials is the abstraction for credentials used by
 * ServiceClients accessing REST services.
 */
public interface DataStore<T extends Serializable> {
    T get(String key);

    T delete(String key);

    boolean contains(String key);

    DataStore<T> set(String key, T value);

    interface Factory {
        <V extends Serializable> DataStore<V> create();
    }
}
