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

import com.github.slimboyfat.mts.domain.User;

import java.util.List;

/**
 * Declares methods provided by storage service.
 *
 * @author slimboyfat
 */
public interface StorageService {

    /**
     * Registers new user.
     *
     * @param user user to be registered
     */
    void register(User user);

    /**
     * Locates previously registered user by name.
     *
     * @param name a name of user
     * @return {@link User} instance
     */
    User getByName(String name);

    /**
     * Retrieves a list of registered users.
     *
     * @return a {@link List} of registered users.
     */
    List<User> getUsers();
}
