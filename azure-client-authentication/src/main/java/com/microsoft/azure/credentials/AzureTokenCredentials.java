/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.azure.credentials;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.rest.credentials.DataStore;
import com.microsoft.rest.credentials.TokenCredentials;
import okhttp3.Request;

import java.io.IOException;
import java.util.Date;

/**
 * AzureTokenCredentials represents a credentials object with access to Azure
 * Resource management.
 */
public abstract class AzureTokenCredentials extends TokenCredentials {
    private final DataStore<AuthenticationResult> dataStore;
    private final String user;
    private final String domain;
    private final AzureEnvironment environment;

    /**
     * Initializes a new instance of the TokenCredentials.
     */
    public AzureTokenCredentials(String user, String domain, AzureEnvironment environment) {
        super("Bearer", null);
        dataStore = dataStoreFactory().create();
        this.user = user;
        this.domain = domain;
        this.environment = (environment == null) ? AzureEnvironment.AZURE : environment;;
    }

    /**
     * Override this method to provide the mechanism to get a token.
     *
     * @param resource the resource the access token is for
     * @return the token to access the resource
     * @throws IOException exceptions from IO
     */
    public abstract AuthenticationResult authenticate(String resource, AuthenticationResult result) throws IOException;

    public String user() {
        return user;
    }

    /**
     * Override this method to provide the domain or tenant ID the token is valid in.
     *
     * @return the domain or tenant ID string
     */
    public String domain() {
        return domain;
    }

    /**
     * @return the environment details the credential has access to.
     */
    public AzureEnvironment environment() {
        return environment;
    }

    @Override
    public final String getToken(Request request) throws IOException {
        String resource = request.url().host();
        String authority = environment().authenticationEndpoint() + domain();
        String id = String.format(user, authority, resource);
        AuthenticationResult result = null;
        synchronized (dataStore) {
            if (dataStore.contains(id)) {
                result = dataStore.get(id);
                if (result.getExpiresOnDate().before(new Date())) {
                    return result.getAccessToken();
                }
            }
            result = authenticate(resource, result);
            dataStore.set(id, result);
        }
        return result.getAccessToken();
    }
}
