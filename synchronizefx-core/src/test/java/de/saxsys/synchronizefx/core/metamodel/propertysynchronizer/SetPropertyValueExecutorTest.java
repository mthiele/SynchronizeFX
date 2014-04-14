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
import de.saxsys.synchronizefx.core.metamodel.ModelChangeExecutor;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.ObservedValueMapper;
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyChangeNotificationDisabler;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;
import de.saxsys.synchronizefx.core.metamodel.commands.builders.SetPropertyValueBuilder;
import de.saxsys.synchronizefx.core.metamodel.commands.builders.ValueBuilder;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.*;

/**
 * Tests if {@link SetPropertyValueExecutor} works as expected.
 */
public class SetPropertyValueExecutorTest {

    private static final UUID KNOWN_PROPERTY = UUID.randomUUID();
    private static final UUID UNKNOWN_PROPERTY = UUID.randomUUID();
    private static final Object NEW_VALUE = "new value";

    private final PropertyRegistry propertyRegistry = mock(PropertyRegistry.class);
    private final ObservedValueMapper observedValueMapper = mock(ObservedValueMapper.class);
    private final PropertyChangeNotificationDisabler notificationDisabler
        = mock(PropertyChangeNotificationDisabler.class);
    private final DelayedgModelChangeExecutor modelChangeExecutor = new DelayedgModelChangeExecutor();
    private final TopologyLayerCallback topology = mock(TopologyLayerCallback.class);

    private final SetPropertyValueExecutor commandExecutor = new SetPropertyValueExecutor(propertyRegistry,
            observedValueMapper, notificationDisabler, modelChangeExecutor, topology);

    private Property propertyToChange;
    private ObservedValue newPropertyValue;
    private SetPropertyValue changePropertyValueCommand;

    /**
     * Sets up the test data used in tests.
     */
    @Before
    public void setUpTestData() {
        propertyToChange = mock(Property.class);
        when(propertyRegistry.getById(KNOWN_PROPERTY)).thenReturn(Optional.of(propertyToChange));

        newPropertyValue = mock(ObservedValue.class);
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

        modelChangeExecutor.executeLast();

        verify(propertyToChange).setValue(newPropertyValue);
    }

    /**
     * All changes to the domain model of the user must be done via the {@link ModelChangeExecutor}.
     */
    @Test
    public void shouldModifyThePropertyOnlyViaTheModelChangeExecutor() {
        commandExecutor.apply(changePropertyValueCommand);

        // if the ModelChangeExecutor does not execute the changes, the users domain model should remain unchanged.
        verifyNoMoreInteractions(propertyToChange);
    }

    /**
     * The change to the domain model should not be propagated to the synchronizeFX specific listeners that generate
     * change commands.
     */
    @Test
    public void shouldDisableAndReanableChangeNotificationWhenChangingThePropertyValue() {
        commandExecutor.apply(changePropertyValueCommand);
        modelChangeExecutor.executeLast();

        InOrder inOrder = inOrder(notificationDisabler, propertyToChange);

        inOrder.verify(notificationDisabler).disableFor(propertyToChange);
        inOrder.verify(propertyToChange).setValue(any(ObservedValue.class));
        inOrder.verify(notificationDisabler).enableFor(propertyToChange);
    }

    /**
     * When a change message with a property id unknown to the property registry is recived the class should send an
     * error to the user and should not do any changes to the domain model.
     */
    @Test
    public void shouldFailForMessageWithUnknownPropertyId() {
        when(propertyRegistry.getById(UNKNOWN_PROPERTY)).thenReturn(Optional.<Property> empty());

        commandExecutor.apply(new SetPropertyValueBuilder().propertyId(UNKNOWN_PROPERTY).build());

        verify(topology).onError(any(SynchronizeFXException.class));
    }

    /**
     * Stores the last {@link Runnable} that was passed and executes when required.
     */
    private static class DelayedgModelChangeExecutor implements ModelChangeExecutor {

        private Runnable runnable;

        @Override
        public void execute(final Runnable runnable) {
            this.runnable = runnable;
        }

        /**
         * Executes the last {@link Runnable} that was passed to {@link #execute(Runnable)}.
         */
        public void executeLast() {
            runnable.run();
        }
    }
}
