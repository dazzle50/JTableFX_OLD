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

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import javafx.scene.input.KeyEvent;

/*************************************************************************************************/
/******************************** Spin control for picking month *********************************/
/*************************************************************************************************/

public class MonthSpinField extends SpinField
{

  /**************************************** constructor ******************************************/
  public MonthSpinField()
  {
    // set default spin field characteristics
    setEditable( false );
    setRange( 1.0, 12.0 );
    setStepPage( 1.0, 3.0 );

    // react to key typed
    setOnKeyTyped( event -> keyTyped( event ) );
  }

  /****************************************** setField *******************************************/
  @Override
  protected void setField( Object value )
  {
    // set field text to month display name
    try
    {
      super.setField( getDisplayName( (int) getValue() ) );
    }
    catch ( Exception e )
    {
      super.setField( e.toString() );
    }
  }

  /****************************************** getMonth *******************************************/
  public Month getMonth()
  {
    // get month index number as integer
    return Month.of( (int) getValue() );
  }

  /************************************** getDisplayName ****************************************/
  public String getDisplayName( int index )
  {
    // get month display name for specified index
    return Month.of( index ).getDisplayName( TextStyle.FULL, Locale.getDefault() );
  }

  /****************************************** keyTyped *******************************************/
  public void keyTyped( KeyEvent event )
  {
    // find next month that starts with typed key (case-insensitive)
    String key = event.getCharacter().toLowerCase();
    for ( int delta = 0; delta < 11; delta++ )
    {
      int index = ( (int) getValue() + delta ) % 12 + 1;
      if ( getDisplayName( index ).toLowerCase().startsWith( key ) )
      {
        setValue( index );
        return;
      }
    }
  }

}
