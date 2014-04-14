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
 * A set of values that can be observed for changes.
 */
public interface ObservableSet extends Observable {

    /**
     * Adds a value to the set.
     * 
     * <p>
     * If the set already contained an element that is {@link #equals(Object)} to <code>value</code>, it is replaced
     * with <code>value</code>
     * </p>
     * 
     * @param value
     *            The value to add.
     */
    void add(ObservedValue value);
}
