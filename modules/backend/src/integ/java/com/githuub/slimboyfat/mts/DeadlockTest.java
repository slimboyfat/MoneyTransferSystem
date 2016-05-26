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
import com.github.slimboyfat.mts.services.impl.InMemoryStorageService;
import com.github.slimboyfat.mts.services.impl.LockBasedTransactionEngine;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Application;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * Contains a test which detects possible deadlocks.
 *
 * @author slimboyfat
 */
public class DeadlockTest extends JerseyTest {
    public static final String BOB = "bob";
    public static final String JAY = "jay";

    @Override
    protected Application configure() {
        return new JaxRsApplication(new LockBasedTransactionEngine(), new InMemoryStorageService());
    }

    @Test(timeout = 20000)
    @Ignore("Enable this test in order to check deadlock detection!")
    public void shouldDetectDeadlock() throws Exception {
        doDeadlockTest();
    }

    protected void doDeadlockTest() throws Exception {
        target("register").path(JAY).request().get();
        target("register").path(BOB).request().get();
        target("credit").path(JAY).path(Currency.USD.toString()).path(String.valueOf(1000)).request().get();
        target("credit").path(BOB).path(Currency.USD.toString()).path(String.valueOf(1000)).request().get();

        List<Callable<Void>> tasks = new ArrayList<>(10000);
        for (int i = 0; i < 1000; i++) {
            tasks.add(transfer1UsdFromTo(BOB, JAY));
            tasks.add(transfer1UsdFromTo(JAY, BOB));
        }
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4).invokeAll(tasks, 10000, TimeUnit.MILLISECONDS);

        detectDeadlocks();
    }

    private void detectDeadlocks() {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long[] treads = bean.findDeadlockedThreads();
        if (treads != null) {
            ThreadInfo[] treadInfo = bean.getThreadInfo(treads);

            for (ThreadInfo info : treadInfo) {
                StackTraceElement[] stack = info.getStackTrace();
                fail("Deadlock detected!!!\n" + Arrays.toString(treadInfo));
            }
        }
    }

    private Callable<Void> transfer1UsdFromTo(final String from, final String to) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                target("transfer").path(from).path(to).path(Currency.USD.toString()).path(String.valueOf(1)).request().get();
                return null;
            }
        };
    }

}
