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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SetProperty;
import de.saxsys.synchronizefx.core.exceptions.SynchronizeFXException;
import de.saxsys.synchronizefx.core.metamodel.ModelWalkingSynchronizer.ActionType;
import de.saxsys.synchronizefx.core.metamodel.commands.AddToList;
import de.saxsys.synchronizefx.core.metamodel.commands.AddToSet;
import de.saxsys.synchronizefx.core.metamodel.commands.ClearReferences;
import de.saxsys.synchronizefx.core.metamodel.commands.CreateObservableObject;
import de.saxsys.synchronizefx.core.metamodel.commands.PutToMap;
import de.saxsys.synchronizefx.core.metamodel.commands.RemoveFromList;
import de.saxsys.synchronizefx.core.metamodel.commands.RemoveFromMap;
import de.saxsys.synchronizefx.core.metamodel.commands.RemoveFromSet;
import de.saxsys.synchronizefx.core.metamodel.commands.SetRootElement;
import de.saxsys.synchronizefx.core.metamodel.commands.Value;
import de.saxsys.synchronizefx.core.metamodel.glue.MetaModelBasedCommandDistributor;
import de.saxsys.synchronizefx.core.metamodel.glue.MetaModleBasedObservableObjectDistributor;
import de.saxsys.synchronizefx.core.metamodel.javafx.JfxProperty;
import de.saxsys.synchronizefx.core.metamodel.javafx.JfxPropertyChangeNotifier;
import de.saxsys.synchronizefx.core.metamodel.propertysynchronizer.PropertyChangeDistributor;

/**
 * Creates various types of commands that describe changes on the domain model.
 */
public class CommandListCreator {

    private final MetaModel parent;
    private final TopologyLayerCallback topology;

    private final PropertyChangeDistributor propertyChangeDistributor;
    private final MetaModelBasedCommandDistributor commandDistributor;
    private final MetaModleBasedObservableObjectDistributor observableObjectDistributor;
    private final ModelWalkingSynchronizer synchronizer;
    private final JfxPropertyChangeNotifier propertyChangeNotifier;

    /**
     * Initializes the creator.
     * 
     * @param parent
     *            The model used to lookup and set ids for objects.
     * @param topology
     *            The user callback used to report errors.
     * @param commandDistributor
     */
    public CommandListCreator(final MetaModel parent, final TopologyLayerCallback topology,
            final PropertyChangeDistributor propertyChangeDistributor,
            final MetaModelBasedCommandDistributor commandDistributor,
            final MetaModleBasedObservableObjectDistributor observableObjectDistributor,
            final ModelWalkingSynchronizer synchronizer,
            final JfxPropertyChangeNotifier propertyChangeNotifier) {
        this.parent = parent;
        this.topology = topology;

        this.propertyChangeDistributor = propertyChangeDistributor;
        this.commandDistributor = commandDistributor;
        this.observableObjectDistributor = observableObjectDistributor;
        this.synchronizer = synchronizer;
        this.propertyChangeNotifier = propertyChangeNotifier;
    }

