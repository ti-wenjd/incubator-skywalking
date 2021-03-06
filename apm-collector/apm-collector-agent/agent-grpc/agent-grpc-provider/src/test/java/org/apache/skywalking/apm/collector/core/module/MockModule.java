/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.skywalking.apm.collector.core.module;

import com.google.common.collect.Lists;
import org.apache.skywalking.apm.collector.grpc.manager.service.GRPCManagerService;
import org.apache.skywalking.apm.collector.server.grpc.GRPCServer;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

import java.util.LinkedList;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author lican
 */
public class MockModule extends Module {

    public MockModule() throws ServiceNotProvidedException {
        ModuleProvider moduleProvider = Mockito.mock(ModuleProvider.class);
        LinkedList<ModuleProvider> linkedList = Lists.newLinkedList();
        linkedList.add(moduleProvider);
        Whitebox.setInternalState(this, "loadedProviders", linkedList);
        when(moduleProvider.getService(any())).then(invocation -> {
            Class argumentAt = invocation.getArgumentAt(0, Class.class);
            Object mock = Mockito.mock(argumentAt);
            if (mock instanceof GRPCManagerService) {
                when(((GRPCManagerService) mock).createIfAbsent(anyString(), anyInt())).then(invocation1 -> {
                    GRPCServer grpcServer = new GRPCServer("127.0.0.1", 18098);
                    grpcServer.initialize();
                    return grpcServer;
                });
            }
            return mock;
        });
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Class[] services() {
        return new Class[0];
    }


}
