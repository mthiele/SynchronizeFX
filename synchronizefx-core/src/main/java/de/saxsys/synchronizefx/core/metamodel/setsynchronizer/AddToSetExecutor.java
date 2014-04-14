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

import de.saxsys.synchronizefx.core.metamodel.ObservableSet;
import de.saxsys.synchronizefx.core.metamodel.ObservableSetRegistry;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.commands.AddToSet;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Executes {@link AddToSet} commands on the users domain model.
 */
public class AddToSetExecutor {

    private final ObservableSetRegistry observableSetRegistry;
    private final ObservedValueMapper observedValueMapper;

    /**
     * Initializes the instance.
     * 
     * @param observableSetRegistry
     *            used to retrieve sets referenced in commands
     * @param observedValueMapper
     *            used to translate {@link Value} messages
     */
    public AddToSetExecutor(final ObservableSetRegistry observableSetRegistry,
            final ObservedValueMapper observedValueMapper) {
        this.observableSetRegistry = observableSetRegistry;
        this.observedValueMapper = observedValueMapper;
    }

    /**
     * Adds an element to the users domain model according to an {@link AddToSet} command.
     * 
     * @param command
     *            The command to execute.
     */
    public void execute(final AddToSet command) {
        final ObservableSet set = observableSetRegistry.getById(command.getSetId()).get();
        final ObservedValue value = observedValueMapper.map(command.getValue());
        set.add(value);
    }
}
