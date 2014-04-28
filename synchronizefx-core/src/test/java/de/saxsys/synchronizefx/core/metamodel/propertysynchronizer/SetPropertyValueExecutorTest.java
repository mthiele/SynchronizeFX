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
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.SilentObservableChanger;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;
import de.saxsys.synchronizefx.core.metamodel.commands.builders.SetPropertyValueBuilder;
import de.saxsys.synchronizefx.core.metamodel.commands.builders.ValueBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests if {@link SetPropertyValueExecutor} works as expected.
 */
public class SetPropertyValueExecutorTest {

    private static final UUID KNOWN_PROPERTY = UUID.randomUUID();
    private static final UUID UNKNOWN_PROPERTY = UUID.randomUUID();
    private static final Object NEW_VALUE = "new value";

    private final PropertyRegistry propertyRegistry = mock(PropertyRegistry.class);
    private final ObservedValueMapper observedValueMapper = mock(ObservedValueMapper.class);
    private final SilentObservableChanger observableChanger = mock(SilentObservableChanger.class);
    private final TopologyLayerCallback topology = mock(TopologyLayerCallback.class);

    private final SetPropertyValueExecutor commandExecutor = new SetPropertyValueExecutor(propertyRegistry,
            observedValueMapper, observableChanger, topology);

    private final Property propertyToChange = mock(Property.class);
    private final ObservedValue newPropertyValue = mock(ObservedValue.class);
    
    private SetPropertyValue changePropertyValueCommand;

    /**
     * Sets up the test data used in tests.
     */
    @Before
    public void setUpTestData() {
        when(propertyRegistry.getById(KNOWN_PROPERTY)).thenReturn(Optional.of(propertyToChange));

        Value newValue = new ValueBuilder().withSimpleObject(NEW_VALUE).build();
        when(observedValueMapper.map(newValue)).thenReturn(newPropertyValue);

        changePropertyValueCommand = new SetPropertyValueBuilder().propertyId(KNOWN_PROPERTY).value(newValue).build();
    }

    /**
     * The executor should change the value of a {@link Property} according to the contents of the command it executes.
     */
    @Test
    public void shouldChangeTheValueOfAProperty() {
        commandExecutor.apply(changePropertyValueCommand);
        
        ArgumentCaptor<Runnable> changeRunnable = ArgumentCaptor.forClass(Runnable.class);
        verify(observableChanger).silentlyModifyObservable(any(Property.class), changeRunnable.capture());
        changeRunnable.getValue().run();

        verify(propertyToChange).setValue(newPropertyValue);
    }

    /**
     * All changes done to the properties in the users domain model must not be signalized to the change message
     * generator of SynchronizeFX.
     */
    @Test
    public void shouldChangeTheDomainModelOfTheUserWithoutNotifingTheListeners() {
        commandExecutor.apply(changePropertyValueCommand);

        verify(observableChanger).silentlyModifyObservable(any(Property.class), any(Runnable.class));

        // The change should not have been executed when the SilentObservableChanger has not executed it. 
        verifyNoMoreInteractions(propertyToChange);
    }

    /**
     * When a change message with a property id unknown to the property registry is received the class should send an
     * error to the user and should not do any changes to the domain model.
     */
    @Test
    public void shouldFailForMessageWithUnknownPropertyId() {
        when(propertyRegistry.getById(UNKNOWN_PROPERTY)).thenReturn(Optional.<Property> empty());

        commandExecutor.apply(new SetPropertyValueBuilder().propertyId(UNKNOWN_PROPERTY).build());

        verify(topology).onError(any(SynchronizeFXException.class));
    }
}
