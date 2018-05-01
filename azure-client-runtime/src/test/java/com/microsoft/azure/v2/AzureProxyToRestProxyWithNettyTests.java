package com.microsoft.azure.v2;

import com.microsoft.rest.v2.http.HttpClient;
import com.microsoft.rest.v2.http.HttpClientConfiguration;
import com.microsoft.rest.v2.http.NettyClient;

public class AzureProxyToRestProxyWithNettyTests extends AzureProxyToRestProxyTests {
    private final NettyClient.Factory nettyClientFactory = new NettyClient.Factory();

    @Override
    protected HttpClient createHttpClient() {
        return nettyClientFactory.create(new HttpClientConfiguration(null));
    }
}