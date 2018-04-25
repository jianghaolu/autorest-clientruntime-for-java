/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */
package com.microsoft.azure.arm.resources.models;

import com.microsoft.azure.arm.model.Indexable;
import com.microsoft.azure.management.apigeneration.Fluent;

/**
 * Base interface used by child resources that do not immediately have their parent attached to them but are instead available directly off other entry points.
 * @param <ParentT> parent interface
 */
@Fluent
public interface ParentlessChildResource<ParentT> extends
        Indexable,
        HasName {

    /**
     * @return the parent of this child object
     */
    ParentT getParent();
}
