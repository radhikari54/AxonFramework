/*
 * Copyright (c) 2010-2016. Axon Framework
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

package org.axonframework.commandhandling.model.inspection;

import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.annotation.MessageHandlingMember;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class AnnotatedChildEntity<P, C> implements ChildEntity<P> {
    private final EntityModel<C> entityModel;
    private final boolean forwardEvents;
    private final Map<String, MessageHandlingMember<? super P>> commandHandlers;
    private final BiFunction<EventMessage<?>, P, Iterable<C>> eventTargetResolver;

    @SuppressWarnings("unchecked")
    public AnnotatedChildEntity(EntityModel<C> entityModel, boolean forwardCommands, boolean forwardEvents,
                                BiFunction<CommandMessage<?>, P, C> commandTargetResolver,
                                BiFunction<EventMessage<?>, P, Iterable<C>> eventTargetResolver) {
        this.entityModel = entityModel;
        this.forwardEvents = forwardEvents;
        this.eventTargetResolver = eventTargetResolver;
        this.commandHandlers = new HashMap<>();
        if (forwardCommands) {
            entityModel.commandHandlers().forEach((commandType, childHandler) -> commandHandlers.put(commandType,
                                                                                                     new ChildForwardingCommandMessageHandlingMember<>(
                                                                                                             entityModel
                                                                                                                     .routingKey(),
                                                                                                             childHandler,
                                                                                                             commandTargetResolver)));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void publish(EventMessage<?> msg, P declaringInstance) {
        if (forwardEvents) {
            Iterable<C> targets = eventTargetResolver.apply(msg, declaringInstance);
            if (targets != null) {
                targets.forEach(target -> this.entityModel.publish(msg, target));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, MessageHandlingMember<? super P>> commandHandlers() {
        return commandHandlers;
    }

}
