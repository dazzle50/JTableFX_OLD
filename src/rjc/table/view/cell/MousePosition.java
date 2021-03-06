/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/********************** Observable table-view cell position from mouse x-y ***********************/
/*************************************************************************************************/

public class MousePosition extends CellPosition
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
    int columnPos = getColumnPos();
    int width = Math.min( (int) m_view.getCanvas().getWidth(), m_view.getTableWidth() );
    int header = m_view.getHeaderWidth();

    if ( m_x < m_cellXstart || m_x >= m_cellXend )
    {
      if ( m_x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
        columnPos = BEFORE;
      }
      else if ( m_x < header )
      {
        m_cellXstart = 0;
        m_cellXend = header;
        columnPos = HEADER;
      }
      else if ( m_x >= width )
      {
        m_cellXstart = width;
        m_cellXend = Integer.MAX_VALUE;
        columnPos = AFTER;
      }
      else
      {
        columnPos = m_view.getColumnPositionAtX( m_x );
        m_cellXstart = Math.max( m_view.getXStartFromColumnPos( columnPos ), header );
        m_cellXend = m_view.getXStartFromColumnPos( columnPos + 1 );
      }
    }

    // check if mouse moved outside current row
    int rowPos = getRowPos();
    int height = Math.min( (int) m_view.getCanvas().getHeight(), m_view.getTableHeight() );
    header = m_view.getHeaderHeight();

    if ( m_y < m_cellYstart || m_y >= m_cellYend )
    {
      if ( m_y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
        rowPos = BEFORE;
      }
      else if ( m_y < header )
      {
        m_cellYstart = 0;
        m_cellYend = header;
        rowPos = HEADER;
      }
      else if ( m_y >= height )
      {
        m_cellYstart = height;
        m_cellYend = Integer.MAX_VALUE;
        rowPos = AFTER;
      }
      else
      {
        rowPos = m_view.getRowPositionAtY( m_y );
        m_cellYstart = Math.max( m_view.getYStartFromRowPos( rowPos ), header );
        m_cellYend = m_view.getYStartFromRowPos( rowPos + 1 );
      }
    }

    // set mouse updated position
    super.setPosition( columnPos, rowPos );

    // update the mouse cursor if requested
    if ( updateCursor )
      setCursor();
  }

  /****************************************** setCursor ******************************************/
  private void setCursor()
  {
    // if over table headers corner, set cursor to default
    int columnHeaderHeight = m_view.getHeaderHeight();
    int rowHeaderWidth = m_view.getHeaderWidth();
    if ( m_x < rowHeaderWidth && m_y < columnHeaderHeight )
    {
      m_view.setCursor( Cursors.DEFAULT );
      return;
    }

    // if beyond table cells, set cursor to default
    if ( m_x >= m_view.getTableWidth() || m_y >= m_view.getTableHeight() )
    {
      m_view.setCursor( Cursors.DEFAULT );
      return;
    }

    // if over column header, check if resize, move, or select
    if ( m_y < columnHeaderHeight )
    {
      // if near column edge, set cursor to resize
      if ( m_cellXend - m_x <= PROXIMITY
          || ( m_x - m_cellXstart <= PROXIMITY && m_view.getMouseCell().getColumnPos() != FIRSTCELL ) )
      {
        m_view.setCursor( Cursors.H_RESIZE );
        return;
      }

      // if column is selected, set cursor to move
      if ( m_view.getSelection().isColumnSelected( m_view.getMouseCell().getColumnPos() ) )
      {
        m_view.setCursor( Cursors.H_MOVE );
        return;
      }

      // otherwise, set cursor to down-arrow for selecting 
      m_view.setCursor( Cursors.DOWNARROW );
      return;
    }

    // if over vertical header, check if resize, move, or select
    if ( m_x < rowHeaderWidth )
    {
      // if near row edge, set cursor to resize
      if ( m_cellYend - m_y <= PROXIMITY
          || ( m_y - m_cellYstart <= PROXIMITY && m_view.getMouseCell().getRowPos() != FIRSTCELL ) )
      {
        m_view.setCursor( Cursors.V_RESIZE );
        return;
      }

      // if row is selected, set cursor to move
      if ( m_view.getSelection().isRowSelected( m_view.getMouseCell().getRowPos() ) )
      {
        m_view.setCursor( Cursors.V_MOVE );
        return;
      }

      // otherwise, set cursor to right-arrow for selecting 
      m_view.setCursor( Cursors.RIGHTARROW );
      return;
    }

    // mouse over table body cells
    m_view.setCursor( Cursors.CROSS );
  }

  /***************************************** setPosition *****************************************/
  @Override
  public void setPosition( int columnPos, int rowPos )
  {
    throw new UnsupportedOperationException( "Use setXY() instead" );
  }

  /***************************************** setPosition *****************************************/
  @Override
  public void setPosition( CellPosition pos )
  {
    throw new UnsupportedOperationException( "Use setXY() instead" );
  }

  /**************************************** setColumnPos *****************************************/
  @Override
  public void setColumnPos( int position )
  {
    throw new UnsupportedOperationException( "Use setXY() instead" );
  }

  /****************************************** setRowPos ******************************************/
  @Override
  public void setRowPos( int position )
  {
    throw new UnsupportedOperationException( "Use setXY() instead" );
  }

}
