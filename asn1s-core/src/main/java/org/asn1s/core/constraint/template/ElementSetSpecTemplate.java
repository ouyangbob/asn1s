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

package org.asn1s.core.constraint.template;

import org.asn1s.api.Scope;
import org.asn1s.api.constraint.Constraint;
import org.asn1s.api.constraint.ConstraintTemplate;
import org.asn1s.api.exception.ResolutionException;
import org.asn1s.api.exception.ValidationException;
import org.asn1s.api.type.Type;
import org.asn1s.core.constraint.ElementSetSpec;
import org.asn1s.core.constraint.Exclusion;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ElementSetSpecTemplate implements ConstraintTemplate
{
	public ElementSetSpecTemplate( @NotNull List<ConstraintTemplate> unions )
	{
		this.unions = new ArrayList<>( unions );
		exclusion = null;
	}

	public ElementSetSpecTemplate( @NotNull ConstraintTemplate exclusion )
	{
		unions = null;
		this.exclusion = exclusion;
	}

	private final List<ConstraintTemplate> unions;
	private final ConstraintTemplate exclusion;

	@Override
	public Constraint build( @NotNull Scope scope, @NotNull Type type ) throws ResolutionException, ValidationException
	{
		if( unions != null )
		{
			if( unions.isEmpty() )
				throw new ValidationException( "Unable to make ElementSetSpec with zero unions" );
			List<Constraint> list = new ArrayList<>( unions.size() );
			for( ConstraintTemplate union : unions )
				list.add( union.build( scope, type ) );

			if( list.size() == 1 )
				return list.get( 0 );
			return new ElementSetSpec( list );
		}

		if( exclusion != null )
			return new Exclusion( exclusion.build( scope, type ) );

		// this should never happen
		throw new IllegalStateException();
	}
}
