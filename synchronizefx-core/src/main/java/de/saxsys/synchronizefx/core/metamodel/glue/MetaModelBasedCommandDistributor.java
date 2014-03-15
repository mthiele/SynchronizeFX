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

package de.saxsys.synchronizefx.core.metamodel.glue;

import de.saxsys.synchronizefx.core.metamodel.CommandDistributor;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator.State;
import de.saxsys.synchronizefx.core.metamodel.CommandListCreator.WithCommandType;
import de.saxsys.synchronizefx.core.metamodel.TopologyLayerCallback;
import de.saxsys.synchronizefx.core.metamodel.commands.Command;

public class MetaModelBasedCommandDistributor implements CommandDistributor {

    private final TopologyLayerCallback topology;
    private State state;
    private CommandListCreator creator;

    public MetaModelBasedCommandDistributor(final TopologyLayerCallback topology) {
        this.topology = topology;
    }

    @Override
    public void distribute(final Command command) {
        if (state == null) {
            State state = creator.createCommandList(new WithCommandType() {
                @Override
                public void invoke(final State state) {
                    state.getCommands().add(command);
                }
            }, true);
            creator.distributeCommands(state.getCommands());
        } else {
            state.getCommands().add(command);
        }
    }

    public void setState(final State state) {
        this.state = state;
    }

    public void setCommandListCreator(final CommandListCreator creator) {
        this.creator = creator;
    }
}
