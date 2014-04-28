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
 * A wrapper for changeable values that can be observed for changes.
 */
public interface Observable {

    /**
     * Disable the change notification for any change done to the values of this {@link Observable}.
     */
    void disableChangeNotification();

    /**
     * Re-Enables change notification for any change done to the values of this {@link Observable}.
     * 
     * <p>
     * As long as {@link #disableChangeNotification()} has not been called, change notification is enabled by default.
     * </p>
     */
    void reEnableChangeNotification();
}
