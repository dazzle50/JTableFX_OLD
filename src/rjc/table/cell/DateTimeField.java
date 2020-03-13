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

import javafx.beans.property.SimpleLongProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.Utils;
import rjc.table.data.DateTime;

/*************************************************************************************************/
/************************************ Date-time field control ************************************/
/*************************************************************************************************/

public class DateTimeField extends XTextField
{
  private SimpleLongProperty m_millisecs; // editor date-time milliseconds (or most recent valid)

  /**************************************** constructor ******************************************/
  public DateTimeField()
  {
    // construct field
    m_millisecs = new SimpleLongProperty();
    setButtonType( ButtonType.DOWN );

    // react to text changes
    textProperty().addListener( ( property, oldText, newText ) ->
    {
      Utils.trace( oldText, newText );
    } );

    // react to date-time millisecond changes
    m_millisecs.addListener( ( property, oldMS, newMS ) ->
    {
      Utils.trace( oldMS, newMS, getDateTime().toString() );
      setText( getDateTime().toString() );
    } );

    // modify date-time if up or down arrows pressed
    addEventFilter( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setDateTime( getDateTime().plusDays( 1 ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setDateTime( getDateTime().plusMonths( 1 ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setDateTime( getDateTime().plusYears( 1 ) );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setDateTime( getDateTime().plusDays( -1 ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setDateTime( getDateTime().plusMonths( -1 ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setDateTime( getDateTime().plusYears( -1 ) );
      }
    } );
  }

  /**************************************** getDateTime ******************************************/
  public DateTime getDateTime()
  {
    // return editor date-time (or most recent valid)
    return new DateTime( m_millisecs.get() );
  }

  /**************************************** setDateTime ******************************************/
  public void setDateTime( DateTime datetime )
  {
    // set date-time milliseconds property, which in turn will update the text
    m_millisecs.set( datetime.getMilliseconds() );
    positionCaret( getText().length() );
  }

  /**************************************** mouseScroll ******************************************/
  public void mouseScroll( ScrollEvent event )
  {
    // increment or decrement date-time depending on mouse wheel scroll event
    if ( event.getDeltaY() > 0 )
    {
      event.consume();
      if ( !event.isShiftDown() && !event.isControlDown() )
        setDateTime( getDateTime().plusDays( 1 ) );
      if ( event.isShiftDown() && !event.isControlDown() )
        setDateTime( getDateTime().plusMonths( 1 ) );
      if ( !event.isShiftDown() && event.isControlDown() )
        setDateTime( getDateTime().plusYears( 1 ) );
    }
    else
    {
      event.consume();
      if ( !event.isShiftDown() && !event.isControlDown() )
        setDateTime( getDateTime().plusDays( -1 ) );
      if ( event.isShiftDown() && !event.isControlDown() )
        setDateTime( getDateTime().plusMonths( -1 ) );
      if ( !event.isShiftDown() && event.isControlDown() )
        setDateTime( getDateTime().plusYears( -1 ) );
    }
  }

}
