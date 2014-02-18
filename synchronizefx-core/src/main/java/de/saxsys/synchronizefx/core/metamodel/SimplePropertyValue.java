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

package de.saxsys.synchronizefx.core.metamodel;


/**
 * An implementation that just stores the value and whether it is observable or not.
 */
class SimplePropertyValue implements PropertyValue {

    private final Object value;
    private final boolean isObservable;

    /**
     * Initializes an instance.
     * 
     * @param value
     *            The value to wrap.
     * @param isObservable
     *            see {@link PropertyValue#isObservable()}
     */
    public SimplePropertyValue(final Object value, final boolean isObservable) {
        this.value = value;
        this.isObservable = isObservable;
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
