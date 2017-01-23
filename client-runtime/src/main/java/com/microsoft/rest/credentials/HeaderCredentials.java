/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.rest.credentials;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

import java.io.IOException;

/**
 * Basic Auth credentials for use with a REST Service Client.
 */
public class HeaderCredentials extends ServiceClientCredentials implements Authenticator {
    private String header;
    private String value;

    public HeaderCredentials(String header, String value) {
        this.header = header;
        this.value = value;
    }

    protected String getHeader() {
        return header;
    }

    protected String getValue() {
        return value;
    }

    @Override
    public final Request authenticate(Route route, Response response) throws IOException {
        return response.request().newBuilder().header(getHeader(), getValue()).build();
    }
}
