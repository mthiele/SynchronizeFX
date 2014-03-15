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

package de.saxsys.synchronizefx.core.metamodel.javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;

import de.saxsys.synchronizefx.core.metamodel.CommandListCreator;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator.State;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator.WithCommandType;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyChangeNotificationDisabler;
import de.saxsys.synchronizefx.core.metamodel.propertysynchronizer.PropertyChangeDistributor;

/**
 * Listens for changes on JavaFX {@link javafx.beans.property.Property}s and reports them to
 * {@link PropertyChangeDistributor}.
 * 
 * @author Raik Bieniek <raik.bieniek@saxsys.de>
 */
public class JfxPropertyChangeNotifier implements PropertyChangeNotificationDisabler {

    private final PropertyChangeDistributor distributor;
    // FIXME this should only be a temporary dependency
    private final CommandListCreator creator;

    private final JfxPropertyChangeListener strongReferencedPropertyListener = new JfxPropertyChangeListener();
    private final WeakChangeListener<Object> propertyListener
        = new WeakChangeListener<>(strongReferencedPropertyListener);

    public JfxPropertyChangeNotifier(final PropertyChangeDistributor distributor, final CommandListCreator creator) {
        this.distributor = distributor;
        this.creator = creator;
    }

    @Override
    public void disableFor(final Property property) {
        cast(property).getJfxProperty().removeListener(propertyListener);
    }

    @Override
    public void enableFor(final Property property) {
        cast(property).getJfxProperty().addListener(propertyListener);
    }

    private JfxProperty cast(final Property property) {
        return (JfxProperty) property;
    }

    @SuppressWarnings("unchecked")
    private javafx.beans.property.Property<Object> cast(final ObservableValue<? extends Object> observableValue) {
        // this listener is only registered on Property so casting will never fail.
        return (javafx.beans.property.Property<Object>) observableValue;
    }

    /**
     * The JavaFX listener that notifies the change distributor of changes.
     */
    private class JfxPropertyChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(final ObservableValue<? extends Object> property, final Object oldValue,
                final Object newValue) {
            creator.distributeCommands(creator.createCommandList(new WithCommandType() {
                @Override
                public void invoke(final State state) {
                    // FIXME this should be executed directly when CreatCommandList is factored out.
                    distributor.onChange(new JfxProperty(cast(property)));
                }
            }, true).getCommands());
        }
    }
}
