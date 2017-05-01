/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.rest.protocol;

import okhttp3.OkHttpClient;

/**
 * An instance of this class loads an SSL context and applies to an OkHttp client builder.
 */
public interface SslContextLoader {
    /**
     * Apply the SSL context to the HTTP client builder.
     *
     * @param clientBuilder the builder for building up an {@link OkHttpClient}
     */
    void applySslContext(OkHttpClient.Builder clientBuilder);
}
