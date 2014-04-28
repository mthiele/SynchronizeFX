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

/**
 * Changes an {@link Observable} without notifying listeners of these changes.
 */
public class SilentObservableChanger {

    private final ModelChangeExecutor executor;

    /**
     * Initializes the observable with all its dependencies.
     * 
     * @param executor
     *            An executor that is be used for any changes to the users domain model.
     */
    public SilentObservableChanger(final ModelChangeExecutor executor) {
        this.executor = executor;
    }

    /**
     * Modifies the value of an {@link Observable} without notifying its observers of this change.
     * 
     * <p>
     * For all changes on the {@link Observable} the {@link ModelChangeExecutor} passed in the constructor is used.
     * </p>
     * 
     * @param observable
     *            The observable thats observers should not be notified on changes.
     * @param change
     *            The change to execute on the observable.
     */
    public void silentlyModifyObservable(final Observable observable, final Runnable change) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                observable.disableChangeNotification();
                change.run();
                observable.reEnableChangeNotification();
            }
        });
    }
}
