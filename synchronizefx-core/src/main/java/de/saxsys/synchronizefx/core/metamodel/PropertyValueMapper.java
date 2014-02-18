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

import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Maps {@link Value} messages to {@link PropertyValue}s.
 */
public interface PropertyValueMapper {

    /**
     * Maps a message to the corresponding {@link PropertyValue}.
     * 
     * @param message the message to map
     * @return the value for a property.
     */
    PropertyValue map(Value message);
}
