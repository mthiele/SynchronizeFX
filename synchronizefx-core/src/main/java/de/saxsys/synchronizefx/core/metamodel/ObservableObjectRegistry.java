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
 * Holds the <em>observable objects</em> of the users domain model and maps them to {@link UUID}s.
 */
public interface ObservableObjectRegistry {

    /**
     * Retrieve an <em>observable object</em> identified by a {@link UUID}.
     * 
     * @param valueId
     *            The id of the <em>observable object</em> that should be retrieved.
     * @return The <em>observable object</em> if one is registered for the given id.
     */
    Optional<Object> getById(UUID valueId);

    /**
     * Returns the identifier for an observable object.
     * 
     * @param object
     *            The object thats id should be returned.
     * @return The id if the object is registered or an empty {@link Observable} if it isn't.
     */
    Optional<UUID> getId(Object object);
}
