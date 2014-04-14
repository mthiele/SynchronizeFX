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

package de.saxsys.synchronizefx.core.metamodel.commands.builders;

import java.util.UUID;

import de.saxsys.synchronizefx.core.metamodel.commands.AddToSet;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Builds a {@link AddToSet} commands with dummy data for unset fields.
 */
public class AddToSetBuilder {

    private UUID id;
    private Value value;

    /**
     * @see AddToSet#getSetId()
     * @param id
     *            the id
     * @return this builder
     */
    public AddToSetBuilder withSetId(final UUID id) {
        this.id = id;
        return this;
    }

    /**
     * @see AddToSet#getValue()
     * @param value
     *            the value
     * @return this builder
     */
    public AddToSetBuilder withValue(final ValueBuilder value) {
        this.value = value.build();
        return this;
    }

    /**
     * @see AddToSet#getValue()
     * @param value
     *            the value
     * @return the builder
     */
    public AddToSetBuilder withValue(final Value value) {
        this.value = value;
        return this;
    }

    /**
     * Builds the command.
     * 
     * @return The build command
     */
    public AddToSet build() {
        AddToSet command = new AddToSet();
        if (id == null) {
            command.setSetId(UUID.randomUUID());
        } else {
            command.setSetId(id);
        }
        if (value == null) {
            command.setValue(ValueBuilder.randomSimpleObject().build());
        } else {
            command.setValue(value);
        }
        return command;
    }

    /**
     * Creates a new builder.
     * 
     * @return The builder created.
     */
    public static AddToSetBuilder addToSetCommand() {
        return new AddToSetBuilder();
    }
}
