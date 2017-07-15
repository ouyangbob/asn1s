////////////////////////////////////////////////////////////////////////////////
// Copyright (c) 2010-2017. Lapinin "lastrix" Sergey.                          /
//                                                                             /
// Permission is hereby granted, free of charge, to any person                 /
// obtaining a copy of this software and associated documentation              /
// files (the "Software"), to deal in the Software without                     /
// restriction, including without limitation the rights to use,                /
// copy, modify, merge, publish, distribute, sublicense, and/or                /
// sell copies of the Software, and to permit persons to whom the              /
// Software is furnished to do so, subject to the following                    /
// conditions:                                                                 /
//                                                                             /
// The above copyright notice and this permission notice shall be              /
// included in all copies or substantial portions of the Software.             /
//                                                                             /
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,             /
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES             /
// OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                    /
// NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT                /
// HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,                /
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING                /
// FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE                  /
// OR OTHER DEALINGS IN THE SOFTWARE.                                          /
////////////////////////////////////////////////////////////////////////////////

package org.asn1s.io.ber.output;

import org.asn1s.api.Ref;
import org.asn1s.api.Scope;
import org.asn1s.api.exception.Asn1Exception;
import org.asn1s.api.exception.IllegalValueException;
import org.asn1s.api.type.CollectionOfType;
import org.asn1s.api.type.CollectionType;
import org.asn1s.api.type.ComponentType;
import org.asn1s.api.type.Type;
import org.asn1s.api.value.Value;
import org.asn1s.api.value.Value.Kind;
import org.asn1s.api.value.x680.ValueCollection;
import org.asn1s.io.ber.BerRules;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

final class SequenceOfBerEncoder implements BerEncoder
{
	@Override
	public void encode( @NotNull BerWriter os, @NotNull Scope scope, @NotNull Type type, @NotNull Value value, boolean writeHeader ) throws IOException, Asn1Exception
	{
		if( !( type instanceof CollectionOfType ) || ( (CollectionType)type ).getKind() != CollectionType.Kind.SequenceOf )
			throw new IllegalStateException( "Only SequenceOf type is allowed" );

		if( value.getKind() == Kind.Collection || value.getKind() == Kind.NamedCollection )
		{
			if( writeHeader )
			{
				if( os.isBufferingAvailable() )
				{
					os.startBuffer( -1 );
					writeCollection( scope, os, (CollectionOfType)type, value.toValueCollection() );
					os.stopBuffer( SequenceBerEncoder.TAG );
				}
				else if( os.getRules() == BerRules.Der )
					throw new IOException( "Buffering is required for DER rules" );
				else
				{
					os.writeHeader( SequenceBerEncoder.TAG, -1 );
					writeCollection( scope, os, (CollectionOfType)type, value.toValueCollection() );
					os.write( 0 );
					os.write( 0 );
				}
			}
			else
				writeCollection( scope, os, (CollectionOfType)type, value.toValueCollection() );
		}
		else
			throw new IllegalValueException( "Unable to write value of kind: " + value.getKind() );
	}

	static void writeCollection( Scope scope, BerWriter os, CollectionOfType type, ValueCollection collection ) throws IOException, Asn1Exception
	{
		if( collection.isEmpty() )
			return;

		ComponentType componentType = type.getComponentType();
		if( componentType == null )
			throw new IllegalStateException();

		scope.setValueLevel( collection );
		scope = componentType.getScope( scope );
		for( Ref<Value> ref : collection.asValueList() )
			os.writeInternal( scope, componentType, ref.resolve( scope ), true );
	}
}
