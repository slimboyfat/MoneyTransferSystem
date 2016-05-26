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

package com.github.slimboyfat.mts.services.impl;


import com.github.slimboyfat.mts.domain.Account;
import com.github.slimboyfat.mts.domain.Currency;
import com.github.slimboyfat.mts.domain.User;
import com.github.slimboyfat.mts.services.TransactionEngine;
import com.github.slimboyfat.mts.domain.Statement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock-based implementation of the Transaction processing engine.
 *
 * Deadlock guard is intentionally extracted into the separate {@link DeadlockFreeTransactionEngine}
 * class to be able to verify quality of the test which intended to detect deadlocks.
 *
 * @author slimboyfat
 */
public class LockBasedTransactionEngine implements TransactionEngine {
    private final ConcurrentMap<Account, Balance> storage = new ConcurrentHashMap<>();

    public BigDecimal getAmount(Account account) {
        return getStatus(account).getBalance();
    }

    @Override
    public List<Statement> register(Collection<Account> accounts) {
        List<Statement> statement = new ArrayList<>();
        for (Account account : accounts) {
            if (storage.putIfAbsent(account, new Balance()) == null) {
                statement.add(statement(account));
            }
        }
        return statement;
    }

    public Statement credit(Account account, BigDecimal amount) {
        BigDecimal balance = getStatus(account).credit(amount);
        return new Statement(account, balance);
    }

    public Statement debit(Account account, BigDecimal amount) {
        BigDecimal balance = getStatus(account).debit(amount);
        return new Statement(account, balance);
    }

    @Override
    public List<Statement> transfer(Account from, Account to, BigDecimal amount) {
        if (from == to) {
            return Arrays.asList(new Statement(from, getStatus(from).getBalance()),
                    new Statement(to, getStatus(to).getBalance()));
        }
        if (from.getCurrency() != to.getCurrency()) {
            throw new IllegalStateException("Both account should have same currency");
        }

        Lock fromLock = getStatus(from).getLock();
        Lock toLock = getStatus(to).getLock();

        return transfer(fromLock, toLock, from, to, amount);
    }

    @Override
    public List<Statement> statement(List<User> users) {
        List<Statement> statement = new ArrayList<>(users.size() * Currency.values().length);
        for (User user : users) {
            for (Account account : user.getAccounts().values()) {
                statement.add(statement(account));
                getStatus(account).getBalance();
            }
        }
        return statement;
    }

    private Statement statement(Account account) {
        return new Statement(account, getStatus(account).getBalance());
    }

    protected List<Statement> transfer(Lock first, Lock second, Account source, Account dest, BigDecimal amount) {
        first.lock();
        try {
            second.lock();
            try {
                BigDecimal sourceBalance = getStatus(source).debit(amount);
                BigDecimal destBalance = getStatus(dest).credit(amount);
                return Arrays.asList(new Statement(source, sourceBalance), new Statement(dest, destBalance));
            } finally {
                second.unlock();
            }
        } finally {
            first.unlock();
        }
    }

    private Balance getStatus(Account account) {
        Balance balance = storage.get(account);
        if (balance == null) {
            throw new IllegalStateException("There is no balance registered for account");
        }
        return balance;
    }

    private static class Balance {
        private final ReentrantLock lock = new ReentrantLock(true);
        private BigDecimal balance = BigDecimal.ZERO;

        public ReentrantLock getLock() {
            return lock;
        }

        public BigDecimal getBalance() {
            lock.lock();
            try {
                return balance;
            } finally {
                lock.unlock();
            }
        }

        public BigDecimal credit(BigDecimal amount) {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount should be a positive number");
            }
            lock.lock();
            try {
                balance = balance.add(amount);
                return balance;
            } finally {
                lock.unlock();
            }
        }

        public BigDecimal debit(BigDecimal amount) {
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("amount should be a positive number");
            }
            lock.lock();
            try {
                if (balance.compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient funds to complete the operation");
                }
                balance = balance.subtract(amount);
                return balance;
            } finally {
                lock.unlock();
            }
        }
    }
}
