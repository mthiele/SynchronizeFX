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

package de.saxsys.synchronizefx.core.metamodel.propertysynchronizer;

import java.util.UUID;

import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.CommandDistributor;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.PropertyValueMapper;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.Command;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Translates changes to {@link Property}s to {@link Command}s when they occur.
 */
public class PropertyChangeDistributor {

    private final PropertyValueMapper propertyValueMapper;
    private final PropertyRegistry propertyRegistry;
    private final CommandDistributor commandDistributor;
    private final TopologyLayerCallback topology;

    /**
     * Initializes the instance.
     * 
     * @param propertyValueMapper
     *            used to map values of properties that have changed to messages
     * @param propertyRegistry
     *            used to retrieve the id for properties
     * @param commandDistributor
     *            used to notify other peers of a changed property
     * @param topology
     *            used to notify the user of errors
     */
    public PropertyChangeDistributor(final PropertyValueMapper propertyValueMapper,
            final PropertyRegistry propertyRegistry, final CommandDistributor commandDistributor,
            final TopologyLayerCallback topology) {
        this.propertyValueMapper = propertyValueMapper;
        this.propertyRegistry = propertyRegistry;
        this.commandDistributor = commandDistributor;
        this.topology = topology;
    }

    /**
     * Handles a the changing of the value of an {@link Property} on the local domain model.
     * 
     * @param property
     *            The property that has changed
     */
    public void onChange(final Property property) {
        final Optional<UUID> propertyId = propertyRegistry.idFor(property);
        if (!propertyId.isPresent()) {
            topology.onError(new SynchronizeFXException(
                    "A change of the value of an unknown property should be distributed: " + property));
            return;
        }

        final Value propertyValueMessage = propertyValueMapper.map(property.getValue());

        SetPropertyValue command = new SetPropertyValue();
        command.setPropertyId(propertyId.get());
        command.setValue(propertyValueMessage);

        commandDistributor.distribute(command);
    }
}
