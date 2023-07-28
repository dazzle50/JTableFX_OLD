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

package rjc.table.view.cell;

import rjc.table.signal.ObservablePosition;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/********************** Observable table-view cell position from mouse x-y ***********************/
/*************************************************************************************************/

public class MousePosition extends ObservablePosition
{
  private TableView        m_view;                         // associated table-view

  private int              m_x;                            // latest event mouse x coordinate
  private int              m_y;                            // latest event mouse y coordinate
  private int              m_cellXstart;                   // current mouse cell X start
  private int              m_cellXend;                     // current mouse cell X end
  private int              m_cellYstart;                   // current mouse cell Y start
  private int              m_cellYend;                     // current mouse cell Y end

  final static private int PROXIMITY = 4;                  // used to distinguish resize from reorder

  final static private int HEADER    = TableAxis.HEADER;
  final static private int FIRSTCELL = TableAxis.FIRSTCELL;
  final static private int BEFORE    = TableAxis.BEFORE;
  final static private int AFTER     = TableAxis.AFTER;
  final static private int INVALID   = TableAxis.INVALID;

  /**************************************** constructor ******************************************/
  public MousePosition( TableView view )
  {
    // construct
    super();
    m_view = view;
  }

  /******************************************* checkXY *******************************************/
  public void checkXY()
  {
    // re-check mouse cell position
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    setXY( m_x, m_y, false );
  }

  /******************************************** setXY ********************************************/
  public void setXY( int x, int y, boolean updateCursor )
  {
    // determine mouse cell position
    m_x = x;
    m_y = y;

    // check if mouse moved outside current column
    int columnIndex = getColumn();
    int width = Math.min( (int) m_view.getCanvas().getWidth(), m_view.getTableWidth() );
    int header = m_view.getHeaderWidth();

    if ( m_x < m_cellXstart || m_x >= m_cellXend )
    {
      if ( m_x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
        columnIndex = BEFORE;
      }
      else if ( m_x < header )
      {
        m_cellXstart = 0;
        m_cellXend = header;
        columnIndex = HEADER;
      }
      else if ( m_x >= width )
      {
        m_cellXstart = width;
        m_cellXend = Integer.MAX_VALUE;
        columnIndex = AFTER;
      }
      else
      {
        columnIndex = m_view.getColumnIndex( m_x );
        m_cellXstart = Math.max( m_view.getColumnStartX( columnIndex ), header );
        m_cellXend = m_view.getColumnStartX( columnIndex + 1 );
      }
    }

    // check if mouse moved outside current row
    int rowIndex = getRow();
    int height = Math.min( (int) m_view.getCanvas().getHeight(), m_view.getTableHeight() );
    header = m_view.getHeaderHeight();

    if ( m_y < m_cellYstart || m_y >= m_cellYend )
    {
      if ( m_y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
        rowIndex = BEFORE;
      }
      else if ( m_y < header )
      {
        m_cellYstart = 0;
        m_cellYend = header;
        rowIndex = HEADER;
      }
      else if ( m_y >= height )
      {
        m_cellYstart = height;
        m_cellYend = Integer.MAX_VALUE;
        rowIndex = AFTER;
      }
      else
      {
        rowIndex = m_view.getRowIndex( m_y );
        m_cellYstart = Math.max( m_view.getRowStartY( rowIndex ), header );
        m_cellYend = m_view.getRowStartY( rowIndex + 1 );
      }
    }

    // set mouse updated position
    super.setPosition( columnIndex, rowIndex );

    // update the mouse cursor if requested
    if ( updateCursor )
      setCursor();
  }

  /***************************************** setPosition *****************************************/
  @Override
  public void setPosition( int columnIndex, int rowIndex )
  {
    // don't allow direct setting position
    throw new UnsupportedOperationException( "Use setXY() or setInvalid() instead" );
  }

  /***************************************** setInvalid ******************************************/
  public void setInvalid()
  {
    // set position index to invalid
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    super.setPosition( INVALID, INVALID );
  }

  /****************************************** setCursor ******************************************/
  private void setCursor()
  {
    // TODO
  }

}