/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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

package rjc.table.view.action;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import rjc.table.view.TableView;

/*************************************************************************************************/
/***************************** Red line to aid re-ordering position ******************************/
/*************************************************************************************************/

public class ReorderLine extends Line
{
  private TableView m_view;  // table view for reordering
  private int       m_index; // current index position of line

  /**************************************** constructor ******************************************/
  public ReorderLine( TableView view )
  {
    // prepare positioning line
    setStrokeLineCap( StrokeLineCap.BUTT );
    setStroke( Color.RED );
    setStrokeWidth( 5 );

    // add to the table view
    m_view = view;
    view.add( this );
  }

  /****************************************** setColumn ******************************************/
  public void setColumn( int beforeColumnIndex )
  {
    // position line vertically at start of specified column
    setStartY( 0.0 );
    setEndY( Math.min( m_view.getCanvas().getHeight(), m_view.getTableHeight() ) );

    m_index = beforeColumnIndex;
    int x = m_view.getColumnStartX( m_index );
    if ( x < m_view.getHeaderWidth() )
    {
      m_index = m_view.getColumnIndex( m_view.getHeaderWidth() ) + 1;
      x = m_view.getColumnStartX( m_index );
    }
    if ( x > m_view.getCanvas().getWidth() )
    {
      m_index = m_view.getColumnIndex( (int) m_view.getCanvas().getWidth() );
      x = m_view.getColumnStartX( m_index );
    }
    setStartX( x );
    setEndX( x );
  }

  /******************************************* setRow ********************************************/
  public void setRow( int beforeRowIndex )
  {
    // position line horizontally at start of specified row
    setStartX( 0.0 );
    setEndX( Math.min( m_view.getCanvas().getWidth(), m_view.getTableWidth() ) );

    m_index = beforeRowIndex;
    int y = m_view.getRowStartY( m_index );
    if ( y < m_view.getHeaderHeight() )
    {
      m_index = m_view.getRowIndex( m_view.getHeaderHeight() ) + 1;
      y = m_view.getRowStartY( m_index );
    }
    if ( y > m_view.getCanvas().getWidth() )
    {
      m_index = m_view.getRowIndex( (int) m_view.getCanvas().getHeight() );
      y = m_view.getRowStartY( m_index );
    }
    setStartY( y );
    setEndY( y );
  }

  /****************************************** getIndex *******************************************/
  public int getIndex()
  {
    // return current index position (column or row) of line
    return m_index;
  }
}
