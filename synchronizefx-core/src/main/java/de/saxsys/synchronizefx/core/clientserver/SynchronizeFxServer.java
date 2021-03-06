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

import java.util.concurrent.Executor;

import javafx.beans.property.Property;

/**
 * This class implements a server that makes a JavaFX model available over the network. All fields of the model that
 * implement the {@link Property} interface will be synchronized between all clients.
 * 
 * @author raik.bieniek
 * 
 */
public class SynchronizeFxServer {

    private DomainModelServer impl;

    /**
     * Sets up everything that is needed to serve a domain model.
     * 
     * <p>
     * This method doesn't start the server. Use {@link SynchronizeFxServer#start()} for that. Using this
     * constructor, all changes done to JavaFX properties will be executed directly in the current thread.
     * </p>
     * 
     * @param model The root object of the domain model to serve.
     * @param networkLayer An object that does the network transfer and optionally the serialization of the data
     *            generated to keep models synchron.
     * @param callback Used to inform the user of this class on errors. The methods in the callback are not called
     *            before you call {@link SynchronizeFxServer#start()}.
     */
    public SynchronizeFxServer(final Object model, final CommandTransferServer networkLayer,
            final ServerCallback callback) {
        impl = new DomainModelServer(model, networkLayer, callback);
    }

    /**
     * Sets up everything that is needed to serve a domain model.
     * 
     * <p>
     * This method doesn't start the server. Use {@link SynchronizeFxServer#start()} for that. Using this
     * constructor, all changes done to JavaFX properties will be executed directly in the current thread.
     * </p>
     * 
     * @param model The root object of the domain model to serve.
     * @param networkLayer An object that does the network transfer and optionally the serialization of the data
     *            generated to keep models synchron.
     * @param callback Used to inform the user of this class on errors. The methods in the callback are not called
     *            before you call {@link SynchronizeFxServer#start()}.
     * @param changeExecutor An executor used to execute all changes done to JavaFX properties.
     */
    public SynchronizeFxServer(final Object model, final CommandTransferServer networkLayer,
            final ServerCallback callback, final Executor changeExecutor) {
        impl = new DomainModelServer(model, networkLayer, callback, changeExecutor);
    }

    /**
     * Starts the server and accepts incoming client connections.
     */
    public void start() {
        impl.start();
    }

    /**
     * Shuts down the server
     * 
     * Before the shutdown, all clients which are still connected are disconnected.
     */
    public void shutdown() {
        impl.shutdown();
    }
}
