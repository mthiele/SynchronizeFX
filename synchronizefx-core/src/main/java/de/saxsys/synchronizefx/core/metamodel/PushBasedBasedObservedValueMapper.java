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

import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;

/**
 * Maps and creates commands to {@link ObservedValue} and vice versa pushing all unknown {@link Observable}s to other
 * peers.
 */
class PushBasedBasedObservedValueMapper implements ObservedValueMapper {

    private final ObservableObjectRegistry mapper;
    private final TopologyLayerCallback topology;
    private final ObservableObjectDistributor observableObjectDistributor;

    /**
     * Initializes an instance with all it's dependencies.
     * 
     * @param mapper
     *            used to retrieve <em>observable objects</em>
     * @param topology
     *            used to inform the next layer on mapping errors.
     * @param observableObjectDistributor
     *            used to inform other peers of new observable objects.
     */
    public PushBasedBasedObservedValueMapper(final ObservableObjectRegistry mapper,
            final TopologyLayerCallback topology, final ObservableObjectDistributor observableObjectDistributor) {
        this.topology = topology;
        this.mapper = mapper;
        this.observableObjectDistributor = observableObjectDistributor;
    }

    @Override
    public ObservedValue map(final Value message) {
        final UUID valueId = message.getObservableObjectId();
        if (valueId != null) {
            Optional<Object> value = mapper.getById(valueId);
            if (!value.isPresent()) {
                topology.onError(new SynchronizeFXException(
                        "SetPropertyValue command with unknown value object id recived. "
                                + message.getObservableObjectId()));
                return new SimplePropertyValue(null, false);
            }
            return new SimplePropertyValue(value.get(), true);
        } else {
            return new SimplePropertyValue(message.getSimpleObjectValue(), false);
        }
    }

    @Override
    public Value map(final ObservedValue value) {
        Value valueMessage = new Value();
        if (!value.isObservable()) {
            valueMessage.setSimpleObjectValue(value.value());
            return valueMessage;
        }

        final UUID valueId = getOrCreateIdFor(value.value());
        valueMessage.setObservableObjectId(valueId);
        return valueMessage;
    }

    private UUID getOrCreateIdFor(final Object value) {
        Optional<UUID> valueId = mapper.getId(value);
        if (!valueId.isPresent()) {
            observableObjectDistributor.distributeNewObservableObject(value);
            // TODO currently registration of the object is made by the distributor above.
            // This should made more clear.
        }
        valueId = mapper.getId(value);
        if (!valueId.isPresent()) {
            throw new RuntimeException(new SynchronizeFXException(
                    "BUG: New observbale object was not registerd in the MetaModel."));
        }
        return valueId.get();
    }
}
