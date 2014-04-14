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

import java.util.UUID;

import de.saxsys.synchronizefx.core.metamodel.ModelChangeExecutor;
import de.saxsys.synchronizefx.core.metamodel.ObservableSet;
import de.saxsys.synchronizefx.core.metamodel.ObservableSetRegistry;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.commands.AddToSet;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;
import static de.saxsys.synchronizefx.core.metamodel.commands.builders.AddToSetBuilder.addToSetCommand;
import static de.saxsys.synchronizefx.core.metamodel.commands.builders.ValueBuilder.valueMessage;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link AddToSetExecutor} works as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddToSetExecutorTest {

    private static final UUID EXAPMLE_OBSERVABLE_SET_ID = UUID.randomUUID();
    private static final Object EXAMPLE_SIMPLE_VALUE = "example value";

    private static final Value EXAMPLE_VALUE = valueMessage().withSimpleObject(EXAMPLE_SIMPLE_VALUE).build();
    private static final AddToSet EXAMPLE_ADD_TO_SET_COMMAND = addToSetCommand().withSetId(EXAPMLE_OBSERVABLE_SET_ID)
            .withValue(EXAMPLE_VALUE).build();

    @Mock
    private ObservedValueMapper observedValueMapper;

    @Mock
    private ObservableSetRegistry observableSetRegistry;

    @InjectMocks
    private AddToSetExecutor classToTest;

    @Mock
    private ObservableSet exampleObservableSet;
    @Mock
    private ObservedValue exampleValue;

    /**
     * Sets up the mocks to have the expected behavior required for tests.
     */
    @Before
    public void defineBehaviorOfMocks() {
        when(observedValueMapper.map(EXAMPLE_VALUE)).thenReturn(exampleValue);
        when(observableSetRegistry.getById(EXAPMLE_OBSERVABLE_SET_ID)).thenReturn(Optional.of(exampleObservableSet));
    }

    /**
     * The executor should add a value to an {@link ObservedValue} when it receives an {@link AddToSet} message.
     */
    @Test
    public void shouldAddAValueToASet() {
        classToTest.execute(EXAMPLE_ADD_TO_SET_COMMAND);

        verify(exampleObservableSet).add(exampleValue);
    }

    /**
     * All changes to the users domain model must be done via an {@link ModelChangeExecutor}.
     */
    @Test
    @Ignore("not implemented yet")
    public void shouldDoChangesToTheModelOnlyViaTheModelChangeExecutor() {
        fail("not implemented yet");
    }

    /**
     * When no {@link ObservableSet} with the id of an {@link AddToSet} command is registered in the
     * {@link ObservableSetRegistry}, the executor should fail.
     */
    @Test
    @Ignore("not implemented yet")
    public void shouldFailIfSetWithIdWasNotFound() {
        fail("not implemented yet");
    }

    /**
     * To prevent the creation of commands for changes done by this executor, the executor must disable the change
     * notification for an {@link ObservableSet} before doing changes and re-enable it afterwards.
     */
    @Test
    @Ignore("not implemented yet")
    public void shouldDisableAndReanableChangeNotificationWhenAddingToSet() {
        fail("not implemented yet");
    }
}
