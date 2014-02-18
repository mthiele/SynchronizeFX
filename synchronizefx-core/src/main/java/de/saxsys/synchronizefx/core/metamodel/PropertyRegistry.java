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

import java.util.UUID;

/**
 * Registers the {@link Property} instances of the users domain model and makes them identifiable by {@link UUID}. 
 */
public interface PropertyRegistry {

    /**
     * Returns the {@link Property} instances that is identified by a given {@link UUID}.
     *  
     * @param propertyId The id of the {@link Property} that should be returned.
     * @return The property if one is registered for the given id.
     */
    Optional<Property> getById(UUID propertyId);

}
