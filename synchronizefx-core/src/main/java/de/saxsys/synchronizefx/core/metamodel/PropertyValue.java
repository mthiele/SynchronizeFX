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
 * A value for {@link Property} which can be an <em>observable object</em> if it contains {@link Property} fields itself
 * or a <em>simple object</em> if it does not.
 * 
 * <p>
 * The {@link Property}s of <em>observable object</em> can be monitored for changes too while <em>simple objects</em>
 * cannot.
 */
public interface PropertyValue {

    /**
     * The value that is wrapped.
     * 
     * @return the value
     */
    Object value();

    /**
     * Whether this value is an observable object or a simple object.
     * 
     * @return <code>true</code> if this instance contains an <em>observable object</em> and <code>false</code> if it
     *         contains a <em>simple object</em>.
     */
    boolean isObservable();
}
