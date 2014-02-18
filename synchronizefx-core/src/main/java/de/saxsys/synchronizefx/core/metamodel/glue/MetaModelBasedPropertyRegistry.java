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
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.javafx.JfxProperty;

/**
 * A temporary {@link PropertyRegistry} implementation that manages registered properties based on a {@link MetaModel}.
 * 
 * <p>
 * This is a temporary implementation while the deprecation of {@link CommandListExecutor} and friends is in progress
 * and to glue the legacy code to the new implementations.
 * </p>
 */
public class MetaModelBasedPropertyRegistry implements PropertyRegistry {
    
    private MetaModel metaModel;

    /**
     * Initializes an instance with all it's dependencies.
     * 
     * @param metaModel the meta model to use.
     */
    public MetaModelBasedPropertyRegistry(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public Optional<Property> getById(final UUID propertyId) {
        javafx.beans.property.Property<Object> property = getJxfPropertyById(propertyId);
        if (property == null) {
            return Optional.empty();
        }
        return Optional.<Property>of(new JfxProperty(property));
    }

    /**
     * Queries the meta model for a JavaFX property with a given id. 
     * 
     * @param propertyId the id to query for.
     * @return the property or <code>null</code> if none is registered for the given id.
     */
    @SuppressWarnings("unchecked")
    private javafx.beans.property.Property<Object> getJxfPropertyById(final UUID propertyId) {
        return (javafx.beans.property.Property<Object>) metaModel.getById(propertyId);
    }
}
