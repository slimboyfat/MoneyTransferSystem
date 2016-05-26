/*
 * Copyright 2016 slimboyfat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.slimboyfat.mts.domain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a User.
 *
 * Every user has a set of {@link Account accounts} for each {@link Currency currency}.
 *
 * @see Currency
 * @see Account
 * @author slimboyfat
 */
public class User {
    private final String name;
    private final Map<Currency, Account> accounts = new ConcurrentHashMap<>();

    /**
     * Constructs a new instance of User with given {@code name}.
     *
     * @param name user's name
     */
    public User(String name) {
        this.name = name;
        for (Currency currency : Currency.values()) {
            accounts.put(currency, new Account(this, currency));
        }
    }

    /**
     * Returns existing account or creates a new one.
     *
     * @param currency a currency of account
     * @return {@link Account} instance
     */
    public Account getAccount(Currency currency) {
        return accounts.get(currency);
    }

    /**
     * Gets the name of this user.
     *
     * @return the user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a map of registered accounts.
     *
     * @return {@link Map} containing all user's accounts
     */
    public Map<Currency, Account> getAccounts() {
        return accounts;
    }
}
