/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

package rjc.table.cell;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.Utils;
import rjc.table.data.Time;

/*************************************************************************************************/
/************************************** Time field control ***************************************/
/*************************************************************************************************/

public class TimeField extends XTextField
{
  private SimpleIntegerProperty m_millisec; // editor time milliseconds (or most recent valid)

  /**************************************** constructor ******************************************/
  public TimeField()
  {
    // construct field
    m_millisec = new SimpleIntegerProperty();
    setButtonType( ButtonType.DOWN );

    // react to text changes
    textProperty().addListener( ( property, oldText, newText ) ->
    {
      Utils.trace( oldText, newText );
    } );

    // react to millisecond changes
    m_millisec.addListener( ( property, oldMS, newMS ) ->
    {
      Utils.trace( oldMS, newMS, getTime().toString() );
      setText( getTime().toString() );
    } );

    // modify time if up or down arrows pressed
    addEventFilter( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setTime( getTime().addMilliseconds( Time.ONE_HOUR ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setTime( getTime().addMilliseconds( Time.ONE_MINUTE ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setTime( getTime().addMilliseconds( Time.ONE_SECOND ) );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setTime( getTime().addMilliseconds( -Time.ONE_HOUR ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setTime( getTime().addMilliseconds( -Time.ONE_MINUTE ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setTime( getTime().addMilliseconds( -Time.ONE_SECOND ) );
      }
    } );
  }

  /****************************************** getTime ********************************************/
  public Time getTime()
  {
    // return editor time (or most recent valid)
    return Time.fromMilliseconds( m_millisec.get() );
  }

  /****************************************** setTime ********************************************/
  public void setTime( Time time )
  {
    // set milliseconds property, which in turn will update the text
    m_millisec.set( time.getDayMilliseconds() );
    positionCaret( getText().length() );
  }

  /**************************************** mouseScroll ******************************************/
  public void mouseScroll( ScrollEvent event )
  {
    // increment or decrement time depending on mouse wheel scroll event
    int ms = 0;
    event.consume();
    if ( !event.isShiftDown() && !event.isControlDown() )
      ms = Time.ONE_HOUR;
    if ( event.isShiftDown() && !event.isControlDown() )
      ms = Time.ONE_MINUTE;
    if ( !event.isShiftDown() && event.isControlDown() )
      ms = Time.ONE_SECOND;

    setTime( getTime().addMilliseconds( event.getDeltaY() > 0 ? ms : -ms ) );
  }

}
