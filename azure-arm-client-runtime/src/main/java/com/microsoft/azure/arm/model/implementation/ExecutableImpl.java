/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.arm.model.implementation;

import com.microsoft.azure.arm.dag.FunctionalTaskItem;
import com.microsoft.azure.arm.dag.TaskGroup;
import com.microsoft.azure.arm.model.Appliable;
import com.microsoft.azure.arm.model.Creatable;
import com.microsoft.azure.arm.model.Executable;
import com.microsoft.azure.arm.model.Indexable;
import com.microsoft.azure.arm.utils.SdkContext;
import com.microsoft.rest.ServiceCallback;
import com.microsoft.rest.ServiceFuture;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;

import java.util.Objects;

/**
 * The base class for all executable model.
 *
 * @param <FluentModelT> the fluent model type
 */
public abstract class ExecutableImpl<FluentModelT extends Indexable>
        extends
        IndexableImpl
        implements
        TaskGroup.HasTaskGroup,
        Executable<FluentModelT>,
        ExecuteTask.Executor<FluentModelT> {
    /**
     * The group of tasks to the produces this result and it's dependencies results.
     */
    private final TaskGroup taskGroup;

    /**
     * Creates ExecutableImpl.
     */
    protected ExecutableImpl() {
        this(SdkContext.randomUuid());
    }

    /**
     * Creates ExecutableImpl.
     *
     * @param key the task group key for the executable
     */
    protected ExecutableImpl(String key) {
        super(key);
        taskGroup = new TaskGroup(this.key(),
                new ExecuteTask(this));
    }

    @Override
    public TaskGroup taskGroup() {
        return this.taskGroup;
    }

    /**
     * Add a dependency task item for this executable.
     *
     * @param dependency the dependency task item.
     * @return key to be used as parameter to taskResult(string) method to retrieve result the task item
     */
    protected String addDependency(FunctionalTaskItem dependency) {
        Objects.requireNonNull(dependency);
        return this.taskGroup.addDependency(dependency);
    }

    /**
     * Add a dependency for this executable.
     *
     * @param dependency the dependency.
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the given dependency task group
     */
    protected String addDependency(TaskGroup.HasTaskGroup dependency) {
        Objects.requireNonNull(dependency);
        this.taskGroup.addDependencyTaskGroup(dependency.taskGroup());
        return dependency.taskGroup().key();
    }

    /**
     * Add a creatable dependency for this executable.
     *
     * @param creatable the creatable dependency.
     * @return the key to be used as parameter to taskResult(string) method to retrieve created dependency
     */
    @SuppressWarnings("unchecked")
    protected String addDependency(Creatable<? extends Indexable> creatable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) creatable;
        return this.addDependency(dependency);
    }

    /**
     * Add an updatable dependency for this executable.
     *
     * @param appliable the appliable dependency.
     * @return the key to be used as parameter to taskResult(string) method to retrieve updated dependency
     */
    @SuppressWarnings("unchecked")
    protected String addeDependency(Appliable<? extends Indexable> appliable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) appliable;
        return this.addDependency(dependency);
    }

    /**
     * Add an executable dependency for this executable.
     *
     * @param executable the executable dependency
     * @return the key to be used as parameter to taskResult(string) method to retrieve result of executing
     * the given executable dependency
     */
    @SuppressWarnings("unchecked")
    protected String addDependency(Executable<? extends Indexable> executable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) executable;
        return this.addDependency(dependency);
    }

    /**
     * Add a "post-run" dependent task item for this executable.
     *
     * @param dependent the "post-run" dependent task item.
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the given dependent task group
     */
    public String addPostRunDependent(FunctionalTaskItem dependent) {
        Objects.requireNonNull(dependent);
        return this.taskGroup().addPostRunDependent(dependent);
    }

    /**
     * Add a "post-run" dependent for this executable.
     *
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the dependent task group
     */
    protected String addPostRunDependent(TaskGroup.HasTaskGroup dependent) {
        Objects.requireNonNull(dependent);
        this.taskGroup.addPostRunDependentTaskGroup(dependent.taskGroup());
        return dependent.taskGroup().key();
    }

    /**
     * Add a creatable "post-run" dependent for this executable.
     *
     * @return the key to be used as parameter to taskResult(string) method to retrieve created "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Creatable<? extends Indexable> creatable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) creatable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Add an appliable "post-run" dependent for this executable.
     *
     * @return the key to be used as parameter to taskResult(string) method to retrieve updated "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Appliable<? extends Indexable> appliable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) appliable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Add an executable "post-run" dependent for this executable.
     *
     * @return the key to be used as parameter to taskResult(string) method to retrieve result of executing
     * the executable "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Executable<? extends Indexable> executable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) executable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Get result of one of the task that belongs to this task's task group.
     *
     * @param key the task key
     * @param <T> the actual type of the task result
     * @return the task result, null will be returned if task has not produced a result yet
     */
    @SuppressWarnings("unchecked")
    protected <T extends Indexable> T taskResult(String key) {
        Indexable result = this.taskGroup.taskResult(key);
        if (result == null) {
            return null;
        } else {
            T castedResult = (T) result;
            return castedResult;
        }
    }

    @Override
    public void beforeGroupExecute() {
    }

    @Override
    public boolean isHot() {
        return false;
    }

    @Override
    public Observable<FluentModelT> executeAsync() {
        return taskGroup.invokeAsync(taskGroup.newInvocationContext())
                .last()
                .map(new Func1<Indexable, FluentModelT>() {
                    @Override
                    public FluentModelT call(Indexable indexable) {
                        return (FluentModelT) indexable;
                    }
                });
    }

    @Override
    public FluentModelT execute() {
        return executeAsync().toBlocking().last();
    }

    @Override
    public ServiceFuture<FluentModelT> executeAsync(ServiceCallback<FluentModelT> callback) {
        return ServiceFuture.fromBody(executeAsync(), callback);
    }

    @Override
    public Completable afterPostRunAsync(boolean isGroupFaulted) {
        return Completable.complete();
    }
}