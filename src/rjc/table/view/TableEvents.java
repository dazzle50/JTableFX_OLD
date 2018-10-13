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

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/*************************************************************************************************/
/*************************** Handles canvas mouse and keyboard events ****************************/
/*************************************************************************************************/

public class TableEvents extends TableSelection
{
  private int              m_cellXstart;             // current mouse cell X start
  private int              m_cellXend;               // current mouse cell X end
  private int              m_cellYstart;             // current mouse cell Y start
  private int              m_cellYend;               // current mouse cell Y end

  private int              m_resizeIndex  = INVALID; // column or row index being resized or INVALID
  private int              m_resizeOffset = INVALID; // column or row resize offset

  private static final int PROXIMITY      = 4;       // used to distinguish resize from reorder

  /****************************************** keyTyped *******************************************/
  protected void keyTyped( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** mouseExited *****************************************/
  protected void mouseExited( MouseEvent event )
  {
    // mouse has left table
    setMouseCellPosition( INVALID, INVALID );
    setCursor( Cursors.DEFAULT );
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which table cell mouse is over
    int x = (int) event.getX();
    int y = (int) event.getY();
    setMouseCellPosition( x, y );
    setMouseCursor( x, y );
  }

  /**************************************** mouseClicked *****************************************/
  protected void mouseClicked( MouseEvent event )
  {
    // user has clicked the table
    mouseMoved( event );
  }

  /**************************************** mousePressed *****************************************/
  protected void mousePressed( MouseEvent event )
  {
    // user has press a mouse button
    MouseButton button = event.getButton();
    int x = (int) event.getX();
    int y = (int) event.getY();
    setMouseCellPosition( x, y );
    setMouseCursor( x, y );

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( x - m_cellXstart < m_cellXend - x )
        m_resizeIndex = mouseColumnIndex.get() - 1;
      else
        m_resizeIndex = mouseColumnIndex.get();

      m_resizeOffset = x - getColumnIndexWidth( m_resizeIndex );
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( y - m_cellYstart < m_cellYend - y )
        m_resizeIndex = mouseRowIndex.get() - 1;
      else
        m_resizeIndex = mouseRowIndex.get();

      m_resizeOffset = y - getRowIndexHeight( m_resizeIndex );
    }

  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mouseDragged *****************************************/
  protected void mouseDragged( MouseEvent event )
  {
    //Utils.trace( m_resizeIndex, m_resizeOffset, event );

    // user is moving mouse with button down
    int x = (int) event.getX();
    int y = (int) event.getY();

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= 0 )
    {
      setColumnIndexWidth( m_resizeIndex, x - m_resizeOffset );
      m_canvas.widthChange( getColumnIndexXStart( m_resizeIndex ), (int) m_canvas.getWidth() );
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= 0 )
    {
      setRowIndexHeight( m_resizeIndex, y - m_resizeOffset );
      m_canvas.heightChange( getRowIndexYStart( m_resizeIndex ), (int) m_canvas.getWidth() );
      return;
    }

  }

  /************************************ setMouseCellPosition *************************************/
  private void setMouseCellPosition( int x, int y )
  {
    // check if mouse moved outside current column 
    if ( x < m_cellXstart || x >= m_cellXend )
    {
      int columnPos = getColumnPositionAtX( x );

      if ( columnPos == HEADER )
      {
        m_cellXstart = 0;
        m_cellXend = getRowHeaderWidth();
      }
      else if ( columnPos == LEFT )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
      }
      else if ( columnPos == RIGHT )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellXstart = getColumnPositionXStart( columnPos );
        m_cellXend = getColumnPositionXStart( columnPos + 1 );
      }

      mouseColumnPos.set( columnPos );
    }

    // check if mouse moved outside current row 
    if ( y < m_cellYstart || y >= m_cellYend )
    {
      int rowPos = getRowPositionAtY( y );

      if ( rowPos == HEADER )
      {
        m_cellYstart = 0;
        m_cellYend = getColumnHeaderHeight();
      }
      else if ( rowPos == ABOVE )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
      }
      else if ( rowPos == BELOW )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellYstart = getRowPositionYStart( rowPos );
        m_cellYend = getRowPositionYStart( rowPos + 1 );
      }

      mouseRowPos.set( rowPos );
    }
  }

  /*************************************** setMouseCursor ****************************************/
  protected void setMouseCursor( int x, int y )
  {
    // if over table headers corner, set cursor to default
    if ( x < getRowHeaderWidth() && y < getColumnHeaderHeight() )
    {
      setCursor( Cursors.DEFAULT );
      return;
    }

    // if beyond table cells, set cursor to default
    if ( x >= getTableWidth() || y >= getTableHeight() )
    {
      setCursor( Cursors.DEFAULT );
      return;
    }

    // if over column header, check if resize, move, or select
    if ( y < getColumnHeaderHeight() )
    {
      // if near column edge, set cursor to resize
      if ( m_cellXend - x <= PROXIMITY || ( x - m_cellXstart <= PROXIMITY && mouseColumnPos.get() != 0 ) )
      {
        setCursor( Cursors.H_RESIZE );
        return;
      }

      // if column is selected, set cursor to move
      if ( isColumnSelected( mouseColumnPos.get() ) )
      {
        setCursor( Cursors.H_MOVE );
        return;
      }

      // otherwise, set cursor to down-arrow for selecting 
      setCursor( Cursors.DOWNARROW );
      return;
    }

    // if over vertical header, check if resize, move, or select
    if ( x < getRowHeaderWidth() )
    {
      // if near row edge, set cursor to resize
      if ( m_cellYend - y <= PROXIMITY || ( y - m_cellYstart <= PROXIMITY && mouseRowPos.get() != 0 ) )
      {
        setCursor( Cursors.V_RESIZE );
        return;
      }

      // if row is selected, set cursor to move
      if ( isRowSelected( mouseRowPos.get() ) )
      {
        setCursor( Cursors.V_MOVE );
        return;
      }

      // otherwise, set cursor to right-arrow for selecting 
      setCursor( Cursors.RIGHTARROW );
      return;
    }

    // mouse over table cells
    setCursor( Cursors.CROSS );
  }

}
