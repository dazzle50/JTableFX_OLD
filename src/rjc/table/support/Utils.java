/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

package rjc.table.support;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Control;

/*************************************************************************************************/
/******************* Miscellaneous utility public static methods and variables *******************/
/*************************************************************************************************/

public class Utils
{
  public static final String      VERSION            = "v0.0.1-alpha WIP";

  public static final String      STYLE_ERROR        = "-fx-text-fill: red;";
  public static final String      STYLE_NORMAL       = "-fx-text-fill: black;";

  public static DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern( "uuuu-MM-dd HH:mm:ss.SSS" );

  /***************************************** timestamp *****************************************/
  public static String timestamp()
  {
    // returns current date-time as string in format YYYY-MM-DD HH:MM:SS.SSS
    return LocalDateTime.now().format( timestampFormatter );
  }

  /******************************************* trace *********************************************/
  public static void trace( Object... objects )
  {
    // prints space separated objects in string representation prefixed by date-time
    // and suffixed by file+line-number & method
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

    System.out.println( timestamp() + " " + str.toString() + caller( 1 ) );
  }

  /******************************************* stack *********************************************/
  public static void stack( Object... objects )
  {
    // prints space separated objects in string representation prefixed by date-time
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
    System.out.println( timestamp() + " " + str.toString() );

    // prints stack
    StackTraceElement[] stack = new Throwable().getStackTrace();
    for ( int i = 1; i < stack.length; i++ )
    {
      // cannot simply use stack[i].toString() because need extra space before bracket for Eclipse hyperlink to work
      String txt = stack[i].getClassName() + "." + stack[i].getMethodName()
          + ( stack[i].isNativeMethod() ? " (Native Method)"
              : ( stack[i].getFileName() != null && stack[i].getLineNumber() >= 0
                  ? " (" + stack[i].getFileName() + ":" + stack[i].getLineNumber() + ")"
                  : ( stack[i].getFileName() != null ? " (" + stack[i].getFileName() + ")" : " (Unknown Source)" ) ) );
      System.out.println( "\t" + txt );
    }
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

}
