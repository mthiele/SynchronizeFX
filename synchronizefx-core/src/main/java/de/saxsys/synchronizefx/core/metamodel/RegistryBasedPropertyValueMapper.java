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

package de.saxsys.synchronizefx.core.metamodel;

import java.util.UUID;

import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Maps commands to {@link PropertyValue} using an {@link ObservableObjectRegistry} to hold the references of
 * <em>observable objects</em>.
 */
class RegistryBasedPropertyValueMapper implements PropertyValueMapper {

    private final ObservableObjectRegistry mapper;
    private final TopologyLayerCallback topology;

    /**
     * Initializes an instance with all it's dependencies.
     * 
     * @param mapper
     *            used to retrieve <em>observable objects</em>
     * @param topology
     *            used to inform the next layer on mapping errors.
     */
    public RegistryBasedPropertyValueMapper(final ObservableObjectRegistry mapper,
            final TopologyLayerCallback topology) {
        this.topology = topology;
        this.mapper = mapper;
    }

    @Override
    public PropertyValue map(final Value message) {
        final UUID valueId = message.getObservableObjectId();
        if (valueId != null) {
            Optional<Object> value = mapper.getById(valueId);
            if (!value.isPresent()) {
                topology.onError(new SynchronizeFXException(
                        "SetPropertyValue command with unknown value object id recived. "
                                + message.getObservableObjectId()));
                return new SimplePropertyValue(null, false);
            }
            return new SimplePropertyValue(value.get(), true);
        } else {
            return new SimplePropertyValue(message.getSimpleObjectValue(), false);
        }
    }
}
