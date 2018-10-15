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

package rjc.table.view;

import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;

/*************************************************************************************************/
/*************** Extended version of ScrollBar with special increment & decrement ****************/
/*************************************************************************************************/

public class TableScrollBar extends ScrollBar
{
  private TableView m_view;   // associated table view

  public static int SIZE = 18;

  /**************************************** constructor ******************************************/
  public TableScrollBar( TableView view, Orientation orientation )
  {
    // create scroll bar
    m_view = view;
    setOrientation( orientation );

    // set width/height
    if ( orientation == Orientation.VERTICAL )
    {
      setMinWidth( SIZE );
      setMinHeight( USE_PREF_SIZE );
      setMaxHeight( USE_PREF_SIZE );
    }
    else
    {
      setMinWidth( USE_PREF_SIZE );
      setMaxWidth( USE_PREF_SIZE );
      setMinHeight( SIZE );
    }

    // redraw table when scroll bar position value changes
    valueProperty().addListener( ( observable, oldV, newV ) -> m_view.redraw() );
  }

  /****************************************** increment ******************************************/
  @Override
  public void increment()
  {
    // increase scroll bar value to next table cell boundary
    if ( getOrientation() == Orientation.VERTICAL )
    {
      int rowPos = m_view.getRowPositionAtY( m_view.getColumnHeaderHeight() );
      int y = m_view.getRowPositionYStart( ++rowPos );
      while ( y < m_view.getColumnHeaderHeight() )
        y = m_view.getRowPositionYStart( ++rowPos );
      double value = getValue() + y - m_view.getColumnHeaderHeight();
      m_view.animate( valueProperty(), (int) ( value < getMax() ? value : getMax() ), 100 );
    }
    else
    {
      int columnPos = m_view.getColumnPositionAtX( m_view.getRowHeaderWidth() );
      int x = m_view.getColumnPositionXStart( ++columnPos );
      while ( x < m_view.getColumnHeaderHeight() )
        x = m_view.getColumnPositionXStart( ++columnPos );
      double value = getValue() + x - m_view.getRowHeaderWidth();
      m_view.animate( valueProperty(), (int) ( value < getMax() ? value : getMax() ), 100 );
    }
  }

  /****************************************** decrement ******************************************/
  @Override
  public void decrement()
  {
    // decrease scroll bar value to next table cell boundary
    if ( getOrientation() == Orientation.VERTICAL )
    {
      int rowPos = m_view.getRowPositionAtY( m_view.getColumnHeaderHeight() - 1 );
      if ( rowPos >= 0 )
      {
        int y = m_view.getRowPositionYStart( rowPos );
        double value = getValue() + y - m_view.getColumnHeaderHeight();
        m_view.animate( valueProperty(), (int) ( value > 0.0 ? value : 0 ), 100 );
      }
    }
    else
    {
      int columnPos = m_view.getColumnPositionAtX( m_view.getRowHeaderWidth() - 1 );
      if ( columnPos >= 0 )
      {
        int x = m_view.getColumnPositionXStart( columnPos );
        double value = getValue() + x - m_view.getRowHeaderWidth();
        m_view.animate( valueProperty(), (int) ( value > 0.0 ? value : 0 ), 100 );
      }
    }
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    return "VAL=" + getValue() + " MIN=" + getMin() + " MAX=" + getMax() + " VIS=" + getVisibleAmount() + " BLK"
        + getBlockIncrement() + " UNIT" + getUnitIncrement() + " ORIENT=" + getOrientation();
  }

}
