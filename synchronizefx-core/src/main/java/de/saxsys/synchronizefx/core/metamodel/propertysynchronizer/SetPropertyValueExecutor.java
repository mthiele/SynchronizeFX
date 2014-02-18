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
import de.saxsys.synchronizefx.core.metamodel.ModelChangeExecutor;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyChangeNotificationDisabler;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.PropertyValue;
import de.saxsys.synchronizefx.core.metamodel.PropertyValueMapper;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;

/**
 * Executes {@link SetPropertyValue} commands on the users domain model.
 */
public class SetPropertyValueExecutor implements CommandExecutor<SetPropertyValue> {

    private final TopologyLayerCallback topology;
    private final ModelChangeExecutor modelChangeExecutor;
    private final PropertyChangeNotificationDisabler notificationDisabler;

    private final PropertyRegistry propertyMapper;
    private final PropertyValueMapper propertyValueMapper;

    /**
     * Initializes the executor with all it's needed dependencies.
     * 
     * @param propertyMapper
     *            used to map {@link UUID} to {@link Property}s and vice versa.
     * @param propertyValueMapper
     *            used to map {@link de.saxsys.synchronizefx.core.metamodel.commands.Value} messages to
     *            {@link PropertyValue} instances
     * @param topology
     *            used to inform the next layer of errors.
     * @param notificationDisabler
     *            used to temporary disable change notification for {@link Property}s this executor is changing.
     * @param executor
     *            used to execute changes on the domain model of the user.
     */
    public SetPropertyValueExecutor(final PropertyRegistry propertyMapper,
            final PropertyValueMapper propertyValueMapper,
            final PropertyChangeNotificationDisabler notificationDisabler, final ModelChangeExecutor executor,
            final TopologyLayerCallback topology) {
        this.propertyMapper = propertyMapper;
        this.propertyValueMapper = propertyValueMapper;
        this.topology = topology;
        this.notificationDisabler = notificationDisabler;
        this.modelChangeExecutor = executor;
    }

    @Override
    public void apply(final SetPropertyValue command) {
        final Optional<Property> prop = propertyMapper.getById(command.getPropertyId());
        if (!prop.isPresent()) {
            topology.onError(new SynchronizeFXException("SetPropertyValue with unknown property id recived. "
                    + command.getPropertyId()));
            return;
        }

        PropertyValue value = propertyValueMapper.map(command.getValue());

        modelChangeExecutor.execute(new ChangePropertyValueRunnable(prop.get(), value));
    }

    /**
     * Ensures that he change notifier do not fire while a change to a property is done.
     */
    private class ChangePropertyValueRunnable implements Runnable {

        private Property prop;
        private PropertyValue value;

        ChangePropertyValueRunnable(final Property prop, final PropertyValue value) {
            this.prop = prop;
            this.value = value;
        }

        @Override
        public void run() {
            notificationDisabler.disableFor(prop);
            prop.setValue(value);
            notificationDisabler.enableFor(prop);
        }
    }
}
