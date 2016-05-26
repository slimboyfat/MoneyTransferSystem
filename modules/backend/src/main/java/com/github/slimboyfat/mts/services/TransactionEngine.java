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

package com.github.slimboyfat.mts.services;

import com.github.slimboyfat.mts.domain.Account;
import com.github.slimboyfat.mts.domain.User;
import com.github.slimboyfat.mts.domain.Statement;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Declares methods provided by transaction engine.
 *
 * @author slimboyfat
 */
public interface TransactionEngine {

    /**
     * Registers provided accounts.
     *
     * @param accounts a collection of accounts to be registered
     * @return {@link Statement} for registered accounts
     */
    List<Statement> register(Collection<Account> accounts);

    /**
     * Adds an {@code amount} to {@code account}.
     *
     * @param account destination account
     * @param amount an income
     * @return {@link Statement} for account
     */
    Statement credit(Account account, BigDecimal amount);

    /**
     * Substracts an {@code amount} from {@code account}.
     *
     * @param account source account
     * @param amount an outcome
     * @return {@link Statement} for account
     */
    Statement debit(Account account, BigDecimal amount);

    /**
     * Transfers an requested {@code amount} from one account to another.
     *
     * @param from source account
     * @param to destination account
     * @param amount how many money should be transferred
     * @return a list of {@link Statement} for accounts
     */
    List<Statement> transfer(Account from, Account to, BigDecimal amount);

    /**
     * Prepares an statement for current amounts.
     *
     * @param users a list of users to build statement for
     * @return a list of {@link Statement)s
     */
    List<Statement> statement(List<User> users);
}
