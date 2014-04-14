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
 * Holds {@link ObservableSet}s that can be retrieved via their id.
 */
public interface ObservableSetRegistry {

    /**
     * Queries the registry for an {@link ObservableSet}.
     * 
     * @param id
     *            The id of the {@link ObservableSet} to return.
     * @return The {@link ObservableSet} registered under the <code>id</code> or an empty {@link Optional} if no
     *         {@link ObservableSet} is registered under that id.
     */
    Optional<ObservableSet> getById(UUID id);
}
