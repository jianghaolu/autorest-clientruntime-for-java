/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.rest.credentials;

import okhttp3.Authenticator;

/**
 * ServiceClientCredentials is the abstraction for credentials used by
 * ServiceClients accessing REST services.
 */
public abstract class ServiceClientCredentials implements Authenticator {

    private DataStore.Factory dataStoreFactory;

    public ServiceClientCredentials() {
        dataStoreFactory = MemoryDataStoreFactory.INSTANCE;
    }

    public ServiceClientCredentials(final DataStore.Factory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
    }

    public DataStore.Factory dataStoreFactory() {
        return dataStoreFactory;
    }

    public ServiceClientCredentials withDataStoreFactory(DataStore.Factory dataStoreFactory) {
        this.dataStoreFactory = dataStoreFactory;
        return this;
    }
}
