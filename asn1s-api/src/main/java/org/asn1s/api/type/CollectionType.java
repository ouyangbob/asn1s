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

package org.asn1s.api.type;

import org.asn1s.api.exception.IllegalValueException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface CollectionType extends Type, ComponentTypeConsumer
{
	void setExtensible( boolean value );

	boolean isExtensible();

	/**
	 * Find component by name, extensions ignored
	 *
	 * @param name component name
	 * @return ComponentType or null
	 */
	@Nullable
	@Override
	default ComponentType getNamedType( @NotNull String name )
	{
		return getComponent( name, false );
	}

	/**
	 * Returns list of components without extensions
	 *
	 * @return list of components
	 * @see #getComponents(boolean)
	 */
	@NotNull
	@Override
	default List<ComponentType> getNamedTypes()
	{
		return getComponents( false );
	}

	int getExtensionIndexStart();

	int getExtensionIndexEnd();

	int getMaxVersion();

	/**
	 * Find component by name
	 *
	 * @param name           component name
	 * @param withExtensions if extensions should be used
	 * @return ComponentType or null
	 * @see #getNamedType(String)
	 */
	@Nullable
	ComponentType getComponent( @NotNull String name, boolean withExtensions );

	/**
	 * Return list of components
	 *
	 * @param withExtensions if true - extensions must be included
	 * @return list of components
	 * @see #getNamedTypes()
	 */
	List<ComponentType> getComponents( boolean withExtensions );

	boolean isAllComponentsOptional();

	Kind getKind();

	default void assertComponentsOptionalityInRange( int start, int endBound, int version ) throws IllegalValueException
	{
		throw new UnsupportedOperationException();
	}

	default boolean isInstanceOf()
	{
		return false;
	}

	enum Kind
	{
		Sequence,
		SequenceOf,
		Set,
		SetOf,
		Choice
	}
}
