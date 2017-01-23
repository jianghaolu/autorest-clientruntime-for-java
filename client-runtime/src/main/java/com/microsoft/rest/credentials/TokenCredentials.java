/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.rest.credentials;

import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.io.IOException;

/**
 * Token based credentials for use with a REST Service Client.
 */
public class TokenCredentials extends ServiceClientCredentials {
    /** The authentication scheme. */
    private String scheme;

    /** The secure token. */
    private String token;

    /**
     * Initializes a new instance of the TokenCredentials.
     *
     * @param scheme scheme to use. If null, defaults to Bearer
     * @param token  valid token
     */
    public TokenCredentials(String scheme, String token) {
        if (scheme == null) {
            scheme = "Bearer";
        }
        this.scheme = scheme;
        this.token = token;
    }

    /**
     * Get the secure token.
     *
     * @return the secure token.
     * @throws IOException exception thrown from token acquisition operations.
     */
    public String getToken(Request request) throws IOException {
        return token;
    }

    @Override
    public final Request authenticate(Route route, Response response) throws IOException {
        return response.request().newBuilder()
            .header("Authorization", scheme + " " + getToken(response.request()))
            .build();
    }
}
