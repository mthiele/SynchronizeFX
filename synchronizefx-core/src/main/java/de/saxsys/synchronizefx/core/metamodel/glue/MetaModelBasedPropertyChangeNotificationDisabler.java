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

package de.saxsys.synchronizefx.core.metamodel.glue;

import de.saxsys.synchronizefx.core.metamodel.CommandListExecutor;
import de.saxsys.synchronizefx.core.metamodel.Listeners;
import de.saxsys.synchronizefx.core.metamodel.Property;
import de.saxsys.synchronizefx.core.metamodel.PropertyChangeNotificationDisabler;
import de.saxsys.synchronizefx.core.metamodel.javafx.JfxProperty;

/**
 * A notification disabler implementation based on {@link Listeners}.
 * 
 * <p>
 * This is a temporary class intended as glue code while the deprecation of the monolithic {@link CommandListExecutor}
 * and friends classes is in progress.
 * </p>
 */
public class MetaModelBasedPropertyChangeNotificationDisabler implements PropertyChangeNotificationDisabler {

    private final Listeners listeners;

    /**
     * Initializes an instance with all it's dependencies.
     * 
     * @param listeners
     *            used to disable change notification.
     */
    public MetaModelBasedPropertyChangeNotificationDisabler(final Listeners listeners) {
        this.listeners = listeners;
    }

    @Override
    public void disableFor(final Property prop) {
        listeners.disableFor(getJfxPropertyOrFail(prop));
    }

    @Override
    public void enableFor(final Property prop) {
        listeners.enableFor(getJfxPropertyOrFail(prop));
    }

    private javafx.beans.property.Property<Object> getJfxPropertyOrFail(final Property prop) {
        if (!(prop instanceof JfxProperty)) {
            throw new IllegalStateException("This implementation of the property change notification disabler "
                    + "works only for JfxProprties.");
        }
        JfxProperty jfxProperty = (JfxProperty) prop;
        return jfxProperty.getJfxProperty();
    }
}
