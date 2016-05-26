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

import com.github.slimboyfat.mts.domain.User;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * JUnit's tests for the {@link InMemoryStorageService} class.
 *
 * @author slimboyfat
 */
public class InMemoryStorageServiceTest {
    private User user1 = new User("NAME-1");
    private User user2 = new User("NAME-2");
    private User user3 = new User("NAME-3");

    private InMemoryStorageService fixture;

    @Before
    public void setUp() throws Exception {
        fixture = new InMemoryStorageService();
    }

    @Test(expected = IllegalStateException.class)
    public void testRegisterRejectsDuplicate() throws Exception {

        fixture.register(user1);
        fixture.register(user1);

    }

    @Test
    public void testGetByNameReturnsRegisteredUser() throws Exception {
        fixture.register(user1);

        User actual = fixture.getByName(user1.getName());

        assertEquals(user1, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetByNameThrowsExceptionIfNoUser() throws Exception {

        fixture.getByName(user2.getName());

    }

    @Test
    public void getUsers() throws Exception {
        fixture.register(user1);
        fixture.register(user2);
        fixture.register(user3);

        List<User> users = fixture.getUsers();

        assertTrue(users.size() == 3 && users.containsAll(Arrays.asList(user1, user2, user3)));
    }
}