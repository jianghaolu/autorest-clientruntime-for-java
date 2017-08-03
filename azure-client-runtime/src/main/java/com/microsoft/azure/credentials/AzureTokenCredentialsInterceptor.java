/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.credentials;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Token credentials filter for placing a token credential into request headers.
 */
public final class AzureTokenCredentialsInterceptor implements Interceptor {
    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * The credentials instance to apply to the HTTP client pipeline.
     */
    private AzureTokenCredentials credentials;

    /**
     * Initialize a TokenCredentialsFilter class with a
     * TokenCredentials credential.
     *
     * @param credentials a TokenCredentials instance
     */
    AzureTokenCredentialsInterceptor(AzureTokenCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (chain.request().header(AUTHORIZATION_HEADER) == null
                || chain.request().header(AUTHORIZATION_HEADER).isEmpty()) {
            String token = credentials.getToken(chain.request());
            request = chain.request().newBuilder()
                    .header(AUTHORIZATION_HEADER, "Bearer " + token)
                    .build();
        }
        return chain.proceed(request);
    }
}
