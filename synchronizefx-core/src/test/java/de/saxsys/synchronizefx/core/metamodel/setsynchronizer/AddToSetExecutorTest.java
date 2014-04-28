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
import static de.saxsys.synchronizefx.core.metamodel.commands.builders.AddToSetBuilder.addToSetCommand;
import static de.saxsys.synchronizefx.core.metamodel.commands.builders.ValueBuilder.valueMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Checks if {@link AddToSetExecutor} works as expected.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddToSetExecutorTest {

    private static final UUID EXAPMLE_OBSERVABLE_SET_ID = UUID.randomUUID();
    private static final UUID UNKNOWN_OBSERVABLE_SET_ID = UUID.randomUUID();
    private static final Object EXAMPLE_SIMPLE_VALUE = "example value";

    private static final Value EXAMPLE_VALUE = valueMessage().withSimpleObject(EXAMPLE_SIMPLE_VALUE).build();

    @Mock
    private ObservedValueMapper observedValueMapper;

    @Mock
    private ObservableSetRegistry observableSetRegistry;

    @Mock
    private SilentObservableChanger observableChanger;

    @Mock
    private TopologyLayerCallback topology;

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
        classToTest.execute(addToSetCommand().withSetId(EXAPMLE_OBSERVABLE_SET_ID).withValue(EXAMPLE_VALUE).build());

        ArgumentCaptor<Runnable> changeRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(observableChanger).silentlyModifyObservable(any(ObservableSet.class), changeRunnable.capture());
        changeRunnable.getValue().run();

        verify(exampleObservableSet).add(exampleValue);
    }

    /**
     * When no {@link ObservableSet} with the id of an {@link AddToSet} command is registered in the
     * {@link ObservableSetRegistry}, the executor should fail.
     */
    @Test
    public void shouldFailIfSetWithIdWasNotFound() {
        when(observableSetRegistry.getById(UNKNOWN_OBSERVABLE_SET_ID)).thenReturn(Optional.<ObservableSet> empty());

        classToTest.execute(addToSetCommand().withSetId(UNKNOWN_OBSERVABLE_SET_ID).build());

        verify(topology).onError(any(SynchronizeFXException.class));
    }

    /**
     * To prevent the creation of commands for changes done by this executor, the executor must disable the change
     * notification for an {@link ObservableSet} before doing changes and re-enable it afterwards.
     */
    @Test
    public void shouldDisableAndReanableChangeNotificationWhenAddingToSet() {
        classToTest.execute(addToSetCommand().withSetId(EXAPMLE_OBSERVABLE_SET_ID).withValue(EXAMPLE_VALUE).build());

        verify(observableChanger).silentlyModifyObservable(any(ObservableSet.class), any(Runnable.class));

        // the change should not have been executed when the SilentObservableChanger has not executed it.
        verifyNoMoreInteractions(exampleObservableSet);
    }
}
