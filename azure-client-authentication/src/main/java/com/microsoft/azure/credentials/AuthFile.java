/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.credentials;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.microsoft.azure.AzureEnvironment;
import com.microsoft.azure.AzureEnvironment.Endpoint;
import com.microsoft.azure.management.apigeneration.Beta;
import com.microsoft.azure.management.apigeneration.Beta.SinceVersion;
import com.microsoft.rest.serializer.JacksonAdapter;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class describes the information from a .azureauth file.
 */
@Beta(SinceVersion.V1_1_0)
public final class AuthFile {

    private String clientId;
    private String tenantId;
    private String clientSecret;
    private byte[] clientCertificate;
    private String clientCertificatePassword;
    private String subscriptionId;

    @JsonIgnore
    private AzureEnvironment environment;
    @JsonIgnore
    private static final JacksonAdapter adapter = new JacksonAdapter();

    private AuthFile() {
        environment = new AzureEnvironment(new HashMap<String, String>());
        environment.endpoints().putAll(AzureEnvironment.AZURE.endpoints());
    }

    public static AuthFile fromCrendetials(ApplicationTokenCredentials credentials) throws IOException {
        String content = adapter.serialize(credentials);
        AuthFile authFile = adapter.deserialize(content, AuthFile.class);
        authFile.environment.endpoints().putAll(credentials.environment().endpoints());
        authFile.tenantId = credentials.domain();
        authFile.subscriptionId = credentials.defaultSubscriptionId();
        return authFile;
    }

    /**
     * Parses an auth file and read into an AuthFile object.
     * @param file the auth file to read
     * @return the AuthFile object created
     * @throws IOException thrown when the auth file or the certificate file cannot be read or parsed
     */
    public static AuthFile parse(File file) throws IOException {
        String content = Files.toString(file, Charsets.UTF_8).trim();
        String certificatePath = null;

        AuthFile authFile;
        if (isJsonBased(content)) {
            authFile = adapter.deserialize(content, AuthFile.class);
            Map<String, String> endpoints = adapter.deserialize(content, new TypeToken<Map<String, String>>() { }.getType());
            authFile.environment.endpoints().putAll(endpoints);
        } else {
            // Set defaults
            Properties authSettings = new Properties();
            authSettings.put(CredentialSettings.AUTH_URL.toString(), AzureEnvironment.AZURE.activeDirectoryEndpoint());
            authSettings.put(CredentialSettings.BASE_URL.toString(), AzureEnvironment.AZURE.resourceManagerEndpoint());
            authSettings.put(CredentialSettings.MANAGEMENT_URI.toString(), AzureEnvironment.AZURE.managementEndpoint());
            authSettings.put(CredentialSettings.GRAPH_URL.toString(), AzureEnvironment.AZURE.graphEndpoint());
            authSettings.put(CredentialSettings.VAULT_SUFFIX.toString(), AzureEnvironment.AZURE.keyVaultDnsSuffix());

            // Load the credentials from the file
            StringReader credentialsReader = new StringReader(content);
            authSettings.load(credentialsReader);
            credentialsReader.close();

            authFile = new AuthFile();
            authFile.clientId = authSettings.getProperty(CredentialSettings.CLIENT_ID.toString());
            authFile.tenantId = authSettings.getProperty(CredentialSettings.TENANT_ID.toString());
            authFile.clientSecret = authSettings.getProperty(CredentialSettings.CLIENT_KEY.toString());
            certificatePath = authSettings.getProperty(CredentialSettings.CLIENT_CERT.toString());
            authFile.clientCertificatePassword = authSettings.getProperty(CredentialSettings.CLIENT_CERT_PASS.toString());
            authFile.subscriptionId = authSettings.getProperty(CredentialSettings.SUBSCRIPTION_ID.toString());
            authFile.environment.endpoints().put(Endpoint.MANAGEMENT.identifier(), authSettings.getProperty(CredentialSettings.MANAGEMENT_URI.toString()));
            authFile.environment.endpoints().put(Endpoint.ACTIVE_DIRECTORY.identifier(), authSettings.getProperty(CredentialSettings.AUTH_URL.toString()));
            authFile.environment.endpoints().put(Endpoint.RESOURCE_MANAGER.identifier(), authSettings.getProperty(CredentialSettings.BASE_URL.toString()));
            authFile.environment.endpoints().put(Endpoint.GRAPH.identifier(), authSettings.getProperty(CredentialSettings.GRAPH_URL.toString()));
            authFile.environment.endpoints().put(Endpoint.KEYVAULT.identifier(), authSettings.getProperty(CredentialSettings.VAULT_SUFFIX.toString()));
        }

        if (certificatePath != null) {
            if (new File(certificatePath).exists()) {
                authFile.clientCertificate = Files.toByteArray(new File(certificatePath));
            } else {
                authFile.clientCertificate = Files.toByteArray(new File(file.getParent(), certificatePath));
            }
        }

        return authFile;
    }

    private static boolean isJsonBased(String content) {
        return content.startsWith("{");
    }

    /**
     * @return an ApplicationTokenCredentials object from the information in this class
     */
    public ApplicationTokenCredentials generateCredentials() {
        if (clientSecret != null) {
            return (ApplicationTokenCredentials) new ApplicationTokenCredentials(
                    clientId,
                    tenantId,
                    clientSecret,
                    environment).withDefaultSubscriptionId(subscriptionId);
        } else if (clientCertificate != null) {
            return (ApplicationTokenCredentials) new ApplicationTokenCredentials(
                    clientId,
                    tenantId,
                    clientCertificate,
                    clientCertificatePassword,
                    environment).withDefaultSubscriptionId(subscriptionId);
        } else {
            throw new IllegalArgumentException("Please specify either a client key or a client certificate.");
        }
    }

    public String generateAuthFileJson() throws IOException {
        Map<String, String> map = adapter.deserialize(adapter.serialize(this),
                new TypeToken<Map<String, String>>() { }.getType());
        map.putAll(environment.endpoints());
        return adapter.serializer().writerWithDefaultPrettyPrinter().writeValueAsString(map);
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
        /** The client certificate for the service principal. */
        CLIENT_CERT("certificate"),
        /** The password for the client certificate for the service principal. */
        CLIENT_CERT_PASS("certificatePassword"),
        /** The management endpoint. */
        MANAGEMENT_URI("managementURI"),
        /** The base URL to the current Azure environment. */
        BASE_URL("baseURL"),
        /** The URL to Active Directory authentication. */
        AUTH_URL("authURL"),
        /** The URL to Active Directory Graph. */
        GRAPH_URL("graphURL"),
        /** The suffix of Key Vaults. */
        VAULT_SUFFIX("vaultSuffix");

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
}