    /**
     * @see MetaModel#commandsForDomainModel()
     * 
     * @param root
     *            The root object of the domain model.
     * @param callback
     *            The callback that takes the commands necessary to rebuild the domain model at it's current state.
     */
    public void commandsForDomainModel(final Object root, final CommandsForDomainModelCallback callback) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                createObservableObject(root, state);
            }
        }, false);

        SetRootElement msg = new SetRootElement();
        msg.setRootElementId(parent.getId(root));
        // prepend it to ClearReferences message
        state.getCommands().add(state.getCommands().size() - 1, msg);

        callback.commandsReady(state.getCommands());
    }

    /**
     * Creates the list with commands necessary for an add to list action.
     * 
     * @param listId
     *            The ID of the list where the element should be added.
     * @param position
     *            The position in the list at which the value object should be added.
     * @param value
     *            The object that should be added to the list.
     * @param newSize
     *            The new size the list has after this command has been executed on it.
     * @return a list with commands necessary to recreate this add to list command.
     */
    public List<Object> addToList(final UUID listId, final int position, final Object value, final int newSize) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                addToList(listId, position, value, newSize, state);
            }
        }, true);
        return state.getCommands();
    }

    /**
     * Creates the list with commands necessary for an add to set action.
     * 
     * @param setId
     *            The ID of the set where the element should be added.
     * @param value
     *            The object that should be added to the set.
     * @return a set with commands necessary to recreate this add to set command.
     */
    public List<Object> addToSet(final UUID setId, final Object value) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                addToSet(setId, value, state);
            }
        }, true);
        return state.getCommands();
    }

    /**
     * Creates the list with commands necessary to put a mapping into a map.
     * 
     * @param mapId
     *            the id of the map where the mapping should be added.
     * @param key
     *            the key of the new mapping.
     * @param value
     *            the value of the new mapping.
     * @return the list with the commands.
     */
    public List<Object> putToMap(final UUID mapId, final Object key, final Object value) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                putToMap(mapId, key, value, state);
            }
        }, true);
        return state.getCommands();
    }

    /**
     * Creates the list with commands necessary to remove a object from a list.
     * 
     * @param listId
     *            The ID of the list where an element should be removed.
     * @param position
     *            The position of the element in the list which should be removed.
     * @param newSize
     *            The size the list will have after this command has been applied.
     * @return The command list.
     */
    public List<Object> removeFromList(final UUID listId, final int position, final int newSize) {
        RemoveFromList msg = new RemoveFromList();
        msg.setListId(listId);
        msg.setPosition(position);
        msg.setNewSize(newSize);
        List<Object> commands = new ArrayList<>(1);
        commands.add(msg);
        return commands;
    }

    /**
     * Creates the list with command necessary to remove a mapping from a map.
     * 
     * @param mapId
     *            the map where the mapping should be removed.
     * @param key
     *            the key of the mapping that should be removed.
     * @return the list with the commands.
     */
    public List<Object> removeFromMap(final UUID mapId, final Object key) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                createObservableObject(key, state);
            }
        }, false);

        boolean keyIsObservableObject = state.lastObjectWasObservable;

        RemoveFromMap msg = new RemoveFromMap();
        msg.setMapId(mapId);

        if (keyIsObservableObject) {
            msg.setKeyObservableObjectId(parent.getId(key));
        } else {
            msg.setKeySimpleObjectValue(key);
        }
        state.getCommands().add(state.getCommands().size() - 1, msg);
        return state.getCommands();
    }

    /**
     * Creates the list with commands necessary to remove a object from a set.
     * 
     * @param setId
     *            The ID of the set where an element should be removed.
     * @param value
     *            The element that should be removed.
     * @return The command list.
     */
    public List<Object> removeFromSet(final UUID setId, final Object value) {
        State state = createCommandList(new WithCommandType() {
            @Override
            public void invoke(final State state) {
                createObservableObject(value, state);
            }
        }, false);

        boolean keyIsObservableObject = state.lastObjectWasObservable;

        RemoveFromSet msg = new RemoveFromSet();
        msg.setSetId(setId);

        if (keyIsObservableObject) {
            msg.setObservableObjectId(parent.getId(value));
        } else {
            msg.setSimpleObjectValue(value);
        }

        state.getCommands().add(state.getCommands().size() - 1, msg);
        return state.getCommands();
    }

    private void addToList(final UUID listId, final int position, final Object value, final int newSize,
            final State state) {
        AddToList msg = new AddToList();
        msg.setListId(listId);
        msg.setPosition(position);
        msg.setNewSize(newSize);

        boolean isObservableObject = createObservableObject(value, state);
        if (isObservableObject) {
            msg.setObservableObjectId(parent.getId(value));
        } else {
            msg.setSimpleObjectValue(value);
        }

        state.getCommands().add(msg);
    }

    private void addToSet(final UUID setId, final Object value, final State state) {
        AddToSet msg = new AddToSet();
        msg.setSetId(setId);

        boolean isObservableObject = createObservableObject(value, state);
        Value valueMessage = new Value();
        if (isObservableObject) {
            valueMessage.setObservableObjectId(parent.getId(value));
        } else {
            valueMessage.setSimpleObjectValue(value);
        }
        msg.setValue(valueMessage);

        state.getCommands().add(msg);
    }

    private void putToMap(final UUID mapId, final Object key, final Object value, final State state) {
        PutToMap msg = new PutToMap();
        msg.setMapId(mapId);

        boolean keyIsObservableObject = createObservableObject(key, state);
        boolean valueIsObservableObject = createObservableObject(value, state);

        if (keyIsObservableObject) {
            msg.setKeyObservableObjectId(parent.getId(key));
        } else {
            msg.setKeySimpleObjectValue(key);
        }

        if (valueIsObservableObject) {
            msg.setValueObservableObjectId(parent.getId(value));
        } else {
            msg.setValueSimpleObjectValue(value);
        }
        state.getCommands().add(msg);
    }

    /**
     * Adds commands to the list that are necessary to create the observable object.
     * 
     * If {@code value} isn't an observable object, then nothing is added to the commandList.
     * 
     * @param value
     *            The object for which the commands should be created.
     * @param state
     *            The state of this domain model parsing.
     * @return true if value is an observable object and false otherwise.
     */
    public boolean createObservableObject(final Object value, final State state) {
        if (value == null || !PropertyVisitor.isObservableObject(value.getClass())) {
            return state.lastObjectWasObservable = false;
        }

        synchronized (state.alreadyVisited) {
            if (state.alreadyVisited.containsKey(value)) {
                return state.lastObjectWasObservable = true;
            }
            state.alreadyVisited.put(value, null);
        }

        if (state.skipKnown && parent.getId(value) != null) {
            return state.lastObjectWasObservable = true;
        }

        final CreateObservableObject msg = new CreateObservableObject();
        int currentSize = state.getCommands().size();

        try {
            new PropertyVisitor(value) {

                @Override
                protected boolean visitSingleValueProperty(final Property<?> fieldValue) {
                    registerPropertyAndParent(getCurrentField(), fieldValue);
                    createObservableObject(fieldValue.getValue(), state);
                    propertyChangeDistributor.onChange(new JfxProperty((Property<Object>) fieldValue, propertyChangeNotifier));
                    return false;
                }

                @Override
                protected boolean visitCollectionProperty(final ListProperty<?> fieldValue) {
                    UUID fieldId = registerPropertyAndParent(getCurrentField(), fieldValue);
                    ListIterator<?> it = fieldValue.listIterator();
                    int index = 0;
                    while (it.hasNext()) {
                        Object o = it.next();
                        addToList(fieldId, index, o, index + 1, state);
                        index++;
                    }
                    return false;
                }

                @Override
                protected boolean visitCollectionProperty(final MapProperty<?, ?> fieldValue) {
                    UUID fieldId = registerPropertyAndParent(getCurrentField(), fieldValue);
                    for (Entry<?, ?> entry : fieldValue.entrySet()) {
                        putToMap(fieldId, entry.getKey(), entry.getValue(), state);
                    }
                    return false;
                }

                @Override
                protected boolean visitCollectionProperty(final SetProperty<?> fieldValue) {
                    UUID fieldId = registerPropertyAndParent(getCurrentField(), fieldValue);
                    for (Object entry : fieldValue) {
                        addToSet(fieldId, entry, state);
                    }
                    return false;
                }

                private UUID registerPropertyAndParent(final Field field, final Property<?> fieldValue) {
                    msg.setObjectId(parent.registerIfUnknown(value));
                    UUID fieldId = parent.registerIfUnknown(fieldValue);
                    msg.getPropertyNameToId().put(field.getName(), fieldId);
                    return fieldId;
                }
            };
        } catch (IllegalAccessException e) {
            topology.onError(new SynchronizeFXException(e));
        } catch (SecurityException e) {
            topology.onError(new SynchronizeFXException(
                    "Maybe you're JVM doesn't allow reflection for this application?", e));
        }

        if (msg.getObjectId() == null) {
            return state.lastObjectWasObservable = false;
        }
        msg.setClassName(value.getClass().getName());
        // create the object before it's field values are set
        state.getCommands().add(currentSize, msg);
        return state.lastObjectWasObservable = true;
    }

    public State createCommandList(final WithCommandType type, final boolean skipKnown) {
        State state = new State(skipKnown);
        this.commandDistributor.setState(state);
        this.observableObjectDistributor.setState(state);
        boolean restart = true;
        while (restart) {
            restart = false;
            state.reset();
            try {
                type.invoke(state);
            } catch (ConcurrentModificationException e) {
                restart = true;
            }
        }
        commandDistributor.setState(null);
        observableObjectDistributor.setState(null);
        state.getCommands().add(new ClearReferences());
        return state;
    }
    
    public void distributeCommands(final List<Object> commands) {
        synchronizer.doWhenModelWalkerFinished(ActionType.LOCAL_PROPERTY_CHANGES, new Runnable() {
            @Override
            public void run() {
                topology.sendCommands(commands);
            }
        });
    }

    /**
     * The state that must be keeped for the creation of depend messages.
     */
    public static class State {
        /**
         * only {@code synchronized} access allowed.
         */
        private final Map<Object, Object> alreadyVisited = new IdentityHashMap<>();
        private final List<Object> commands = new LinkedList<>();
        private final boolean skipKnown;
        /**
         * Holds the return value of the last invocation of
         * {@link CommandListCreator#createObservableObject(Object, State)}.
         */
        private boolean lastObjectWasObservable;

        public State(final boolean skipKnown) {
            this.skipKnown = skipKnown;
        }

        /**
         * Resets all state holding fields to the state when this object was instantiated.
         */
        public void reset() {
            alreadyVisited.clear();
            commands.clear();
            lastObjectWasObservable = false;
        }

        /**
         * The list of commands that should be distributed together.
         * 
         * @return The list of commands.
         */
        public List<Object> getCommands() {
            return commands;
        }

        /**
         * Whether commands for known objects should be created or not.
         * 
         * @return <code>false</code> if they should, <code>true</code> if they shouldn't.
         */
        public boolean getSkipKnown() {
            return skipKnown;
        }
    }

    /**
     * Used to define methods that should be really be executed when calling
     * {@link CommandListCreator#createCommandList(WithCommandType, boolean)}.
     */
    public interface WithCommandType {
        void invoke(State state);
    }
}
