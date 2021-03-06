/**
 * This file is part of SynchronizeFX.
 * 
 * Copyright (C) 2013-2014 Saxonia Systems AG
 *
 * SynchronizeFX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SynchronizeFX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SynchronizeFX. If not, see <http://www.gnu.org/licenses/>.
 */

package de.saxsys.synchronizefx.core.clientserver;

import java.util.List;

import javafx.beans.property.Property;

import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.commands.Command;

/**
 * <p>
 * This is the interface to the underlying network library that serializes and transfers the {@link Command}s produced
 * by this framework.
 * </p>
 * 
 * <p>
 * All methods defined in here are intended to be used by {@link SynchronizeFxClient} and not by the user.
 * </p>
 * 
 * <p>
 * Any implementation must be capable of transferring all classes in the package
 * {@link de.saxsys.synchronizefx.core.metamodel.commands}, the class {@code java.util.UUID} and all classes of the user
 * domain model that doesn't contain any Fields that extend {@link Property}. You may wan't the user to provide them
 * manually if your implementation hasn't any generic system to handle any {@link Object} that is thrown on it.
 * </p>
 * 
 * <p>
 * It is guaranteed that {@link CommandTransferClient#setTopologyCallback(NetworkToTopologyCallbackClient)} is only
 * called before {@link CommandTransferClient#connect()} and {@link CommandTransferClient#send(List)} only after
 * {@link CommandTransferClient#connect()} (maybe multiple times). It is also guaranteed that no more methods are called
 * after {@link CommandTransferClient#disconnect()} was called.
 * </p>
 * 
 * <p>
 * If you support user supplied serializers, please use the {@link Serializer} interface for them. This way serializers
 * are usable for multiple {@link CommandTransferClient} implementations.
 * </p>
 * 
 * <p>
 * This is the client side. For the server side see {@link CommandTransferServer}.
 * </p>
 * 
 * @author raik.bieniek
 */
public interface CommandTransferClient {

    /**
     * Sets the callback to the topology layer for events like incoming commands or errors.
     * 
     * @param callback
     *            The callback
     */
    void setTopologyCallback(NetworkToTopologyCallbackClient callback);

    /**
     * Open the connection to the server.
     * 
     * @throws SynchronizeFXException
     *             When the connection to the server failed.
     */
    void connect() throws SynchronizeFXException;

    /**
     * Sends commands to the server.
     * 
     * @param commands
     *            The commands to send. The commands don't need to be send all at once but their order must not be
     *            changed.
     */
    void send(List<Command> commands);

    /**
     * Closes the connection to the server.
     */
    void disconnect();
}
