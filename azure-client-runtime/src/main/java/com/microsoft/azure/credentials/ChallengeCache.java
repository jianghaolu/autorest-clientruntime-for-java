/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.credentials;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.HttpUrl;

/**
 * Handles caching of the challenge.
 */
class ChallengeCache {

    private final HashMap<String, Map<String, String>> cachedChallenges = new HashMap<>();

    /**
     * Uses authority to retrieve the cached values.
     *
     * @param authority
     *            the authority url that is used as a cache key.
     * @return cached value or null if value is not available.
     */
    public Map<String, String> getCachedChallenge(String authority) {
        if (authority == null) {
            return null;
        }
        authority = authority.toLowerCase(Locale.ENGLISH);
        return cachedChallenges.get(authority);
    }

    /**
     * Uses authority to cache challenge.
     *
     * @param authority
     *            the authority url that is used as a cache key.
     * @param challenge
     *            the challenge to cache.
     */
    public void addCachedChallenge(String authority, Map<String, String> challenge) {
        if (authority == null || challenge == null) {
            return;
        }
        authority = authority.toLowerCase(Locale.ENGLISH);
        cachedChallenges.put(authority, challenge);
    }
}
