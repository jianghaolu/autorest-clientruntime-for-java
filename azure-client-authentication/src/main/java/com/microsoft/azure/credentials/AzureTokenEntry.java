package com.microsoft.azure.credentials;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.aad.adal4j.AuthenticationResult;

import java.util.Date;

public class AzureTokenEntry {
    @JsonProperty("_authority")
    private String authority;
    @JsonProperty("_clientId")
    private String clientId;
    private String tokenType;
    private long expiresIn;
    private Date expiresOn;
    private String oid;
    private String userId;
    private String servicePrincipalId;
    private String servicePrincipalTenant;
    private boolean isMRRT;
    private String resource;
    private String accessToken;
    private String refreshToken;
    private String identityProvider;


    private AzureTokenEntry() {
    }

    public AzureTokenEntry fromUser(
            String authority,
            String resource,
            String clientId,
            String userId,
            ) {
        AzureTokenEntry entry = new AzureTokenEntry();
        entry.tokenType = "Bearer";
        entry.authority = authority;
        entry.clientId = clientId;
        entry.userId = userId;
        entry.expiresOn = result.getExpiresOnDate();
        entry.expiresIn = result.getExpiresAfter();
        entry.isMRRT = result.isMultipleResourceRefreshToken();
        entry.refreshToken = resource;
        entry.accessToken = result.getAccessToken();
        entry.refreshToken = result.getRefreshToken();
        if (result.getUserInfo() != null) {
            entry.oid = result.getUserInfo().getUniqueId();
            entry.identityProvider = result.getUserInfo().getIdentityProvider();
        }
        return entry;
    }

    public AzureTokenEntry fromServicePrincipal(
            String appId,
            String tenant,
            String accessToken) {
        AzureTokenEntry entry = new AzureTokenEntry();
        entry.tokenType = "Bearer";
        entry.servicePrincipalId = appId;
        entry.servicePrincipalTenant = tenant;
        entry.accessToken = accessToken;
        return entry;
    }
}
