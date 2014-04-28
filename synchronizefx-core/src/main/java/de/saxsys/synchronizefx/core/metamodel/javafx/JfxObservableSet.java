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

package de.saxsys.synchronizefx.core.metamodel.javafx;

import javafx.beans.property.SetProperty;

import de.saxsys.synchronizefx.core.metamodel.Listeners;
import de.saxsys.synchronizefx.core.metamodel.ObservableSet;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;

/**
 * Wraps a JavaFX {@link SetProperty} as an SynchronizeFX {@link ObservableSet}.
 */
public class JfxObservableSet implements ObservableSet {

    private final SetProperty<Object> jfxSet;
    // FIXME do not use this
    private final Listeners listeners;

    /**
     * Initializes the instance.
     * 
     * @param jfxSet
     *            The JavaFX set to wrap.
     * @param listeners
     *            A temporary dependency the MetaModel based implementation of the change listener.
     */
    public JfxObservableSet(final SetProperty<Object> jfxSet, final Listeners listeners) {
        this.jfxSet = jfxSet;
        this.listeners = listeners;
    }

    @Override
    public void disableChangeNotification() {
        listeners.disableFor(jfxSet);
    }

    @Override
    public void reEnableChangeNotification() {
        listeners.enableFor(jfxSet);
    }

    @Override
    public void add(final ObservedValue value) {
        jfxSet.add(value.value());
    }
}
