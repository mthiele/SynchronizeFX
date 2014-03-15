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

package de.saxsys.synchronizefx.core.metamodel.glue;

import de.saxsys.synchronizefx.core.metamodel.CommandListCreator;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator.State;
import de.saxsys.synchronizefx.core.metamodel.Listeners;
import de.saxsys.synchronizefx.core.metamodel.MetaModel;
import de.saxsys.synchronizefx.core.metamodel.ObservableObjectDistributor;

public class MetaModleBasedObservableObjectDistributor implements ObservableObjectDistributor {

    private CommandListCreator creator;
    private State state;
    private Listeners listeners;
    private final MetaModel metaModel;

    public MetaModleBasedObservableObjectDistributor(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public void distributeNewObservableObject(final Object object) {

        final boolean isUnknown = metaModel.getId(object) == null;

        if (state == null) {
            throw new IllegalStateException("The state was null");
        }
        
        creator.createObservableObject(object, state);
        if (isUnknown) {
            listeners.registerListenersOnEverything(object);
        }
    }

    public void setCreator(final CommandListCreator creator) {
        this.creator = creator;
    }

    public void setListeners(final Listeners listeners) {
        this.listeners = listeners;
    }

    public void setState(final State state) {
        this.state = state;
    }
}
