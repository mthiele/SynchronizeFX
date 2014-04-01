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
import de.saxsys.synchronizefx.core.metamodel.PropertyValue;
import de.saxsys.synchronizefx.core.metamodel.PropertyValueMapper;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.Command;
import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Tests if {@link PropertyChangeDistributor} works as expected.
 */
public class PropertyChangeDistributorTest {

    private static final UUID EXAMPLE_UUID = UUID.randomUUID();

    private Property exampleProperty;
    private PropertyValue examplePropertyValue;
    private Value exampleValueMassage;
    
    private TopologyLayerCallback topology;
    private PropertyValueMapper propertyValueMapper;
    private PropertyRegistry propertyRegistry;
    private CommandDistributor commandDistributor;

    private PropertyChangeDistributor changeDistributor;

    /**
     * Sets up the class to test and mocks its dependencies.
     */
    @Before
    public void setUpClassToTestAndDependencies() {
        propertyValueMapper = mock(PropertyValueMapper.class);
        propertyRegistry = mock(PropertyRegistry.class);
        commandDistributor = mock(CommandDistributor.class);
        topology = mock(TopologyLayerCallback.class);

        changeDistributor = new PropertyChangeDistributor(propertyValueMapper, propertyRegistry, commandDistributor,
                topology);
    }

    /**
     * Sets up exemplary data classes used in the test.
     */
    @Before
    public void setUpTestData() {
        examplePropertyValue = mock(PropertyValue.class);
        exampleProperty = mock(Property.class);
        when(exampleProperty.getValue()).thenReturn(examplePropertyValue);
        
        exampleValueMassage = mock(Value.class);
    }

    /**
     * When requested, the {@link PropertyChangeDistributor} should send the current value of a {@link Property} to
     * other peers.
     */
    @Test
    public void shouldDistributeTheValueOfAPropertyToOtherPeers() {
        when(propertyRegistry.idFor(exampleProperty)).thenReturn(Optional.of(EXAMPLE_UUID));
        when(propertyValueMapper.map(examplePropertyValue)).thenReturn(exampleValueMassage);
        
        changeDistributor.onChange(exampleProperty);
        
        verify(commandDistributor).distribute(argThat(containsExampleData()));
    }

    /**
     * When the value of a property which is not registered in the {@link PropertyRegistry} should be distributed to
     * peers, the {@link PropertyChangeDistributor} should report an error to the user.
     */
    @Test
    public void shouldFailIfThePropertyThatsValueShouldBeDistributedIsUnknown() {
        Optional<UUID> empty = Optional.empty();
        when(propertyRegistry.idFor(exampleProperty)).thenReturn(empty);

        changeDistributor.onChange(exampleProperty);

        verify(topology).onError(any(SynchronizeFXException.class));
        verifyNoMoreInteractions(commandDistributor);
    }

    private Matcher<Command> containsExampleData() {
        return new TypeSafeMatcher<Command>(SetPropertyValue.class) {

            @Override
            public void describeTo(final Description description) {
                description.appendText("a SetPropertyCommand with the example test data");
            }

            @Override
            protected boolean matchesSafely(final Command baseCommand) {
                final SetPropertyValue command = (SetPropertyValue) baseCommand;
                return EXAMPLE_UUID.equals(command.getPropertyId()) && exampleValueMassage.equals(command.getValue());
            }
        };
    }
}
