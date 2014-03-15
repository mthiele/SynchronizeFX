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

import de.saxsys.synchronizefx.core.metamodel.CommandListExecutor;
import de.saxsys.synchronizefx.core.metamodel.MetaModel;
import de.saxsys.synchronizefx.core.metamodel.ObservableObjectRegistry;
import de.saxsys.synchronizefx.core.metamodel.Optional;

/**
 * Manages <em>observable objects</em> based on the {@link MetaModel}.
 * 
 * <p>
 * This is a temporary glue class while the deprecation of {@link CommandListExecutor} and friends is in progress.
 * </p>
 */
public class MetaModelBasedObservableObjectRegistry implements ObservableObjectRegistry {

    private MetaModel metaModel;

    /**
     * Initializes an instance with all its dependencies.
     * 
     * @param metaModel the meta model used as storage backend.
     */
    public MetaModelBasedObservableObjectRegistry(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public Optional<Object> getById(final UUID valueId) {
        return Optional.ofNullable(metaModel.getById(valueId));
    }

    @Override
    public Optional<UUID> getId(final Object object) {
        return Optional.ofNullable(metaModel.getId(object));
    }
}
