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
import rjc.table.data.Date;

/*************************************************************************************************/
/************************************** Date field control ***************************************/
/*************************************************************************************************/

public class DateField extends XTextField
{
  private SimpleIntegerProperty m_epochday; // editor date epoch-day (or most recent valid)
  private DateTimeDropDown          m_dropdown; // drop-down to select date

  /**************************************** constructor ******************************************/
  public DateField()
  {
    // construct field
    m_epochday = new SimpleIntegerProperty();
    setButtonType( ButtonType.DOWN );
    m_dropdown = new DateTimeDropDown( this );

    // react to text changes
    textProperty().addListener( ( property, oldText, newText ) ->
    {
      Utils.trace( oldText, newText );
    } );

    // react to epoch-day changes
    m_epochday.addListener( ( property, oldEpochDay, newEpochDay ) ->
    {
      Utils.trace( oldEpochDay, newEpochDay, getDate().toString() );
      setText( getDate().toString() );
    } );

    // modify date if up or down arrows pressed
    addEventFilter( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setDate( getDate().plusDays( 1 ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setDate( getDate().plusMonths( 1 ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setDate( getDate().plusYears( 1 ) );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        if ( !event.isShiftDown() && !event.isControlDown() )
          setDate( getDate().plusDays( -1 ) );
        if ( event.isShiftDown() && !event.isControlDown() )
          setDate( getDate().plusMonths( -1 ) );
        if ( !event.isShiftDown() && event.isControlDown() )
          setDate( getDate().plusYears( -1 ) );
      }
    } );

    // set initial date
    setDate( Date.now() );
  }

  /****************************************** getDate ********************************************/
  public Date getDate()
  {
    // return editor date (or most recent valid)
    return new Date( m_epochday.get() );
  }

  /****************************************** setDate ********************************************/
  public void setDate( Date date )
  {
    // set epoch-day property, which in turn will update the text
    m_epochday.set( date.getEpochday() );
    positionCaret( getText().length() );
    m_dropdown.setDate( date );
  }

  /**************************************** mouseScroll ******************************************/
  @Override
  public void mouseScroll( ScrollEvent event )
  {
    // increment or decrement date depending on mouse wheel scroll event
    int delta = event.getDeltaY() > 0 ? 1 : -1;
    event.consume();
    if ( !event.isShiftDown() && !event.isControlDown() )
      setDate( getDate().plusDays( delta ) );
    if ( event.isShiftDown() && !event.isControlDown() )
      setDate( getDate().plusMonths( delta ) );
    if ( !event.isShiftDown() && event.isControlDown() )
      setDate( getDate().plusYears( delta ) );
  }

}
