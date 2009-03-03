/*  Copyright 2009 Tonny Kohar.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied.
*
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.qi4j.library.swing.envisage.graph;

import org.qi4j.library.swing.envisage.model.descriptor.ApplicationDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.EntityDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.LayerDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ModuleDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ObjectDetailDescriptor;
import org.qi4j.library.swing.envisage.model.descriptor.ServiceDetailDescriptor;
import prefuse.data.Edge;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Table;

/**
 * @author Tonny Kohar (tonny.kohar@gmail.com)
 */
public class GraphBuilderExperiment
{
    private Graph graph = null;

    public static Graph buildGraph( ApplicationDetailDescriptor descriptor )
    {
        GraphBuilderExperiment builder = new GraphBuilderExperiment();
        return builder.buildApplicationNode( descriptor );
    }

    private GraphBuilderExperiment()
    {
        graph = new Graph(true);
        Table nodeTable = graph.getNodeTable();
        nodeTable.addColumn( GraphDisplay.NAME_LABEL, String.class);
        nodeTable.addColumn(GraphDisplay.USER_OBJECT, Object.class);

        Table edgeTable = graph.getEdgeTable();
        edgeTable.addColumn(GraphDisplay.USES_EDGES, Boolean.class, false);
    }

    private Graph buildApplicationNode( ApplicationDetailDescriptor descriptor )
    {
        Node node = graph.addNode();
        node.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().name());
        node.set(GraphDisplay.USER_OBJECT, descriptor);

        buildLayersNode( node, descriptor.layers() );

        buildUsesNode ( node, descriptor.layers());

        return graph;
    }


    private void buildLayersNode( Node parent, Iterable<LayerDetailDescriptor> iter )
    {
        for( LayerDetailDescriptor descriptor : iter )
        {
            Node childNode = graph.addNode( );
            childNode.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().name());
            childNode.set(GraphDisplay.USER_OBJECT, descriptor );
            graph.addEdge( parent, childNode );
            buildModulesNode( childNode, descriptor.modules() );
        }
    }

    private void buildUsesNode(Node parent, Iterable<LayerDetailDescriptor> iter)
    {
        for( LayerDetailDescriptor descriptor : iter )
        {
            Node source = findNode( parent, descriptor );

            for (LayerDetailDescriptor usesDescriptor : descriptor.usedLayers())
            {
                Node target = findNode(parent, usesDescriptor);
                Edge edge = graph.addEdge( source, target );
                edge.set( GraphDisplay.USES_EDGES, true );
            }
        }

        // TESTING Uses/Layer
        /*Node node = parent;

        System.out.println(node.getChild(0).get(GraphDisplay.NAME_LABEL ));
        System.out.println(node.getChild(1).get(GraphDisplay.NAME_LABEL ));
        System.out.println(node.getChild(2).get(GraphDisplay.NAME_LABEL ));



        Edge edge = graph.addEdge( node.getChild(1), node.getChild(0) );
        edge.set( GraphDisplay.USES_EDGES, true );
        edge = graph.addEdge( node.getChild(2), node.getChild(1) );
        edge.set( GraphDisplay.USES_EDGES, true );
        */
    }

    private Node findNode(Node parent, Object userObject)
    {
        Node node = null;

        for (int i=0; i<parent.getChildCount(); i++)
        {
            Node tNode = parent.getChild( i );
            Object obj = tNode.get(GraphDisplay.USER_OBJECT);
            if (obj.equals( userObject ))
            {
                node = tNode;
                break;
            }
        }

        return node;
    }

    private void buildModulesNode( Node parent, Iterable<ModuleDetailDescriptor> iter )
    {
        for( ModuleDetailDescriptor descriptor : iter )
        {
            Node childNode = graph.addNode( );
            childNode.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().name());
            childNode.set(GraphDisplay.USER_OBJECT, descriptor );
            graph.addEdge( parent, childNode );
            //buildServicesNode( childNode, descriptor.services() );
            //buildEntitiesNode( childNode, descriptor.entities() );
            //buildObjectsNode( childNode, descriptor.objects() );
        }
   }

    private void buildServicesNode( Node parent, Iterable<ServiceDetailDescriptor> iter )
    {
        for( ServiceDetailDescriptor descriptor : iter )
        {
            Node childNode = graph.addNode( );
            childNode.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().identity());
            childNode.set(GraphDisplay.USER_OBJECT, descriptor );
            graph.addEdge( parent, childNode );
        }
    }

    private void buildEntitiesNode( Node parent, Iterable<EntityDetailDescriptor> iter )
    {
        for( EntityDetailDescriptor descriptor : iter )
        {
            Node childNode = graph.addNode( );
            childNode.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().type().getSimpleName());
            childNode.set(GraphDisplay.USER_OBJECT, descriptor );
            graph.addEdge( parent, childNode );
        }
    }

    private void buildObjectsNode( Node parent, Iterable<ObjectDetailDescriptor> iter )
    {
        for( ObjectDetailDescriptor descriptor : iter )
        {
            Node childNode = graph.addNode( );
            childNode.set(GraphDisplay.NAME_LABEL, descriptor.descriptor().type().getSimpleName());
            childNode.set(GraphDisplay.USER_OBJECT, descriptor );
            graph.addEdge( parent, childNode );
        }
    }
}
