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

package de.saxsys.synchronizefx.core.metamodel.setsynchronizer;

import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.ObservableSet;
import de.saxsys.synchronizefx.core.metamodel.ObservableSetRegistry;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.SilentObservableChanger;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.AddToSet;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Executes {@link AddToSet} commands on the users domain model.
 */
public class AddToSetExecutor {

    private final ObservableSetRegistry observableSetRegistry;
    private final ObservedValueMapper observedValueMapper;
    private final SilentObservableChanger observableChanger;
    private final TopologyLayerCallback topology;

    /**
     * Initializes the instance.
     * 
     * @param observableSetRegistry
     *            used to retrieve sets referenced in commands
     * @param observedValueMapper
     *            used to translate {@link Value} messages
     * @param observableChanger
     *            used to add elements to observable sets without notifying other SynchronizeFX classes of this change.
     * @param topology
     *            used to inform the next layer on errors that occurred during command execution.
     */
    public AddToSetExecutor(final ObservableSetRegistry observableSetRegistry,
            final ObservedValueMapper observedValueMapper, final SilentObservableChanger observableChanger,
            final TopologyLayerCallback topology) {
        this.observableSetRegistry = observableSetRegistry;
        this.observedValueMapper = observedValueMapper;
        this.observableChanger = observableChanger;
        this.topology = topology;
    }

    /**
     * Adds an element to the users domain model according to an {@link AddToSet} command.
     * 
     * @param command
     *            The command to execute.
     */
    public void execute(final AddToSet command) {
        final Optional<ObservableSet> set = observableSetRegistry.getById(command.getSetId());
        if (!set.isPresent()) {
            topology.onError(new SynchronizeFXException("A command was received to add an element to the set "
                    + "with the id " + command.getSetId() + " which is unknown."));
            return;
        }
        final ObservedValue value = observedValueMapper.map(command.getValue());
        observableChanger.silentlyModifyObservable(set.get(), new Runnable() {
            @Override
            public void run() {
                set.get().add(value);
            }
        });
    }
}
