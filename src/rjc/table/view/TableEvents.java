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
  private int              m_x;                      // latest event mouse x coordinate
  private int              m_y;                      // latest event mouse y coordinate
  private int              m_cellXstart;             // current mouse cell X start
  private int              m_cellXend;               // current mouse cell X end
  private int              m_cellYstart;             // current mouse cell Y start
  private int              m_cellYend;               // current mouse cell Y end

  private int              m_resizeIndex  = INVALID; // column or row index being resized or INVALID
  private int              m_resizeOffset = INVALID; // column or row resize offset

  private Selected         m_selection;              // current selection area

  private static final int PROXIMITY      = 4;       // used to distinguish resize from reorder

  /****************************************** keyTyped *******************************************/
  protected void keyTyped( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // handle key presses
    boolean shift = event.isShiftDown();
    boolean ctrl = event.isControlDown();
    boolean alt = event.isAltDown();
    int pos = INVALID;
    event.consume();

    // if shift not pressed move select cell to focus cell
    if ( !shift )
    {
      selectColumnPos.set( focusColumnPos.get() );
      selectRowPos.set( focusRowPos.get() );
    }

    // handle arrow keys
    if ( !alt )
      switch ( event.getCode() )
      {
        case RIGHT: // right -> arrow key
          pos = ctrl ? getVisibleLast() : getVisibleRight( selectColumnPos.get() );
          setSelectFocusPosition( pos, selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case LEFT: // left <- arrow key
          pos = ctrl ? getVisibleFirst() : getVisibleLeft( selectColumnPos.get() );
          setSelectFocusPosition( pos, selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case DOWN: // down arrow key
          pos = ctrl ? getVisibleBottom() : getVisibleDown( selectRowPos.get() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          redraw();
          break;

        case UP: // up arrow key
          pos = ctrl ? getVisibleTop() : getVisibleUp( selectRowPos.get() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          redraw();
          break;

        default: // anything else
          break;
      }

  }

  /***************************************** mouseExited *****************************************/
  protected void mouseExited( MouseEvent event )
  {
    // mouse has left table
    m_x = INVALID;
    m_y = INVALID;
    setMousePosition();
    if ( event.getButton() == MouseButton.NONE )
      setCursor( Cursors.DEFAULT );
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which table cell mouse is over and set cursor appropriately
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    setMousePosition();
    setMouseCursor();
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
    boolean shift = event.isShiftDown();
    boolean ctrl = event.isControlDown();
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    event.consume();
    requestFocus();
    setMousePosition();
    setMouseCursor();

    // check if cell selected
    if ( getCursor() == Cursors.CROSS )
    {
      m_selection = null;
      setSelectFocusPosition( mouseColumnPos.get(), mouseRowPos.get(), !shift, !ctrl );
      redraw();
      return;
    }

    // check if column selected
    if ( getCursor() == Cursors.DOWNARROW )
    {
      //setSelectPosition( mouseColumnPos.get(), mouseRowPos.get(), !shift, !ctrl );
      //selectColumns( focusColumnPos.get(), selectColumnPos.get(), true );
      redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      //setSelectPosition( mouseColumnPos.get(), mouseRowPos.get(), !shift, !ctrl );
      //selectRows( focusRowPos.get(), selectRowPos.get(), true );
      redraw();
      return;
    }

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        m_resizeIndex = getColumnIndexFromPosition( mouseColumnPos.get() - 1 );
      else
        m_resizeIndex = getColumnIndexFromPosition( mouseColumnPos.get() );

      m_resizeOffset = m_x - getColumnIndexWidth( m_resizeIndex );
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        m_resizeIndex = getRowIndexFromPosition( mouseRowPos.get() - 1 );
      else
        m_resizeIndex = getRowIndexFromPosition( mouseRowPos.get() );

      m_resizeOffset = m_y - getRowIndexHeight( m_resizeIndex );
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
    // user is moving mouse with button down
    boolean ctrl = event.isControlDown();
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    setMousePosition();

    // check if cell selecting
    if ( getCursor() == Cursors.CROSS )
    {
      setSelectFocusPosition( mouseColumnPos.get(), mouseRowPos.get(), false, !ctrl );
      redraw();
      return;
    }

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= 0 )
    {
      setColumnIndexWidth( m_resizeIndex, m_x - m_resizeOffset );
      m_cellXend = INVALID;
      widthChange( getColumnIndexXStart( m_resizeIndex ), getCanvasWidth() );
      layoutDisplay();
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= 0 )
    {
      setRowIndexHeight( m_resizeIndex, m_y - m_resizeOffset );
      m_cellYend = INVALID;
      heightChange( getRowIndexYStart( m_resizeIndex ), getCanvasHeight() );
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

  /*********************************** setSelectFocusPosition ************************************/
  protected void setSelectFocusPosition( int columnPos, int rowPos, boolean setFocus, boolean clearSelection )
  {
    // ensure column and row positions are visible
    columnPos = ensureColumnShown( columnPos );
    rowPos = ensureRowShown( rowPos );

    // set table select & focus cell position properties
    selectColumnPos.set( columnPos );
    selectRowPos.set( rowPos );
    if ( setFocus )
    {
      focusColumnPos.set( columnPos );
      focusRowPos.set( rowPos );
    }

    // clear previous selections
    if ( clearSelection )
    {
      clearAllSelection();
      m_selection = null;
    }

    // update current selection area
    if ( m_selection == null )
      m_selection = startSelection();
    m_selection.set( focusColumnPos.get(), focusRowPos.get(), selectColumnPos.get(), selectRowPos.get() );
  }

  /************************************* resetMousePosition **************************************/
  protected void resetMousePosition()
  {
    // determine mouse cell position
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    setMousePosition();
  }

  /************************************** setMousePosition ***************************************/
  protected void setMousePosition()
  {
    // check if mouse moved outside current column
    if ( m_x < m_cellXstart || m_x >= m_cellXend )
    {
      int columnPos = INVALID;
      if ( m_x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
      }
      else if ( m_x >= getTableWidth() )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
      }
      else if ( m_x < getRowHeaderWidth() )
      {
        m_cellXstart = 0;
        m_cellXend = getRowHeaderWidth();
        columnPos = HEADER;
      }
      else
      {
        columnPos = getColumnPositionAtX( m_x );
        m_cellXstart = getColumnPositionXStart( columnPos );
        if ( m_cellXstart < getRowHeaderWidth() )
          m_cellXstart = getRowHeaderWidth();
        m_cellXend = getColumnPositionXStart( columnPos + 1 );
      }

      mouseColumnPos.set( columnPos );
    }

    // check if mouse moved outside current row
    if ( m_y < m_cellYstart || m_y >= m_cellYend )
    {
      int rowPos = INVALID;
      if ( m_y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
      }
      else if ( m_y >= getTableHeight() )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
      }
      else if ( m_y < getColumnHeaderHeight() )
      {
        m_cellYstart = 0;
        m_cellYend = getColumnHeaderHeight();
        rowPos = HEADER;
      }
      else
      {
        rowPos = getRowPositionAtY( m_y );
        m_cellYstart = getRowPositionYStart( rowPos );
        if ( m_cellYstart < getColumnHeaderHeight() )
          m_cellYstart = getColumnHeaderHeight();
        m_cellYend = getRowPositionYStart( rowPos + 1 );
      }

      mouseRowPos.set( rowPos );
    }
  }

  /*************************************** setMouseCursor ****************************************/
  protected void setMouseCursor()
  {
    // if over table headers corner, set cursor to default
    if ( m_x < getRowHeaderWidth() && m_y < getColumnHeaderHeight() )
    {
      setCursor( Cursors.DEFAULT );
      return;
    }

    // if beyond table cells, set cursor to default
    if ( m_x >= getTableWidth() || m_y >= getTableHeight() )
    {
      setCursor( Cursors.DEFAULT );
      return;
    }

    // if over column header, check if resize, move, or select
    if ( m_y < getColumnHeaderHeight() )
    {
      // if near column edge, set cursor to resize
      if ( m_cellXend - m_x <= PROXIMITY || ( m_x - m_cellXstart <= PROXIMITY && mouseColumnPos.get() != 0 ) )
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
    if ( m_x < getRowHeaderWidth() )
    {
      // if near row edge, set cursor to resize
      if ( m_cellYend - m_y <= PROXIMITY || ( m_y - m_cellYstart <= PROXIMITY && mouseRowPos.get() != 0 ) )
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
