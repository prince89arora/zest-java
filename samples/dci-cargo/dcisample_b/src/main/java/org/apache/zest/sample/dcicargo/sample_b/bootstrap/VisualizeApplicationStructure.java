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
package org.apache.zest.sample.dcicargo.sample_b.bootstrap;

import org.apache.zest.api.structure.ApplicationDescriptor;
import org.apache.zest.bootstrap.Energy4Java;
import org.apache.zest.envisage.Envisage;
import org.apache.zest.sample.dcicargo.sample_b.bootstrap.assembly.Assembler;

/**
 * Visualize the application assemblage structure.
 */
public class VisualizeApplicationStructure
{
    public static void main( String[] args )
        throws Exception
    {
        Energy4Java zest = new Energy4Java();
        Assembler assembler = new Assembler();
        ApplicationDescriptor applicationModel = zest.newApplicationModel( assembler );
        applicationModel.newInstance( zest.spi() );

        /*
       * The Envisage Swing app visualizes the application assemblage structure.
       *
       * Tree view:
       * - Click on elements to expand sub-elements.
       * - Scroll to change font size.
       * - Right click on viewer to re-size to fit window.
       *
       * Stacked view:
       * - Scroll to zoom in/out of structure levels - might freeze though :-(
       *
       * Click on any element and see details of that element in the upper right pane.
       *
       * Pretty cool, eh?
       * */
        new Envisage().run( applicationModel );
        int randomTimeoutMs = 18374140;
        Thread.sleep( randomTimeoutMs );
    }
}