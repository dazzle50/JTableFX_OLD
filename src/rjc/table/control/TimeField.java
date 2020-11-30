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

package rjc.table.control;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.Status.Level;
import rjc.table.data.Time;
import rjc.table.signal.ISignal;

/*************************************************************************************************/
/************************************** Time field control ***************************************/
/*************************************************************************************************/

public class TimeField extends XTextField implements ISignal
{
  private Time m_time; // field current time (or most recent valid)

  /**************************************** constructor ******************************************/
  public TimeField()
  {
    // construct field
    setButtonType( ButtonType.DOWN );
    new DateTimeDropDown( this );

    // react to text changes, for example user typing new time
    textProperty().addListener( ( property, oldText, newText ) ->
    {
      try
      {
        // if no exception raised and time is different send signal (but don't update text)
        Time time = Time.fromString( newText );
        if ( !time.equals( m_time ) )
        {
          m_time = time;
          signal( time );
        }

        getStatus().update( Level.NORMAL, "Time: " + format( time ) );
        setStyle( getStatus().getStyle() );
      }
      catch ( Exception exception )
      {
        getStatus().update( Level.ERROR, "Time is not valid" );
        setStyle( getStatus().getStyle() );
      }
    } );

    // react to focus change to ensure text shows time in correct format
    focusedProperty().addListener( ( property, oldF, newF ) ->
    {
      setText( format( m_time ) );
      positionCaret( getText().length() );
      getStatus().update( Level.NORMAL, null );
    } );

    // react to key presses
    addEventFilter( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        step( 1, event.isShiftDown(), event.isControlDown() );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        step( -1, event.isShiftDown(), event.isControlDown() );
      }

      if ( event.getCode() == KeyCode.ENTER )
      {
        setText( format( m_time ) );
        positionCaret( getText().length() );
      }
    } );

    // set initial time truncated to hour
    setTime( Time.fromHours( Time.now().getHours() ) );
  }

  /****************************************** getTime ********************************************/
  public Time getTime()
  {
    // return field time (or most recent valid)
    return m_time;
  }

  /****************************************** setTime ********************************************/
  public void setTime( Time time )
  {
    // set current field time, display in text, signal change
    if ( !time.equals( m_time ) )
    {
      m_time = time;
      setText( format( time ) );
      positionCaret( getText().length() );
      signal( time );
    }
  }

  /******************************************* format ********************************************/
  public String format( Time time )
  {
    // return time in display format
    return time.toString();
  }

  /******************************************** step ********************************************/
  public void step( int delta, boolean shift, boolean ctrl )
  {
    // modify field value
    if ( !shift && !ctrl )
      m_time.addMilliseconds( -Time.ONE_HOUR );
    if ( shift && !ctrl )
      m_time.addMilliseconds( -Time.ONE_MINUTE );
    if ( !shift && ctrl )
      m_time.addMilliseconds( -Time.ONE_SECOND );

    // display in text and signal change
    setText( format( m_time ) );
    positionCaret( getText().length() );
    signal( m_time );
  }

  /**************************************** mouseScroll ******************************************/
  @Override
  public void mouseScroll( ScrollEvent event )
  {
    // increment or decrement time depending on mouse wheel scroll event
    int delta = event.getDeltaY() > 0 ? 1 : -1;
    event.consume();
    step( delta, event.isShiftDown(), event.isControlDown() );
  }

}
