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

package com.github.slimboyfat.mts;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.github.slimboyfat.mts.services.StorageService;
import com.github.slimboyfat.mts.services.TransactionEngine;
import com.github.slimboyfat.mts.services.impl.DeadlockFreeTransactionEngine;
import com.github.slimboyfat.mts.services.impl.InMemoryStorageService;
import com.github.slimboyfat.mts.services.rest.MoneyTransferServiceResource;
import org.glassfish.hk2.api.Factory;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

/**
 * JAX-RS application descriptor.
 *
 * @author slimboyfat
 */
public class JaxRsApplication extends ResourceConfig {
    private final TransactionEngine transactionEngine;
    private final StorageService storageService;

    public JaxRsApplication() {
        this(new DeadlockFreeTransactionEngine(), new InMemoryStorageService());
    }

    public JaxRsApplication(TransactionEngine transactionEngine, StorageService storageService) {
        this.transactionEngine = transactionEngine;
        this.storageService = storageService;
        register(MoneyTransferServiceResource.class);
        register(new JacksonJsonProvider());
        register(new AbstractBinder() {

            @Override
            protected void configure() {
                bindFactory(new StorageServiceFactory()).to(StorageService.class);
                bindFactory(new TransactionEngineFactory()).to(TransactionEngine.class);
            }
        });
    }


    private class StorageServiceFactory implements Factory<StorageService> {

        @Override
        public StorageService provide() {
            return storageService;
        }

        @Override
        public void dispose(StorageService service) {
        }
    }

    private class TransactionEngineFactory implements Factory<TransactionEngine> {
        @Override
        public TransactionEngine provide() {
            return transactionEngine;
        }

        @Override
        public void dispose(TransactionEngine instance) {
        }
    }
}
