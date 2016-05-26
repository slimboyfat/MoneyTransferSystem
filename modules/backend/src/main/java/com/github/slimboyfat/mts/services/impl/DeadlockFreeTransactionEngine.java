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
import com.github.slimboyfat.mts.domain.Statement;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Deadlock-safe implementation of LockBasedTransactionEngine.
 *
 * @author slimboyfat
 */
public class DeadlockFreeTransactionEngine extends LockBasedTransactionEngine {
    private final Lock unorderedGuard = new ReentrantLock(true);

    @Override
    protected List<Statement> transfer(Lock first, Lock second, Account source, Account dest, BigDecimal amount) {
        switch (Integer.compare(System.identityHashCode(source), System.identityHashCode(dest))) {
            case -1:
                return super.transfer(first, second, source, dest, amount);
            case 0:
                unorderedGuard.lock();
                try {
                    return super.transfer(first, second, source, dest, amount);
                } finally {
                    unorderedGuard.unlock();
                }
            case 1:
                return super.transfer(second, first, source, dest, amount);
            default:
                throw new IllegalStateException();
        }
    }
}
