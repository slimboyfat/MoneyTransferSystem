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
import com.github.slimboyfat.mts.domain.Statement;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JUnit's tests for the {@link LockBasedTransactionEngine} class.
 *
 * @author slimboyfat
 */
public class LockBasedTransactionEngineTest {
    private static final BigDecimal HUNDRED = new BigDecimal(100);
    private static final BigDecimal TWENTY = new BigDecimal(20);
    private static final BigDecimal FIVE = new BigDecimal(5);

    private static final User USER = new User("USER");
    private static final Account USD = USER.getAccount(Currency.USD);
    private static final Account EUR = USER.getAccount(Currency.EUR);
    private static final User USER2 = new User("USER2");
    private static final Account USD2 = USER2.getAccount(Currency.USD);

    private LockBasedTransactionEngine fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new LockBasedTransactionEngine();
    }

    @Test(expected = IllegalStateException.class)
    public void getGetAmountThrowsExceptionIfNoAccountWasRegistered() throws Exception {

        BigDecimal amount = fixture.getAmount(USD);

    }

    @Test
    public void getGetAmountReturnsAmountForRegisteredAccount() throws Exception {
        fixture.register(Collections.singletonList(USD));

        BigDecimal amount = fixture.getAmount(USD);

        assertEquals(BigDecimal.ZERO, amount);
    }


    @Test
    public void testRegisterIgnoresDuplicates() throws Exception {

        List<Statement> statement = fixture.register(Arrays.asList(USD, USD));

        assertTrue(statement.size() == 1);
        Statement first = statement.iterator().next();
        assertEquals(USD.getUser().getName(), first.getUserName());
        assertEquals(USD.getNumber(), first.getAccountNumber());
        assertEquals(USD.getCurrency(), first.getAccountCurrency());
        assertEquals(BigDecimal.ZERO, first.getAccountBalance());
    }

    @Test
    public void testCreditAddsAmount() throws Exception {
        List<Statement> statement = fixture.register(Collections.singleton(USD));
        BigDecimal hundred = new BigDecimal(100);

        Statement balance = fixture.credit(USD, hundred);

        assertEquals(BigDecimal.ZERO, statement.iterator().next().getAccountBalance());
        assertEquals(hundred, balance.getAccountBalance());
    }

    @Test
    public void testCreditAddsAmountMultipleTimes() throws Exception {
        List<Statement> statement = fixture.register(Collections.singleton(USD));

        fixture.credit(USD, HUNDRED);
        fixture.credit(USD, TWENTY);
        Statement balance = fixture.credit(USD, FIVE);

        assertEquals(BigDecimal.ZERO, statement.iterator().next().getAccountBalance());
        assertEquals(HUNDRED.add(TWENTY).add(FIVE), balance.getAccountBalance());
    }

    @Test
    public void testDebitThrowsExceptionIfNoSufficientAmount() throws Exception {
        List<Statement> statement = fixture.register(Collections.singleton(USD));
        fixture.credit(USD, TWENTY);

        try {
            fixture.debit(USD, HUNDRED);
        } catch (IllegalStateException e){
            // expected
        }
        BigDecimal balance = fixture.getAmount(USD);

        assertEquals(TWENTY, balance);
    }

    @Test
    public void testDebitSubtractsAmount() throws Exception {
        List<Statement> statement = fixture.register(Collections.singleton(USD));
        fixture.credit(USD, HUNDRED);

        Statement balance = fixture.debit(USD, TWENTY);

        assertEquals(HUNDRED.subtract(TWENTY), balance.getAccountBalance());
    }


    @Test(expected = IllegalStateException.class)
    public void testTransferRejectsAccountWithDifferentCurrencies() throws Exception {
        List<Statement> statement = fixture.register(Arrays.asList(USD, EUR));
        fixture.credit(USD, HUNDRED);
        fixture.credit(EUR, HUNDRED);

        fixture.transfer(USD, EUR, TWENTY);

    }

    @Test
    public void testTransferThrowsExceptionIfNoAvailableAmount() throws Exception {
        fixture.register(Arrays.asList(USD, USD2));
        fixture.credit(USD, TWENTY);

        try {
            fixture.transfer(USD, USD2, HUNDRED);
        } catch (IllegalStateException e) {
            // Insufficient funds is expected
        }

        BigDecimal balance = fixture.getAmount(USD);

        assertEquals(TWENTY, balance);
    }

    @Test
    public void testTransferMovesMoney() throws Exception {
        fixture.register(Arrays.asList(USD, USD2));
        fixture.credit(USD, HUNDRED);
        fixture.credit(USD2, TWENTY);

        List<Statement> statement = fixture.transfer(USD, USD2, FIVE);


        for (Statement item : statement) {
            if (USD.getNumber().equals(item.getAccountNumber())) {
                assertEquals(HUNDRED.subtract(FIVE), item.getAccountBalance());
            } else if (USD2.getNumber().equals(item.getAccountNumber())) {
                assertEquals(TWENTY.add(FIVE), item.getAccountBalance());
            } else {
                fail("Unexpected item");
            }
        }
    }

    @Test
    public void statement() throws Exception {
        fixture.register(USER.getAccounts().values());
        fixture.register(USER2.getAccounts().values());
        fixture.credit(USD, TWENTY);
        fixture.credit(USD2, HUNDRED);
        fixture.transfer(USD, USD2, TWENTY);

        List<Statement> statement = fixture.statement(Arrays.asList(USER, USER2));

        for (Statement item : statement) {
            if (USD.getNumber().equals(item.getAccountNumber())) {
                assertEquals(BigDecimal.ZERO, item.getAccountBalance());
            } else if (USD2.getNumber().equals(item.getAccountNumber())) {
                assertEquals(HUNDRED.add(TWENTY), item.getAccountBalance());
            } else {
                assertEquals(BigDecimal.ZERO, item.getAccountBalance());
            }
        }
    }
}