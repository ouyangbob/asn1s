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

package org.asn1s.core.type;

import org.asn1s.api.Ref;
import org.asn1s.api.module.Module;
import org.asn1s.api.type.CollectionType;
import org.asn1s.api.type.ComponentType.Kind;
import org.asn1s.api.type.DefinedType;
import org.asn1s.api.type.Type;
import org.asn1s.api.type.Type.Family;
import org.asn1s.api.type.TypeFactory;
import org.junit.Assert;
import org.junit.Test;

public class SetInterpolationTest
{
	@Test
	public void testInterpolation() throws Exception
	{
		TypeFactory factory = new CoreTypeFactory();
		Module module = factory.dummyModule();

		Ref<Type> intRef = factory.builtin( "INTEGER" );

		CollectionType set = factory.collection( Family.SET );
		set.addComponent( Kind.PRIMARY, "a", intRef );
		set.addComponent( Kind.PRIMARY, "b", intRef ).setOptional( true );
		set.addComponent( Kind.EXTENSION, "c", intRef );
		set.addComponent( Kind.SECONDARY, "d", intRef );

		DefinedType type = factory.define( "MySet", set, null );

		module.validate();
		Assert.assertTrue( "Type is not validated", type.isValidated() );
	}
}
