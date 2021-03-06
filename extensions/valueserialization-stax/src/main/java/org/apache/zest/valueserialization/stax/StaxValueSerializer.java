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
package org.apache.zest.valueserialization.stax;

import java.io.OutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.zest.spi.value.ValueSerializerAdapter;

/**
 * ValueSerializer producing Values state as XML documents.
 */
public class StaxValueSerializer
    extends ValueSerializerAdapter<XMLStreamWriter>
{

    private final XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();

    public StaxValueSerializer()
    {
        // Output Factory setup
        outputFactory.setProperty( "javax.xml.stream.isRepairingNamespaces", Boolean.FALSE );
    }

    @Override
    protected XMLStreamWriter adaptOutput( OutputStream output )
        throws Exception
    {
        XMLStreamWriter xmlStreamWriter = outputFactory.createXMLStreamWriter( output, "UTF-8" );
        xmlStreamWriter.writeStartDocument( "utf-8", "1.1" );
        return xmlStreamWriter;
    }

    @Override
    protected void onSerializationEnd( Object object, XMLStreamWriter output )
        throws Exception
    {
        output.writeEndDocument();
        output.flush();
        output.close();
    }

    @Override
    protected void onArrayStart( XMLStreamWriter output )
        throws Exception
    {
        output.writeStartElement( "array" );
    }

    @Override
    protected void onArrayEnd( XMLStreamWriter output )
        throws Exception
    {
        output.writeEndElement();
    }

    @Override
    protected void onObjectStart( XMLStreamWriter output )
        throws Exception
    {
        output.writeStartElement( "object" );
    }

    @Override
    protected void onObjectEnd( XMLStreamWriter output )
        throws Exception
    {
        output.writeEndElement();
    }

    @Override
    protected void onFieldStart( XMLStreamWriter output, String key )
        throws Exception
    {
        output.writeStartElement( "field" );
        output.writeStartElement( "name" );
        output.writeCharacters( key );
        output.writeEndElement();
    }

    @Override
    protected void onFieldEnd( XMLStreamWriter output )
        throws Exception
    {
        output.writeEndElement();
    }

    @Override
    protected void onValueStart( XMLStreamWriter output )
        throws Exception
    {
        output.writeStartElement( "value" );
    }

    @Override
    protected void onValue( XMLStreamWriter output, Object value )
        throws Exception
    {
        if( value == null )
        {
            output.writeStartElement( "null" );
            output.writeEndElement();
        }
        else
        {
            output.writeCharacters( StringEscapeUtils.escapeXml( value.toString() ) );
        }
    }

    @Override
    protected void onValueEnd( XMLStreamWriter output )
        throws Exception
    {
        output.writeEndElement();
    }
}
