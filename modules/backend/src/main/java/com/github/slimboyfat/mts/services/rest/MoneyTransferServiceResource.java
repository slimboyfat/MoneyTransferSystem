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

package com.github.slimboyfat.mts.services.rest;

import com.github.slimboyfat.mts.domain.Currency;
import com.github.slimboyfat.mts.domain.User;
import com.github.slimboyfat.mts.services.StorageService;
import com.github.slimboyfat.mts.services.TransactionEngine;
import com.github.slimboyfat.mts.domain.Statement;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.util.List;

/**
 * Money Transfer Service RESTful endpoint.
 *
 * @author slimboyfat
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferServiceResource {
    private final StorageService storageService;
    private final TransactionEngine transactionEngine;

    @Inject public MoneyTransferServiceResource(StorageService storageService, TransactionEngine transactionEngine) {
        this.storageService = storageService;
        this.transactionEngine = transactionEngine;
    }

    @GET
    @Path("register/{name}")
    public List<Statement> register(@PathParam("name") String name) {
        User user = new User(name);
        storageService.register(user);
        return transactionEngine.register(user.getAccounts().values());
    }

    @GET
    @Path("currencies")
    public Currency[] currencies() {
        return Currency.values();
    }

    @GET
    @Path("credit/{to}/{currency}/{amount}")
    public Statement credit(@PathParam("to") String to, @PathParam("currency") Currency currency, @PathParam("amount") BigDecimal amount) {
        return transactionEngine.credit(storageService.getByName(to).getAccount(currency), amount);
    }

    @GET
    @Path("debit/{from}/{currency}/{amount}")
    public Statement debit(@PathParam("from") String from, @PathParam("currency") Currency currency, @PathParam("amount") BigDecimal amount) {
        return transactionEngine.debit(storageService.getByName(from).getAccount(currency), amount);
    }

    @GET
    @Path("transfer/{from}/{to}/{currency}/{amount}")
    public List<Statement> transfer(@PathParam("from") String from, @PathParam("to") String to,
                                    @PathParam("currency") Currency currency, @PathParam("amount") BigDecimal amount) {
        return transactionEngine.transfer(storageService.getByName(from).getAccount(currency),
                storageService.getByName(to).getAccount(currency), amount);
    }

    @GET
    @Path("statement")
    public List<Statement> statement() {
        return transactionEngine.statement(storageService.getUsers());
    }
}
