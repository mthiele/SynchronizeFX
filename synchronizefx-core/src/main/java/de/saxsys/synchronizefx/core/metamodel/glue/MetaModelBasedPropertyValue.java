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

import de.saxsys.synchronizefx.core.metamodel.MetaModel;
import de.saxsys.synchronizefx.core.metamodel.ObservedValue;
import de.saxsys.synchronizefx.core.metamodel.PropertyVisitor;

/**
 * A temporary implementation that decides if an object is observable based on the current {@link MetaModel}
 * implementation.
 */
public class MetaModelBasedPropertyValue implements ObservedValue {

    private final Object value;
    private final boolean isObservable;

    /**
     * Initializes this property value.
     * 
     * @param object
     *            The value of the property which is either an observable object or a simple object.
     */
    public MetaModelBasedPropertyValue(final Object object) {
        this.value = object;
        this.isObservable = object == null ? false : PropertyVisitor.isObservableObject(object.getClass());
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public boolean isObservable() {
        return isObservable;
    }

}
