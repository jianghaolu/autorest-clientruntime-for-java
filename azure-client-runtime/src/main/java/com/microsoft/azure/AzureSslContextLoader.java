/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure;

import com.microsoft.rest.protocol.SslContextLoader;
import okhttp3.OkHttpClient;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;

public final class AzureSslContextLoader implements SslContextLoader {
    private SSLContext sslContext;
    private X509TrustManager trustManager;

    public AzureSslContextLoader() {
        this(AzureSslContextLoader.class.getResource("/azurecerts.jks").getPath(), "password");
    }

    public AzureSslContextLoader(final String jksPath, final String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance("jks");
            keyStore.load(
                    new FileInputStream(jksPath),
                    password.toCharArray());
            sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, password.toCharArray());
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException | KeyManagementException | IOException e) {
            throw new IllegalStateException("Failed to apply Azure China and Azure German cloud certificates.");
        }
    }

    @Override
    public void applySslContext(OkHttpClient.Builder clientBuilder) {
        clientBuilder.sslSocketFactory(sslContext.getSocketFactory());
    }
}
