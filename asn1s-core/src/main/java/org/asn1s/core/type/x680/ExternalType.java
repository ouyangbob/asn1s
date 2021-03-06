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

package org.asn1s.core.type.x680;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.asn1s.api.Ref;
import org.asn1s.api.UniversalType;
import org.asn1s.api.constraint.ConstraintTemplate;
import org.asn1s.api.constraint.Presence;
import org.asn1s.api.encoding.tag.TagEncoding;
import org.asn1s.api.type.Type;
import org.asn1s.core.constraint.template.ComponentConstraintTemplate;
import org.asn1s.core.constraint.template.InnerTypesConstraintTemplate;
import org.asn1s.core.type.AbstractNestingBuiltinType;
import org.asn1s.core.type.ConstrainedType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * X.680, p 37.1
 */
public final class ExternalType extends AbstractNestingBuiltinType
{
	private static final Log log = LogFactory.getLog( ExternalType.class );

	public ExternalType()
	{
		super( createSubType() );
		setEncoding( TagEncoding.universal( UniversalType.EXTERNAL ) );
	}

	@NotNull
	@Override
	public Family getFamily()
	{
		return Family.EXTERNAL;
	}

	@Override
	public String toString()
	{
		return UniversalType.EXTERNAL.typeName().toString();
	}

	@NotNull
	@Override
	public Type copy()
	{
		log.warn( "Copying builtin type!" );
		return new ExternalType();
	}

	private static Ref<Type> createSubType()
	{
		List<ConstraintTemplate> templates = Arrays.asList(
				new ComponentConstraintTemplate( "syntaxes", null, Presence.ABSENT ),
				new ComponentConstraintTemplate( "transfer-syntax", null, Presence.ABSENT ),
				new ComponentConstraintTemplate( "fixed", null, Presence.ABSENT ) );

		InnerTypesConstraintTemplate identificationTypeConstraintsTemplate = new InnerTypesConstraintTemplate( templates, true );
		ConstraintTemplate identificationConstraintTemplate = new ComponentConstraintTemplate( "identification", identificationTypeConstraintsTemplate, Presence.PRESENT );
		ConstraintTemplate template = new InnerTypesConstraintTemplate( Collections.singletonList( identificationConstraintTemplate ), true );
		return new ConstrainedType( template, EmbeddedPdvType.createSequenceType() );
	}
}
