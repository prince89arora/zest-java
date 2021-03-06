/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */
package org.apache.zest.sample.swing.binding.internal;

import org.apache.zest.api.association.Association;
import org.apache.zest.sample.swing.binding.SwingAdapter;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class AssociationFocusLostListener
    implements FocusListener
{

    private final JComponent component;
    private SwingAdapter adapter;
    private Association actual;

    public AssociationFocusLostListener( JComponent component )
    {
        this.component = component;
    }

    void use( SwingAdapter adapterToUse, Association actual )
    {
        adapter = adapterToUse;
        this.actual = actual;
    }

    public void focusGained( FocusEvent e )
    {
    }

    public void focusLost( FocusEvent e )
    {
        if( adapter != null )
        {
            adapter.fromSwingToAssociation( component, actual );
        }
    }
}
