/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
 *  https://github.com/dazzle50/JTableFX                                  *
 *                                                                        *
 *  This program is free software: you can redistribute it and/or modify  *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  This program is distributed in the hope that it will be useful,       *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with this program.  If not, see http://www.gnu.org/licenses/    *
 **************************************************************************/

package rjc.table;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Control;

/*************************************************************************************************/
/******************* Miscellaneous utility public static methods and variables *******************/
/*************************************************************************************************/

public class Utils
{
  public static final String      VERSION            = "v0.0.2-alpha WIP";

  public static final String      STYLE_ERROR        = "-fx-text-fill: red;";
  public static final String      STYLE_NORMAL       = "-fx-text-fill: black;";

  public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss.SSS" );

  /****************************************** timestamp ******************************************/
  public static String timestamp()
  {
    // returns current date-time as string in format YYYY-MM-DD HH:MM:SS.SSS
    return LocalDateTime.now().format( timestampFormatter );
  }

  /******************************************** trace ********************************************/
  public static void trace( Object... objects )
  {
    // sends to standard out date-time, the input objects, suffixed by file+line-number & method
    System.out.println( timestamp() + " " + objectsString( objects ) + caller( 1 ) );
  }

  /********************************************* path ********************************************/
  public static void path( Object... objects )
  {
    // sends to standard out date-time, the input objects, suffixed by file+line-number & method x5
    System.out.println( timestamp() + " " + objectsString( objects ) + caller( 1 ) + " " + caller( 2 ) + " "
        + caller( 3 ) + " " + caller( 4 ) + " " + caller( 5 ) );
  }

  /******************************************* stack *********************************************/

  public static void stack( Object... objects )
  {
    // sends to standard out date-time and the input objects
    System.out.println( timestamp() + " " + objectsString( objects ) );

    // sends to standard out this thread's stack trace
    StackTraceElement[] stack = new Throwable().getStackTrace();
    for ( int i = 1; i < stack.length; i++ )
      System.out.println( "\t" + stackElementString( stack[i] ) );
  }

  /******************************************* caller *******************************************/
  public static String caller( int pos )
  {
    // returns stack entry at specified position
    StackTraceElement[] stack = new Throwable().getStackTrace();
    String method = stack[++pos].getMethodName() + "()";
    String file = "(" + stack[pos].getFileName() + ":" + stack[pos].getLineNumber() + ") ";
    return file + method;
  }

  /**************************************** objectsString ****************************************/
  public static StringBuilder objectsString( Object... objects )
  {
    // converts objects to space separated string
    StringBuilder str = new StringBuilder();
    for ( Object obj : objects )
    {
      if ( obj == null )
        str.append( "null " );
      else if ( obj instanceof String )
        str.append( "\"" + obj + "\" " );
      else if ( obj instanceof Character )
        str.append( "'" + obj + "' " );
      else
        str.append( obj + " " );
    }

    return str;
  }

  /************************************* stackElementString **************************************/
  public static String stackElementString( StackTraceElement element )
  {
    // cannot simply use stack[i].toString() because need extra space before bracket for Eclipse hyperlink to work 
    return element.getClassName() + "." + element.getMethodName()
        + ( element.isNativeMethod() ? " (Native Method)"
            : ( element.getFileName() != null && element.getLineNumber() >= 0
                ? " (" + element.getFileName() + ":" + element.getLineNumber() + ")"
                : ( element.getFileName() != null ? " (" + element.getFileName() + ")" : " (Unknown Source)" ) ) );
  }

  /******************************************* clean *********************************************/
  public static String clean( String txt )
  {
    // returns a clean string
    return txt.trim().replaceAll( "(\\s+)", " " );
  }

  /******************************************* equal *********************************************/
  public static boolean equal( Object obj1, Object obj2 )
  {
    // returns true if obj1 equals obj2 even if both are null
    if ( obj1 == obj2 )
      return true;
    if ( obj1 != null )
      return obj1.equals( obj2 );
    return obj2.equals( obj1 );
  }

  /****************************************** setError *******************************************/
  public static void setError( Control control )
  {
    // set error state
    control.setId( STYLE_ERROR );
    control.setStyle( STYLE_ERROR );
  }

  /***************************************** setNoError ******************************************/
  public static void setNoError( Control control )
  {
    // remove error state
    control.setId( null );
    control.setStyle( STYLE_NORMAL );
  }

  /******************************************* isError *******************************************/
  public static Boolean isError( Control control )
  {
    // return if control in error state
    return control == null || control.getId() == STYLE_ERROR;
  }

  /******************************************** clamp ********************************************/
  public static int clamp( int val, int min, int max )
  {
    // return integer clamped between supplied min and max
    return Math.max( min, Math.min( max, val ) );
  }

}
