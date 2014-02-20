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

import javafx.beans.property.Property;

import de.saxsys.synchronizefx.core.metamodel.PropertyValue;

/**
 * A {@link de.saxsys.synchronizefx.core.metamodel.Property} wrapping a JavaFX {@link Property}.
 */
public class JfxProperty implements de.saxsys.synchronizefx.core.metamodel.Property {

    private final Property<Object> property;
    
    /**
     * Initializes a new instance.
     * 
     * @param property the JavaFX property to wrap.
     */
    public JfxProperty(final Property<Object> property) {
        this.property = property;
    }
    
    /**
     * The JavaFX property that is wrapped.
     * 
     * @return the property
     */
    public javafx.beans.property.Property<Object> getJfxProperty() {
        return property;
    }

    @Override
    public void setValue(final PropertyValue value) {
        property.setValue(value.value());
    }
}