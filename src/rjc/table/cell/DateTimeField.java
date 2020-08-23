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

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.data.DateTime;
import rjc.table.signal.ISignal;

/*************************************************************************************************/
/************************************ Date-time field control ************************************/
/*************************************************************************************************/

public class DateTimeField extends XTextField implements ISignal
{
  private DateTime m_datetime; // field current date-time (or most recent valid)

  /**************************************** constructor ******************************************/
  public DateTimeField()
  {
    // construct field
    setButtonType( ButtonType.DOWN );
    new DateTimeDropDown( this );

    // modify date-time if up or down arrows pressed
    addEventFilter( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        delta( 1, event.isShiftDown(), event.isControlDown() );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        delta( -1, event.isShiftDown(), event.isControlDown() );
      }
    } );

    // set default date-time to now truncated to hour
    long now = DateTime.now().getMilliseconds() / 3600000L;
    setDateTime( new DateTime( now * 3600000L ) );
  }

  /**************************************** getDateTime ******************************************/
  public DateTime getDateTime()
  {
    // return field date-time (or most recent valid)
    return m_datetime;
  }

  /**************************************** setDateTime ******************************************/
  public void setDateTime( DateTime datetime )
  {
    // set current field date, display in text, signal change
    if ( !datetime.equals( m_datetime ) )
    {
      m_datetime = datetime;
      setText( format( datetime ) );
      positionCaret( getText().length() );
      signal( datetime );
    }
  }

  /******************************************* format ********************************************/
  public String format( DateTime datetime )
  {
    // return date-time in display format
    return datetime.toString();
  }

  /******************************************** delta ********************************************/
  private void delta( int delta, boolean shift, boolean ctrl )
  {
    // modify field date
    if ( !shift && !ctrl )
      setDateTime( getDateTime().plusDays( delta ) );
    if ( shift && !ctrl )
      setDateTime( getDateTime().plusMonths( delta ) );
    if ( !shift && ctrl )
      setDateTime( getDateTime().plusYears( delta ) );
  }

  /**************************************** mouseScroll ******************************************/
  public void mouseScroll( ScrollEvent event )
  {
    // increment or decrement date-time depending on mouse wheel scroll event
    int delta = event.getDeltaY() > 0 ? 1 : -1;
    event.consume();
    delta( delta, event.isShiftDown(), event.isControlDown() );
  }

}
