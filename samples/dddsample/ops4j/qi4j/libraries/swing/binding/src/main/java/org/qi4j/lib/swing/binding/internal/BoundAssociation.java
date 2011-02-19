/*
 * Copyright 2008 Niclas Hedhman. All rights Reserved.
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.qi4j.lib.swing.binding.internal;

import java.awt.event.FocusListener;
import java.lang.reflect.Method;
import javax.swing.JComponent;
import org.qi4j.composite.ObjectBuilderFactory;
import org.qi4j.composite.scope.Service;
import org.qi4j.composite.scope.Structure;
import org.qi4j.composite.scope.Uses;
import org.qi4j.entity.association.Association;
import org.qi4j.lib.swing.binding.Binding;
import org.qi4j.lib.swing.binding.SwingAdapter;

public final class BoundAssociation<T> extends AbstractBinding<T>
    implements Association<T>, Binding
{

    private Association<T> actual;

    /**
     * Construct an instance of {@code BoundAssociation} with the specified arguments.
     *
     * @param associationMethod The method that returns Assocation. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code aMethod} is {@code null}.
     */
    public BoundAssociation( @Uses Method associationMethod, @Structure ObjectBuilderFactory objectBuilderFactory,
                             @Service Iterable<SwingAdapter> allAdapters )
        throws IllegalArgumentException
    {
        super( associationMethod, objectBuilderFactory, allAdapters );

    }

    public <K> K metaInfo( Class<K> infoType )
    {
        if( actual == null )
        {
            return null;
        }
        return actual.metaInfo( infoType );
    }

    public String name()
    {
        if( actual == null )
        {
            return null;
        }
        return actual.name();
    }

    public String qualifiedName()
    {
        if( actual == null )
        {
            return null;
        }
        return actual.qualifiedName();
    }

    public void use( Association<T> actualAssociation )
    {
        actual = actualAssociation;
        T value = null;
        if( actualAssociation != null )
        {
            value = actualAssociation.get();
        }
        stateModel.use( value );
        for( JComponent component : components )
        {
            SwingAdapter adapter = adapters.get( component.getClass() );
            adapter.fromAssociationToSwing( component, actualAssociation );
            for( FocusListener listener : component.getFocusListeners() )
            {
                if( AssociationFocusLostListener.class.isInstance( listener ) )
                {
                    ( (AssociationFocusLostListener) listener ).use( adapter, actual );
                }
            }
        }
    }

    protected boolean requiredCapabilitySatisfied( SwingAdapter.Capabilities capabilities )
    {
        return capabilities.association;
    }

    public void to( JComponent component )
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
