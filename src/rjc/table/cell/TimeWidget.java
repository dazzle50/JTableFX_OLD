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

import javafx.scene.layout.HBox;

/*************************************************************************************************/
/******************* Number spin fields for Hour/Minutes/Seconds/Milliseconds ********************/
/*************************************************************************************************/

public class TimeWidget extends HBox
{
  private NumberSpinField  m_hours     = new NumberSpinField();
  private NumberSpinField  m_mins      = new NumberSpinField();
  private NumberSpinField  m_secs      = new NumberSpinField();
  private NumberSpinField  m_millisecs = new NumberSpinField();

  private static final int BORDER      = 4;

  /**************************************** constructor ******************************************/
  public TimeWidget( double width )
  {
    // create layout with the four number spin fields
    width = width - 3 * BORDER;
    m_hours.setMaxWidth( width * 0.24 );
    m_hours.setFormat( "00", 0 );
    m_hours.setRange( 0, 23 );
    m_hours.setStepPage( 1, 6 );

    m_mins.setMaxWidth( width * 0.24 );
    m_mins.setFormat( "00", 0 );
    m_mins.setRange( 0, 59 );
    m_mins.setWrapField( m_hours );

    m_secs.setMaxWidth( width * 0.24 );
    m_secs.setFormat( "00", 0 );
    m_secs.setRange( 0, 59 );
    m_secs.setWrapField( m_mins );

    m_millisecs.setMaxWidth( 1 + width - m_hours.getMaxWidth() - m_mins.getMaxWidth() - m_secs.getMaxWidth() );
    m_millisecs.setFormat( "000", 0 );
    m_millisecs.setRange( 0, 999 );
    m_millisecs.setStepPage( 1, 100 );
    m_millisecs.setWrapField( m_secs );

    setSpacing( BORDER - 1 );
    getChildren().addAll( m_hours, m_mins, m_secs, m_millisecs );

    m_hours.setValue( 5 );
    m_mins.setValue( 5 );
    m_secs.setValue( 5 );
    m_millisecs.setValue( 5 );
  }
}
