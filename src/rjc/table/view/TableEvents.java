/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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
import rjc.table.Utils;

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
        case KP_RIGHT:
          pos = ctrl ? getVisibleLast() : getVisibleRight( selectColumnPos.get() );
          setSelectFocusPosition( pos, selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          pos = ctrl ? getVisibleFirst() : getVisibleLeft( selectColumnPos.get() );
          setSelectFocusPosition( pos, selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case DOWN: // down arrow key
        case KP_DOWN:
          pos = ctrl ? getVisibleBottom() : getVisibleDown( selectRowPos.get() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          redraw();
          break;

        case UP: // up arrow key
        case KP_UP:
          pos = ctrl ? getVisibleTop() : getVisibleUp( selectRowPos.get() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          redraw();
          break;

        case PAGE_UP: // page up key
          int newTopRow = getRowPositionAtY(
              2 * getColumnHeaderHeight() - getRowPositionYStart( getRowPositionAtY( getCanvasHeight() ) ) );
          if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
            newTopRow--;
          int newYOffset = getRowPositionYStart( newTopRow ) - getColumnHeaderHeight() + getYOffset();
          int focusY = ( getRowPositionYStart( selectRowPos.get() ) + getRowPositionYStart( selectRowPos.get() + 1 ) )
              / 2;
          pos = getRowPositionAtY( focusY + newYOffset - getYOffset() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          animateToYOffset( newYOffset );
          break;

        case PAGE_DOWN: // page down key
          newTopRow = getRowPositionAtY( getCanvasHeight() );
          if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
            newTopRow++;
          newYOffset = getRowPositionYStart( newTopRow ) - getColumnHeaderHeight() + getYOffset();
          focusY = ( getRowPositionYStart( selectRowPos.get() ) + getRowPositionYStart( selectRowPos.get() + 1 ) ) / 2;
          pos = getRowPositionAtY( focusY + newYOffset - getYOffset() );
          setSelectFocusPosition( selectColumnPos.get(), pos, !shift, !shift );
          animateToYOffset( newYOffset );
          break;

        case HOME: // home key - navigate to left-most visible column
          setSelectFocusPosition( getVisibleFirst(), selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case END: // end key - navigate to right-most visible column
          setSelectFocusPosition( getVisibleLast(), selectRowPos.get(), !shift, !shift );
          redraw();
          break;

        case DELETE: // delete key - delete selected cells content
          deleteKeyPressed();
          break;

        case INSERT: // insert key - insert row or column
          insertKeyPressed();
          break;

        case A:
          // select whole table (Ctrl-A)
          if ( ctrl )
            controlAPressed();
          break;

        case X:
          // cut cells contents (Ctrl-X)
          if ( ctrl )
            controlXPressed();
          break;

        case C:
          // copy cells contents (Ctrl-C)
          if ( ctrl )
            controlCPressed();
          break;

        case V:
          // paste cells contents (Ctrl-V)
          if ( ctrl )
            controlVPressed();
          break;

        case D:
          // fill-down cells contents (Ctrl-D)
          if ( ctrl )
            controlDPressed();
          break;

        case F2: // F2 key - open cell editor with current focus cell contents
          int columnIndex = getColumnIndexFromPosition( focusColumnPos.get() );
          int rowIndex = getRowIndexFromPosition( focusRowPos.get() );
          openEditor( m_data.getValue( columnIndex, rowIndex ) );
          break;

        default: // anything else
          break;
      }

  }

  /***************************************** openEditor ******************************************/
  protected void openEditor( Object value )
  {
    // open editor at focus cell
    Utils.trace( "EDIT - NOT YET IMPLEMENTED !!! value = ", value );
  }

  /************************************** insertKeyPressed ***************************************/
  protected void insertKeyPressed()
  {
    // insert key pressed - TODO
    Utils.trace( "INSERT - NOT YET IMPLEMENTED !!!" );
  }

  /************************************** deleteKeyPressed ***************************************/
  protected void deleteKeyPressed()
  {
    // insert key pressed - TODO
    Utils.trace( "DELETE - NOT YET IMPLEMENTED !!!" );
  }

  /*************************************** controlAPressed ***************************************/
  protected void controlAPressed()
  {
    // select whole table (Ctrl-A)
    selectTable();
    redraw();
  }

  /*************************************** controlXPressed ***************************************/
  protected void controlXPressed()
  {
    // cut cells contents (Ctrl-X) - TODO
    Utils.trace( "CUT - NOT YET IMPLEMENTED !!!" );
  }

  /*************************************** controlCPressed ***************************************/
  protected void controlCPressed()
  {
    // copy cells contents (Ctrl-C) - TODO
    Utils.trace( "COPY - NOT YET IMPLEMENTED !!!" );
  }

  /*************************************** controlVPressed ***************************************/
  protected void controlVPressed()
  {
    // paste cells contents (Ctrl-V) - TODO
    Utils.trace( "PASTE - NOT YET IMPLEMENTED !!!" );
  }

  /*************************************** controlDPressed ***************************************/
  protected void controlDPressed()
  {
    // fill-down cells contents (Ctrl-D) - TODO
    Utils.trace( "FILL-DOWN - NOT YET IMPLEMENTED !!!" );
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
    boolean doubleClick = event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY;
    event.consume();

    // double-click on table body to start cell editor with cell contents
    if ( doubleClick && getCursor() == Cursors.CROSS )
    {
      int columnIndex = getColumnIndexFromPosition( focusColumnPos.get() );
      int rowIndex = getRowIndexFromPosition( focusRowPos.get() );
      openEditor( m_data.getValue( columnIndex, rowIndex ) );
    }

    // double-click on column header resize to autofit
    if ( doubleClick && getCursor() == Cursors.H_RESIZE )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        autofitColumnWidth( mouseColumnPos.get() - 1 );
      else
        autofitColumnWidth( mouseColumnPos.get() );
    }

    // double-click on row header resize to autofit
    if ( doubleClick && getCursor() == Cursors.V_RESIZE )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        autofitRowHeight( mouseRowPos.get() - 1 );
      else
        autofitRowHeight( mouseRowPos.get() );
    }
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
      startNewSelection();
      setSelectFocusPosition( mouseColumnPos.get(), mouseRowPos.get(), !shift, !ctrl );
      redraw();
      return;
    }

    // check if column selected
    if ( getCursor() == Cursors.DOWNARROW )
    {
      int columnPos = mouseColumnPos.get();
      int rowPos = getRowPositionAtY( getColumnHeaderHeight() );
      startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      setCurrentSelection( getCurrentSelection().c1, 0, getCurrentSelection().c2, m_data.getRowCount() - 1 );

      // setting selectRowPos to max int indicates column being selected
      selectRowPos.set( Integer.MAX_VALUE );
      redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = mouseRowPos.get();
      startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      setCurrentSelection( 0, getCurrentSelection().r1, m_data.getColumnCount() - 1, getCurrentSelection().r2 );

      // setting selectColumnPos to max int indicates row being selected
      selectColumnPos.set( Integer.MAX_VALUE );
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

    // check if whole table selected
    if ( getCursor() == Cursors.DEFAULT && mouseColumnPos.get() == HEADER && mouseRowPos.get() == HEADER )
    {
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = getRowPositionAtY( getColumnHeaderHeight() );
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      selectTable();
      selectColumnPos.set( getVisibleLast() );
      selectRowPos.set( getVisibleBottom() );
      redraw();
      return;
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

    // check if column selecting
    if ( getCursor() == Cursors.DOWNARROW )
    {
      setCurrentSelection( focusColumnPos.get(), getCurrentSelection().r1, mouseColumnPos.get(),
          getCurrentSelection().r2 );
      if ( isAnimationFinished() )
        ensureColumnShown( mouseColumnPos.get() );
      selectColumnPos.set( mouseColumnPos.get() );
      redraw();
      return;
    }

    // check if row selecting
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      setCurrentSelection( getCurrentSelection().c1, focusRowPos.get(), getCurrentSelection().c2, mouseRowPos.get() );
      if ( isAnimationFinished() )
        ensureRowShown( mouseRowPos.get() );
      selectRowPos.set( mouseRowPos.get() );
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

    // check if column reorder
    if ( getCursor() == Cursors.H_MOVE )
    {

      return;
    }

    // check if row reorder
    if ( getCursor() == Cursors.V_MOVE )
    {

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
