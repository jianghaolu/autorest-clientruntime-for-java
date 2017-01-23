/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.azure.credentials;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.AzureEnvironment;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Token based credentials for use with a REST Service Client.
 */
public class UserTokenCredentials extends AzureTokenCredentials {
    /** The Active Directory application client id. */
    private String clientId;
    /** The password for the Organization Id account. */
    private String password;

    /**
     * Initializes a new instance of the UserTokenCredentials.
     *
     * @param clientId the active directory application client id.
     * @param domain the domain or tenant id containing this application.
     * @param username the user name for the Organization Id account.
     * @param password the password for the Organization Id account.
     * @param environment the Azure environment to authenticate with.
     *                    If null is provided, AzureEnvironment.AZURE will be used.
     */
    public UserTokenCredentials(String clientId, String domain, String username, String password, AzureEnvironment environment) {
        super(username, domain, environment); // defer token acquisition
        this.clientId = clientId;
        this.password = password;
    }

    /**
     * Gets the active directory application client id.
     *
     * @return the active directory application client id.
     */
    public String clientId() {
        return clientId;
    }

    /**
     * Gets the user name for the Organization Id account.
     *
     * @return the user name.
     */
    public String username() {
        return user();
    }

    /**
     * Gets the password for the Organization Id account.
     *
     * @return the password.
     */
    public String password() {
        return password;
    }

    @Override
    public AuthenticationResult authenticate(String resource, AuthenticationResult result) throws IOException {
        if (result != null) {
            result = acquireAccessTokenFromRefreshToken(resource, result);
            if (result != null) {
                return result;
            }
        }

        String authorityUrl = this.environment().authenticationEndpoint() + this.domain();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AuthenticationContext context = new AuthenticationContext(authorityUrl, false, executor);
        try {
            return context.acquireToken(
                    resource,
                    this.clientId(),
                    this.username(),
                    this.password(),
                    null).get();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }

    // Refresh tokens are currently not used since we don't know if the refresh token has expired
    private AuthenticationResult acquireAccessTokenFromRefreshToken(String resource, AuthenticationResult result) throws IOException {
        String authorityUrl = this.environment().authenticationEndpoint() + this.domain();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        AuthenticationContext context = new AuthenticationContext(authorityUrl, false, executor);
        try {
            return context.acquireTokenByRefreshToken(
                    result.getRefreshToken(),
                    this.clientId(),
                    null, null).get();
        } catch (Exception e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            executor.shutdown();
        }
    }
}
