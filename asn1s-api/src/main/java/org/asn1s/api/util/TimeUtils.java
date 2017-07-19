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

package org.asn1s.api.util;

import org.asn1s.api.UniversalType;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.regex.Pattern;

@SuppressWarnings( "NumericCastThatLosesPrecision" )
public final class TimeUtils
{
	public static final String UTC_TIME_FORMAT = "yyMMddHHmmss";
	public static final String GENERALIZED_TIME_FORMAT = "yyyyMMddHHmmss.SSS";
	@SuppressWarnings( "ConstantConditions" )
	@NotNull
	public static final Charset CHARSET = UniversalType.VisibleString.charset();

	private TimeUtils()
	{
	}

	@NotNull
	public static String formatInstant( TemporalAccessor instant, String format, boolean optimize )
	{
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern( format )
				.withZone( ZoneId.of( "GMT" ) );
		String result = formatter.format( instant );
		if( result.indexOf( '.' ) > 0 )
		{
			if( result.endsWith( ".000" ) )
				result = result.substring( 0, result.length() - 4 );
			else if( result.endsWith( "00" ) )
				result = result.substring( 0, result.length() - 2 );
			else if( result.endsWith( "0" ) )
				result = result.substring( 0, result.length() - 1 );
		}

		if( optimize && result.endsWith( "00" ) )
			result = result.substring( 0, result.length() - 2 );
		// now we have to manually add the 'UTC' timezone identifier
		result += "Z";
		return result;
	}

	public static boolean isTimeValue( CharSequence value )
	{
		return isUTCTimeValue( value )
				|| isGeneralizedTimeValue( value )
				|| TIME_PATTERN.matcher( value ).matches()
				|| DATE_PATTERN.matcher( value ).matches();
	}

	public static boolean isUTCTimeValue( CharSequence value )
	{
		return UTC_PATTERN.matcher( value ).matches();
	}

	public static boolean isGeneralizedTimeValue( CharSequence value )
	{
		return G_PATTERN.matcher( value ).matches();
	}

	public static Instant parseGeneralizedTime( String value )
	{
		if( !isGeneralizedTimeValue( value ) )
			throw new IllegalArgumentException( "Not an GeneralizedTime string: " + value );
		return fromGeneralized( value ).atZone( ZoneId.of( "GMT" ) ).toInstant();
	}

	public static Instant parseUTCTime( String value )
	{
		if( !isUTCTimeValue( value ) )
			throw new IllegalArgumentException( "Not an UTCTime string: " + value );
		return fromUTC( value ).atZone( ZoneId.of( "GMT" ) ).toInstant();
	}

	public static Instant parseUnknownTime( String value )
	{
		value = value.replace( ',', '.' );
		if( isUTCTimeValue( value ) )
			return fromUTC( value );

		if( isGeneralizedTimeValue( value ) )
			return fromGeneralized( value );

		if( TIME_PATTERN.matcher( value ).matches() )
			return DateTimeFormatter.ofPattern( value.indexOf( ':' ) == -1 ? TIME_FORMAT : TIME_FORMAT_COLONS ).parse( value, Instant:: from );

		if( DATE_PATTERN.matcher( value ).matches() )
			return DateTimeFormatter.ofPattern( DATE_FORMAT ).parse( value, Instant:: from );

		throw new IllegalArgumentException( "Not an time value: " + value );
	}

	private static Instant fromUTC( String value )
	{
		if( value.endsWith( "Z" ) || value.indexOf( '-' ) == -1 && value.indexOf( '+' ) == -1 )
		{
			value = value.endsWith( "Z" ) ? value.substring( 0, value.length() - 1 ) : value;
			if( value.length() == 10 )
				return DateTimeFormatter.ofPattern( UTC_FORMAT ).withZone( ZoneId.of( "GMT" ) ).parse( value, Instant:: from );

			return DateTimeFormatter.ofPattern( UTC_FORMAT_S ).withZone( ZoneId.of( "GMT" ) ).parse( value, Instant:: from );
		}
		else
		{
			int idx = value.replace( '-', '+' ).indexOf( '+' );
			if( idx == 10 )
				return DateTimeFormatter.ofPattern( UTC_FORMAT_TZ ).parse( value, Instant:: from );

			return DateTimeFormatter.ofPattern( UTC_FORMAT_S_TZ ).parse( value, Instant:: from );
		}
	}

