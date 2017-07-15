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

package org.asn1s.core.constraint;

import org.asn1s.api.Module;
import org.asn1s.api.ObjectFactory;
import org.asn1s.api.Ref;
import org.asn1s.api.Scope;
import org.asn1s.api.constraint.ConstraintTemplate;
import org.asn1s.api.constraint.Presence;
import org.asn1s.api.type.CollectionType;
import org.asn1s.api.type.ComponentType.Kind;
import org.asn1s.api.value.Value;
import org.asn1s.api.value.x680.ValueCollection;
import org.asn1s.core.DefaultObjectFactory;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InnerTypesConstraintTest
{
	@Test
	public void validate() throws Exception
	{
		ObjectFactory factory = new DefaultObjectFactory();
		Module module = factory.dummyModule();

		CollectionType sequenceType = factory.collection( CollectionType.Kind.Sequence );
		sequenceType.setExtensible( true );
		sequenceType.addComponent( Kind.Primary, "a", factory.builtin( "INTEGER" ), true, null );
		sequenceType.addComponent( Kind.Primary, "b", factory.builtin( "REAL" ), false, null );

		factory.define( "My-Type", sequenceType, null );
		module.validate();

		Ref<Value> realValueRef = factory.real( 0.0f );
		ConstraintTemplate constraint = factory.innerTypes(
				Collections.singletonList( factory.component( "b", factory.value( realValueRef ), Presence.Present ) ),
				false );

		ValueCollection value = factory.collection( true );
		value.addNamed( "b", realValueRef );

		Scope scope = module.createScope();
		assertTrue( "Constraint failure", ConstraintTestUtils.checkConstraint( constraint, value, sequenceType, scope ) );

		ValueCollection value1 = factory.collection( true );
		value1.addNamed( "a", factory.integer( 1 ) );
		value1.addNamed( "b", realValueRef );
		assertFalse( "Constraint not failed", ConstraintTestUtils.checkConstraint( constraint, value1, sequenceType, scope ) );
	}
}
