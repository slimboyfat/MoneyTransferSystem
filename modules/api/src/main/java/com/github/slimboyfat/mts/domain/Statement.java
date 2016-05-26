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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Common information on account's owner/currency/balance.
 *
 * @author slimboyfat
 */
public class Statement {
    private final String userName;
    private final String accountNumber;
    private final Currency accountCurrency;
    private final BigDecimal accountBalance;

    @JsonCreator
    public Statement(@JsonProperty("userName") String userName, @JsonProperty("accountNumber") String accountNumber,
                     @JsonProperty("accountCurrency") Currency accountCurrency,
                     @JsonProperty("accountBalance") BigDecimal accountBalance) {
        this.userName = userName;
        this.accountNumber = accountNumber;
        this.accountCurrency = accountCurrency;
        this.accountBalance = accountBalance;
    }

    public Statement(Account account, BigDecimal balance) {
        this.userName = account.getUser().getName();
        this.accountNumber = account.getNumber();
        this.accountCurrency = account.getCurrency();
        this.accountBalance = balance;
    }

    public String getUserName() {
        return userName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public Currency getAccountCurrency() {
        return accountCurrency;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }
}
