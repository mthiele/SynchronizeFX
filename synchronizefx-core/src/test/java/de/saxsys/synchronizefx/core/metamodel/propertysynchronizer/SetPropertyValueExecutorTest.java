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
import de.saxsys.synchronizefx.core.metamodel.Optional;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyChangeNotificationDisabler;
import de.saxsys.synchronizefx.core.metamodel.PropertyRegistry;
import de.saxsys.synchronizefx.core.metamodel.PropertyValueMapper;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.builders.SetPropertyValueBuilder;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Tests if {@link SetPropertyValueExecutor} works as expected.
 */
public class SetPropertyValueExecutorTest {
    
    private static final UUID UNKNOWN_PROPERTY = UUID.randomUUID();

    private PropertyRegistry propertyRegistry;
    private PropertyValueMapper propertyValueMapper;
    private PropertyChangeNotificationDisabler notificationDisabler;
    private ModelChangeExecutor changeExecutor;
    private TopologyLayerCallback topology;

    private SetPropertyValueExecutor executor;

    /**
     * Sets up the class under test.
     */
    @Before
    public void setUp() {
        this.propertyRegistry = mock(PropertyRegistry.class);
        this.propertyValueMapper = mock(PropertyValueMapper.class);
        this.notificationDisabler = mock(PropertyChangeNotificationDisabler.class);
        this.changeExecutor = mock(ModelChangeExecutor.class);
        this.topology = mock(TopologyLayerCallback.class);

        this.executor = new SetPropertyValueExecutor(propertyRegistry, propertyValueMapper, notificationDisabler,
                changeExecutor, topology);
    }

    /**
     * When a change message with a property id unknown to the property registry is recived the class should send an
     * error to the user and should not do any changes to the domain model.
     */
    @Test
    public void shouldFailForMessageWithUnknownPropertyId() {
        when(propertyRegistry.getById(UNKNOWN_PROPERTY)).thenReturn(Optional.<Property>empty());
        
        executor.apply(new SetPropertyValueBuilder().propertyId(UNKNOWN_PROPERTY).build());
        
        verify(topology).onError(any(SynchronizeFXException.class));
    }
}
