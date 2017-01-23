/**
 *
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 *
 */

package com.microsoft.azure.credentials;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.AzureEnvironment;
import org.junit.Assert;
import org.junit.Ignore;

import java.io.IOException;

public class UserTokenCredentialsTests {
    private static MockUserTokenCredentials credentials = new MockUserTokenCredentials(
            "clientId",
            "domain",
            "username",
            "password",
            AzureEnvironment.AZURE
    );

    @Ignore
    public void testAcquireToken() throws Exception {
        Assert.assertEquals("token1", credentials.getToken(null));
        Thread.sleep(1500);
        Assert.assertEquals("token2", credentials.getToken(null));
    }

    public static class MockUserTokenCredentials extends UserTokenCredentials {

        public MockUserTokenCredentials(String clientId, String domain, String username, String password, AzureEnvironment environment) {
            super(clientId, domain, username, password, environment);
        }

        @Override
        public AuthenticationResult authenticate(String resource, AuthenticationResult result) throws IOException {
            return new AuthenticationResult(
                    null,
                    "token1",
                    "refresh",
                    1,
                    null,
                    null,
                    false);
        }
    }
}
