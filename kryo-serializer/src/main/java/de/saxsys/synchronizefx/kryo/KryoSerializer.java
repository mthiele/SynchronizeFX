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

package de.saxsys.synchronizefx.kryo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import de.saxsys.synchronizefx.core.metamodel.commands.Command;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Serializes SynchronizeFX {@link Command}s by using the Kryo library.
 * 
 */
public class KryoSerializer implements de.saxsys.synchronizefx.core.clientserver.Serializer {
    private KryoInitializer kryo = new KryoInitializer();

    /**
     * Registers a class that may be send over the network.
     * 
     * Use this method only before the first invocation of either {@link KryoSerializer#serialize(List)} or
     * {@link KryoSerializer#deserialize(byte[])}. If you invoke it after these methods it is not guaranteed that the
     * {@link Kryo} used by these methods will actually use your serializers.
     * 
     * @param clazz The class that's maybe send.
     * @param serializer An optional serializer for this class. If it's null than the default serialization of kryo
     *            is used.
     * @param <T> see clazz parameter.
     */
    public <T> void registerSerializableClass(final Class<T> clazz, final Serializer<T> serializer) {
        kryo.registerSerializableClass(clazz, serializer);
    }

    /**
     * Serializes SyncronizeFX {@link Command}s to bytes.
     * 
     * To deserialize them, use {@link KryoSerializer#deserialize(byte[])}. This method is thread safe.
     * 
     * @param commands The commands to serialize.
     * @return The commands in serialized form.
     */
    @Override
    public byte[] serialize(final List<Command> commands) {
        final ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        final Output output = new Output(outStream);
        kryo.get().writeObject(output, commands);
        output.close();
        byte[] bytes = outStream.toByteArray();
        try {
            outStream.close();
        } catch (IOException e) {
            // This will not happen, as there is no real I/O. Everything happens in RAM.
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Deserializes {@code byte []} to SynchronizeFX {@link Command}s.
     * 
     * This method is thread save.
     * 
     * @param commands The serialized form of SynchronizeFX {@link Command}s that was created by
     *            {@link KryoSerializer#serialize(List)}.
     * @return The original {@link Command}s.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Command> deserialize(final byte[] commands) {
        return kryo.get().readObject(new Input(commands), LinkedList.class);
    }
}
