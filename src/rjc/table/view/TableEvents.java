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
import javafx.scene.input.ScrollEvent;

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
    if ( event.getButton() == MouseButton.NONE )
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
    boolean redraw = false;
    MouseButton button = event.getButton();
    int x = (int) event.getX();
    int y = (int) event.getY();
    setMouseCellPosition( x, y );
    setMouseCursor( x, y );

    // check if cell selected
    if ( getCursor() == Cursors.CROSS )
    {
      focusColumnPos.set( mouseColumnPos.get() );
      focusRowPos.set( mouseRowPos.get() );
      redraw = true;
    }

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

    // request focus and consume event so table does not loss focus to tab-pane
    if ( isTableFocused() && redraw )
      redraw();
    else
      requestFocus();
    event.consume();
  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mouseDragged *****************************************/
  protected void mouseDragged( MouseEvent event )
  {
    // user is moving mouse with button down
    int x = (int) event.getX();
    int y = (int) event.getY();

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= 0 )
    {
      setColumnIndexWidth( m_resizeIndex, x - m_resizeOffset );
      m_cellXend = INVALID;
      m_canvas.widthChange( getColumnIndexXStart( m_resizeIndex ), (int) m_canvas.getWidth() );
      layoutDisplay();
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= 0 )
    {
      setRowIndexHeight( m_resizeIndex, y - m_resizeOffset );
      m_cellYend = INVALID;
      m_canvas.heightChange( getRowIndexYStart( m_resizeIndex ), (int) m_canvas.getWidth() );
      layoutDisplay();
      return;
    }

  }

  /**************************************** mouseScroll ******************************************/
  protected void mouseScroll( ScrollEvent event )
  {
    // scroll up or down depending on mouse wheel scroll event
    if ( m_vScrollBar.isVisible() )
    {
      if ( event.getDeltaY() > 0 )
        m_vScrollBar.decrement();
      else
        m_vScrollBar.increment();
      finishAnimation();
    }
  }

  /************************************ setMouseCellPosition *************************************/
  private void setMouseCellPosition( int x, int y )
  {
    // check if mouse moved outside current column
    if ( x < m_cellXstart || x >= m_cellXend )
    {
      int columnPos = INVALID;
      if ( x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
      }
      else if ( x >= getTableWidth() )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
      }
      else if ( x < getRowHeaderWidth() )
      {
        m_cellXstart = 0;
        m_cellXend = getRowHeaderWidth();
        columnPos = HEADER;
      }
      else
      {
        columnPos = getColumnPositionAtX( x );
        m_cellXstart = getColumnPositionXStart( columnPos );
        if ( m_cellXstart < getRowHeaderWidth() )
          m_cellXstart = getRowHeaderWidth();
        m_cellXend = getColumnPositionXStart( columnPos + 1 );
      }

      mouseColumnPos.set( columnPos );
    }

    // check if mouse moved outside current row
    if ( y < m_cellYstart || y >= m_cellYend )
    {
      int rowPos = INVALID;
      if ( y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
      }
      else if ( y >= getTableHeight() )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
      }
      else if ( y < getColumnHeaderHeight() )
      {
        m_cellYstart = 0;
        m_cellYend = getColumnHeaderHeight();
        rowPos = HEADER;
      }
      else
      {
        rowPos = getRowPositionAtY( y );
        m_cellYstart = getRowPositionYStart( rowPos );
        if ( m_cellYstart < getColumnHeaderHeight() )
          m_cellYstart = getColumnHeaderHeight();
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
