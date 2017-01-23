/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.azure.credentials;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.AzureEnvironment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Token based credentials for use with a REST Service Client.
 */
public class ApplicationTokenCredentials extends AzureTokenCredentials {
    /** The authentication secret for the application. */
    private String secret;
    /** The default subscription to use, if any. */
    private String defaultSubscription;

    /**
     * Initializes a new instance of the UserTokenCredentials.
     *
     * @param clientId the active directory application client id.
     * @param domain the domain or tenant id containing this application.
     * @param secret the authentication secret for the application.
     * @param environment the Azure environment to authenticate with.
     *                    If null is provided, AzureEnvironment.AZURE will be used.
     */
    public ApplicationTokenCredentials(String clientId, String domain, String secret, AzureEnvironment environment) {
        super(clientId, domain, environment);
        this.secret = secret;
    }

    /**
     * Contains the keys of the settings in a Properties file to read credentials from.
     */
    private enum CredentialSettings {
        /** The subscription GUID. */
        SUBSCRIPTION_ID("subscription"),
        /** The tenant GUID or domain. */
        TENANT_ID("tenant"),
        /** The client id for the client application. */
        CLIENT_ID("client"),
        /** The client secret for the service principal. */
        CLIENT_KEY("key"),
        /** The management endpoint. */
        MANAGEMENT_URI("managementURI"),
        /** The base URL to the current Azure environment. */
        BASE_URL("baseURL"),
        /** The URL to Active Directory authentication. */
        AUTH_URL("authURL"),
        /** The URL to Active Directory Graph. */
        GRAPH_URL("graphURL");

        /** The name of the key in the properties file. */
        private final String name;

        CredentialSettings(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    /**
     * @return The default subscription ID, if any
     */
    public String defaultSubscriptionId() {
        return defaultSubscription;
    }

    /**
     * Set default subscription ID.
     *
     * @param subscriptionId the default subscription ID.
     * @return the credentials object itself.
     */
    public ApplicationTokenCredentials withDefaultSubscriptionId(String subscriptionId) {
        this.defaultSubscription = subscriptionId;
        return this;
    }

    /**
     * Initializes the credentials based on the provided credentials file.
     *
     * @param credentialsFile A  file with credentials, using the standard Java properties format.
     * and the following keys:
     *     subscription=&lt;subscription-id&gt;
     *     tenant=&lt;tenant-id&gt;
     *     client=&lt;client-id&gt;
     *     key=&lt;client-key&gt;
     *     managementURI=&lt;management-URI&gt;
     *     baseURL=&lt;base-URL&gt;
     *     authURL=&lt;authentication-URL&gt;
     *
     * @return The credentials based on the file.
     * @throws IOException exception thrown from file access errors.
     */
    public static ApplicationTokenCredentials fromFile(File credentialsFile) throws IOException {
        // Set defaults
        Properties authSettings = new Properties();
        authSettings.put(CredentialSettings.AUTH_URL.toString(), AzureEnvironment.AZURE.authenticationEndpoint());
        authSettings.put(CredentialSettings.BASE_URL.toString(), AzureEnvironment.AZURE.resourceManagerEndpoint());
        authSettings.put(CredentialSettings.MANAGEMENT_URI.toString(), AzureEnvironment.AZURE.managementEndpoint());
        authSettings.put(CredentialSettings.GRAPH_URL.toString(), AzureEnvironment.AZURE.graphEndpoint());

        // Load the credentials from the file
        FileInputStream credentialsFileStream = new FileInputStream(credentialsFile);
        authSettings.load(credentialsFileStream);
        credentialsFileStream.close();

        final String clientId = authSettings.getProperty(CredentialSettings.CLIENT_ID.toString());
        final String tenantId = authSettings.getProperty(CredentialSettings.TENANT_ID.toString());
        final String clientKey = authSettings.getProperty(CredentialSettings.CLIENT_KEY.toString());
        final String mgmtUri = authSettings.getProperty(CredentialSettings.MANAGEMENT_URI.toString());
        final String authUrl = authSettings.getProperty(CredentialSettings.AUTH_URL.toString());
        final String baseUrl = authSettings.getProperty(CredentialSettings.BASE_URL.toString());
        final String graphUrl = authSettings.getProperty(CredentialSettings.GRAPH_URL.toString());
        final String defaultSubscriptionId = authSettings.getProperty(CredentialSettings.SUBSCRIPTION_ID.toString());

        return new ApplicationTokenCredentials(
                clientId,
                tenantId,
                clientKey,
                new AzureEnvironment(
                    authUrl,
                    mgmtUri,
                    baseUrl,
                    graphUrl)
                ).withDefaultSubscriptionId(defaultSubscriptionId);
    }

    /**
     * Gets the active directory application client id.
     *
     * @return the active directory application client id.
     */
    public String clientId() {
        return user();
    }

    /**
     * Gets the authentication secret for the application.
     *
     * @return the authentication secret for the application.
     */
    public String getSecret() {
        return secret;
    }

    @Override
    public AuthenticationResult authenticate(String resource, AuthenticationResult result) throws IOException {
        String authorityUrl = this.environment().authenticationEndpoint() + this.domain();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AuthenticationContext context = new AuthenticationContext(authorityUrl, false, executor);
        try {
            return context.acquireToken(
                resource,
                new ClientCredential(this.clientId(), this.getSecret()),
                null).get();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }
}
