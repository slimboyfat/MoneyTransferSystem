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

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

/**
 * Represents an account.
 *
 * Every account is owned by user and has unique number and an associated {@link Currency currency}.
 *
 * @see Currency
 * @author slimboyfat
 */
public class Account {
    private final String number;
    private final Currency currency;
    @JsonIgnore
    private final User user;

    public Account(User user, Currency currency) {
        this.user = user;
        this.number = currency.toString() + '-' + UUID.randomUUID().toString().toUpperCase();
        this.currency = currency;
    }

    public String getNumber() {
        return number;
    }

    public Currency getCurrency() {
        return currency;
    }

    public User getUser() {
        return user;
    }
}