	private static Instant fromGeneralized( String value )
	{
		if( value.indexOf( '.' ) == -1 )
			return parseGeneralizedImpl( value );

		int idx = value.indexOf( '.' );
		int end = idx + 1;
		char c = value.charAt( end );
		while( Character.isDigit( c ) )
		{
			end++;
			if( value.length() == end )
				break;
			c = value.charAt( end );
		}

		String noFracture = value.length() == end ? value.substring( 0, idx ) : value.substring( 0, idx ) + value.substring( end );
		String fracturePart = value.substring( idx + 1, end );
		//noinspection MagicNumber
		double fracture = Long.parseLong( fracturePart ) / StrictMath.pow( 10.0d, fracturePart.length() );
		Instant instant = parseGeneralizedImpl( noFracture );
		if( idx == 10 )
			// no minutes, no seconds
			instant = appendFractureMinutes( instant, fracture );
		else //noinspection MagicNumber
			if( idx == 12 )
			{
				// no seconds
				instant = appendFractureSeconds( instant, fracture );
			}
			else
				instant = appendFractureMillis( instant, fracture );

		return instant;

	}

	private static Instant appendFractureMinutes( Instant instant, double fracture )
	{
		double rawMinutes = MINUTES_IN_HOUR * fracture;
		long minutes = (long)rawMinutes;
		if( minutes != 0 )
			instant = instant.plus( minutes, ChronoUnit.MINUTES );
		return appendFractureSeconds( instant, fraction( rawMinutes ) );
	}

	private static Instant appendFractureSeconds( Instant instant, double fraction )
	{
		double rawSeconds = SECONDS_IN_MINUTE * fraction;
		long seconds = (long)rawSeconds;
		if( seconds != 0 )
			instant = instant.plus( seconds, ChronoUnit.SECONDS );
		return appendFractureMillis( instant, fraction( rawSeconds ) );
	}

	private static Instant appendFractureMillis( Instant instant, double fraction )
	{
		long millis = (long)( MILLIS_IN_SECOND * fraction );
		if( millis != 0 )
			return instant.plus( millis, ChronoUnit.MILLIS );
		return instant;
	}

	private static double fraction( double value )
	{
		return value % 1;
	}

	private static Instant parseGeneralizedImpl( String value )
	{
		if( value.endsWith( "Z" ) )
		{
			value = value.substring( 0, value.length() - 1 );
			return generalizedToInstant( value, value.length(), false );
		}

		int idx = value.replace( '-', '+' ).indexOf( '+' );
		if( idx == -1 )
			return generalizedToInstant( value, value.length(), false );
		return generalizedToInstant( value, idx, true );
	}

	private static Instant generalizedToInstant( CharSequence value, int length, boolean withTz )
	{
		DateTimeFormatter formatter;
		if( length == 10 )
			formatter = DateTimeFormatter.ofPattern( withTz ? G_FORMAT + 'Z' : G_FORMAT );
		else
			//noinspection MagicNumber
			if( length == 12 )
				formatter = DateTimeFormatter.ofPattern( withTz ? G_FORMAT_M + 'Z' : G_FORMAT_M );
			else
				formatter = DateTimeFormatter.ofPattern( withTz ? G_FORMAT_M_S + 'Z' : G_FORMAT_M_S );

		if( !withTz )
			formatter = formatter.withZone( ZoneId.of( "GMT" ) );

		return formatter.parse( value, Instant:: from );
	}

	private static final String UTC_FORMAT = "yyMMddHHmm";
	private static final String UTC_FORMAT_TZ = "yyMMddHHmmZ";
	private static final String UTC_FORMAT_S = "yyMMddHHmmss";
	private static final String UTC_FORMAT_S_TZ = "yyMMddHHmmssZ";
	private static final Pattern UTC_PATTERN = Pattern.compile( "[0-9]{2}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])?(Z|[+\\-]([0-1][0-9]|2[0-3])([0-5][0-9]))?" );

	private static final String G_FORMAT = "yyyyMMddHH";
	private static final String G_FORMAT_M = "yyyyMMddHHmm";
	private static final String G_FORMAT_M_S = "yyyyMMddHHmmss";
	private static final Pattern G_PATTERN = Pattern.compile( "[0-9]{4}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])([0-1][0-9]|2[0-3])(([0-5][0-9])([0-5][0-9])?)?([.,][0-9]+)?(Z|[+\\-]([0-1][0-9]|2[0-3])([0-5][0-9]))?" );

	private static final String TIME_FORMAT = "HHmmss";
	private static final String TIME_FORMAT_COLONS = "HH:mm:ss";
	private static final Pattern TIME_PATTERN = Pattern.compile( "(([0-1][0-9]|2[0-3])([0-5][0-9])([0-5][0-9])|([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9]))" );

	private static final String DATE_FORMAT = "yyyyMMdd";
	private static final Pattern DATE_PATTERN = Pattern.compile( "[0-9]{4}(0[0-9]|1[0-2])([0-2][0-9]|3[0-1])" );

	private static final int SECONDS_IN_MINUTE = 60;
	private static final int MILLIS_IN_SECOND = 1000;
	private static final int MINUTES_IN_HOUR = 60;
}
