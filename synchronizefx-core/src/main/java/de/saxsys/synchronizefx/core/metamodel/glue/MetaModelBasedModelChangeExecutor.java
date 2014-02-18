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

import javafx.application.Platform;

import de.saxsys.synchronizefx.core.metamodel.CommandListExecutor;
import de.saxsys.synchronizefx.core.metamodel.MetaModel;
import de.saxsys.synchronizefx.core.metamodel.ModelChangeExecutor;

/**
 * A {@link ModelChangeExecutor} which executes changes in the JavaFX if set in the {@link MetaModel}.
 * 
 * <p>
 * This is a temporary implementation while the deprecation of {@link CommandListExecutor} and friends is in progress.
 * </p>
 */
public class MetaModelBasedModelChangeExecutor implements ModelChangeExecutor {

    private final MetaModel metaModel;

    /**
     * Initializes the executor.
     * 
     * @param metaModel
     *            used to decide whether changes on the domain model should be done in the JavaFX thread or not.
     */
    public MetaModelBasedModelChangeExecutor(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public void execute(final Runnable runnable) {
        if (metaModel.isDoChangesInJavaFxThread()) {
            Platform.runLater(runnable);
        } else {
            runnable.run();
        }
    }
}
