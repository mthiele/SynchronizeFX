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
import de.saxsys.synchronizefx.core.metamodel.CommandExecutor;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.SilentObservableChanger;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;

/**
 * Executes {@link SetPropertyValue} commands on the users domain model.
 */
public class SetPropertyValueExecutor implements CommandExecutor<SetPropertyValue> {

    private final TopologyLayerCallback topology;
    private final SilentObservableChanger observableChanger;

    private final PropertyRegistry propertyMapper;
    private final ObservedValueMapper observedValueMapper;

    /**
     * Initializes the executor with all it's needed dependencies.
     * 
     * @param propertyMapper
     *            used to map {@link UUID} to {@link Property}s and vice versa.
     * @param observedValueMapper
     *            used to map {@link de.saxsys.synchronizefx.core.metamodel.commands.Value} messages to
     *            {@link ObservedValue} instances
     * @param observableChanger
     *            used to disable change notification for changes done by this class to the users domain model.
     * @param topology
     *            used to inform the next layer of errors.
     */
    public SetPropertyValueExecutor(final PropertyRegistry propertyMapper,
            final ObservedValueMapper observedValueMapper, final SilentObservableChanger observableChanger,
            final TopologyLayerCallback topology) {
        this.propertyMapper = propertyMapper;
        this.observedValueMapper = observedValueMapper;
        this.observableChanger = observableChanger;
        this.topology = topology;
    }

    @Override
    public void apply(final SetPropertyValue command) {
        final Optional<Property> prop = propertyMapper.getById(command.getPropertyId());
        if (!prop.isPresent()) {
            topology.onError(new SynchronizeFXException("SetPropertyValue with unknown property id recived. "
                    + command.getPropertyId()));
            return;
        }

        final ObservedValue value = observedValueMapper.map(command.getValue());

        observableChanger.silentlyModifyObservable(prop.get(), new Runnable() {
            @Override
            public void run() {
                prop.get().setValue(value);
            }
        });
    }
}
