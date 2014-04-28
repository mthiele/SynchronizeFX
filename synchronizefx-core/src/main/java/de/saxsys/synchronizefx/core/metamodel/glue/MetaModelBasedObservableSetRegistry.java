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

import java.util.UUID;

import javafx.beans.property.SetProperty;

import de.saxsys.synchronizefx.core.metamodel.Listeners;
import de.saxsys.synchronizefx.core.metamodel.MetaModel;
import de.saxsys.synchronizefx.core.metamodel.ObservableSet;
import de.saxsys.synchronizefx.core.metamodel.ObservableSetRegistry;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.javafx.JfxObservableSet;

/**
 * A temporary {@link ObservableSetRegistry} based on the {@link MetaModel}s registry.
 */
public class MetaModelBasedObservableSetRegistry implements ObservableSetRegistry {

    private final MetaModel metaModel;
    private final Listeners listeners;

    /**
     * Initializes an instance with its dependencies.
     * 
     * @param metaModel
     *            The meta model to use to retrieve the {@link ObservableSet}s.
     * @param listeners
     *            A temporary dependency of {@link JfxObservableSet}s
     */
    public MetaModelBasedObservableSetRegistry(final MetaModel metaModel, final Listeners listeners) {
        this.metaModel = metaModel;
        this.listeners = listeners;
    }

    @Override
    public Optional<ObservableSet> getById(final UUID id) {
        final SetProperty<Object> observableSet = (SetProperty<Object>) metaModel.getById(id);
        if (observableSet == null) {
            return Optional.empty();
        }
        return Optional.<ObservableSet> of(new JfxObservableSet(observableSet, listeners));
    }
}
