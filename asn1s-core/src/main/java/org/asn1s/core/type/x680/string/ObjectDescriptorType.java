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

package org.asn1s.core.type.x680.string;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asn1s.api.Ref;
import org.asn1s.api.UniversalType;
import org.asn1s.api.encoding.tag.TagClass;
import org.asn1s.api.encoding.tag.TagEncoding;
import org.asn1s.api.encoding.tag.TagMethod;
import org.asn1s.api.type.Type;
import org.asn1s.core.type.AbstractNestingBuiltinType;
import org.asn1s.core.type.TaggedTypeImpl;
import org.jetbrains.annotations.NotNull;

/**
 * X.680, p 48.3
 * ObjectDescriptor ::= [UNIVERSAL 7] IMPLICIT GraphicString
 *
 * @author lastrix
 * @version 1.0
 */
public final class ObjectDescriptorType extends AbstractNestingBuiltinType
{
	private static final Log log = LogFactory.getLog( ObjectDescriptorType.class );

	public ObjectDescriptorType()
	{
		super( createSubType() );
	}

	@NotNull
	private static Ref<Type> createSubType()
	{
		return new TaggedTypeImpl( TagEncoding.create( TagMethod.UNKNOWN, TagMethod.IMPLICIT, TagClass.UNIVERSAL, UniversalType.OBJECT_DESCRIPTOR.tagNumber() ), UniversalType.GRAPHIC_STRING.ref() );
	}

	@Override
	public String toString()
	{
		return UniversalType.OBJECT_DESCRIPTOR.typeName().toString();
	}

	@NotNull
	@Override
	public Family getFamily()
	{
		return Family.OBJECT_DESCRIPTOR;
	}

	@NotNull
	@Override
	public Type copy()
	{
		log.warn( "Copying builtin type!" );
		return new ObjectDescriptorType();
	}
}
