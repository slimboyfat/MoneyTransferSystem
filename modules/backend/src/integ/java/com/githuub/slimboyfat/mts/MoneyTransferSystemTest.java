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

package com.githuub.slimboyfat.mts;

import com.github.slimboyfat.mts.JaxRsApplication;
import com.github.slimboyfat.mts.domain.Currency;
import com.github.slimboyfat.mts.domain.Statement;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Integration test.
 *
 * @author slimboyfat
 */
public class MoneyTransferSystemTest extends DeadlockTest {

    @Override
    protected Application configure() {
        return new JaxRsApplication();
    }

    @Test
    public void testRegisterCreatesAccountsForAllCurrencies() {
        List<Statement> response = target("register").path(BOB).request().get(new GenericType<List<Statement>>() {});

        assertEquals(Currency.values().length, response.size());
        for (Statement statement : response) {
            assertEquals(BigDecimal.ZERO, statement.getAccountBalance());
        }
    }

    @Test
    public void testCreditIncreasesBalance() {
        List<Statement> initial = target("register").path(JAY).request().get(new GenericType<List<Statement>>() {});

        Statement balance = target("credit")
                .path(JAY)
                .path(Currency.USD.toString())
                .path(String.valueOf(1000))
                .request()
                .get(Statement.class);

        for (Statement statement : initial) {
            assertEquals(0, statement.getAccountBalance().longValue());
        }
        assertEquals(1000, balance.getAccountBalance().longValue());
    }

    @Test
    public void testDebitDecreasesBalance() {
        target("register").path(JAY).request().get();

        Statement initialBalance = target("credit")
                .path(JAY)
                .path(Currency.USD.toString())
                .path(String.valueOf(1000))
                .request()
                .get(Statement.class);

        Statement balance = target("debit")
                .path(JAY)
                .path(Currency.USD.toString())
                .path(String.valueOf(350))
                .request()
                .get(Statement.class);


        assertEquals(1000, initialBalance.getAccountBalance().longValue());
        assertEquals(650, balance.getAccountBalance().longValue());
    }

    @Test
    public void testTransferMovesAmountBetweenAccounts() {
        target("register").path(JAY).request().get();
        target("register").path(BOB).request().get();
        target("credit").path(JAY).path(Currency.USD.toString()).path(String.valueOf(200)).request().get();
        target("credit").path(BOB).path(Currency.USD.toString()).path(String.valueOf(100)).request().get();

        List<Statement> statement = target("transfer")
                .path(JAY)
                .path(BOB)
                .path(Currency.USD.toString())
                .path(String.valueOf(50))
                .request()
                .get(new GenericType<List<Statement>>() {});


        for (Statement item : statement) {
            switch (item.getUserName()) {
                case BOB:
                case JAY:
                    assertEquals(150, item.getAccountBalance().longValue());
                    break;
                default:
                    fail("Unexpected item");
            }
        }
    }

    @Test(timeout = 20000)
    public void testNoDeadlocks() throws Exception {
        doDeadlockTest();
    }
}
