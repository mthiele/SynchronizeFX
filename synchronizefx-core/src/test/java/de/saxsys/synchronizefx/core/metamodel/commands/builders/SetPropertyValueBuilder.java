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

import de.saxsys.synchronizefx.core.metamodel.commands.SetPropertyValue;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Builds a {@link SetPropertyValue} message with dummy data for unset fields.
 */
public class SetPropertyValueBuilder {

    private final SetPropertyValue message = new SetPropertyValue();

    /**
     * @see SetPropertyValue#getPropertyId()
     * @param propertyId
     *            the id
     * @return this builder
     */
    public SetPropertyValueBuilder propertyId(final UUID propertyId) {
        message.setPropertyId(propertyId);
        return this;
    }

    /**
     * @see SetPropertyValue#getValue()
     * @param value
     *            the value
     * @return this builder
     */
    public SetPropertyValueBuilder value(final Value value) {
        message.setValue(value);
        return this;
    }

    /**
     * Creates the message.
     * 
     * @return the built messages
     */
    public SetPropertyValue build() {
        if (message.getPropertyId() == null) {
            message.setPropertyId(UUID.randomUUID());
        }
        if (message.getValue() == null) {
            message.setValue(ValueBuilder.randomSimpleObject().build());
        }
        return message;
    }
}
