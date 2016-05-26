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
import com.github.slimboyfat.mts.services.StorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * In-Memory storage service.
 *
 * @author slimboyfat
 */
public class InMemoryStorageService implements StorageService {
    private final ConcurrentMap<String, User> storage = new ConcurrentHashMap<>();

    @Override
    public void register(User user) {
        User existing = storage.putIfAbsent(user.getName(), user);
        if (existing != null) {
            throw new IllegalStateException("The user has already been registered!");
        }
    }

    @Override
    public User getByName(String name) {
        User user = storage.get(name);
        if (user == null) {
            throw new IllegalStateException("The user hasn't been registered yet!");
        }
        return user;
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(storage.values());
    }
}
