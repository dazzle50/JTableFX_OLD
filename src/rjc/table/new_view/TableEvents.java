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

package rjc.table.new_view;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.Utils;
import rjc.table.view.Cursors;

/*************************************************************************************************/
/*************************** Handles canvas mouse and keyboard events ****************************/
/*************************************************************************************************/

public class TableEvents extends TableSelection
{
  private int m_x;                      // latest event mouse x coordinate
  private int m_y;                      // latest event mouse y coordinate

  private int m_resizeIndex  = INVALID; // column or row index being resized or INVALID
  private int m_resizeOffset = INVALID; // column or row resize offset

  /****************************************** keyTyped *******************************************/
  protected void keyTyped( KeyEvent event )
  {
    /*
    // move editor right or left when tab typed
    char key = event.getCharacter().charAt( 0 );
    if ( key == '\t' )
      if ( event.isShiftDown() )
        moveFocus( MoveDirection.LEFT );
      else
        moveFocus( MoveDirection.RIGHT );
    
    // move editor up or down when carriage return typed
    if ( key == '\r' )
      if ( event.isShiftDown() )
        moveFocus( MoveDirection.UP );
      else
        moveFocus( MoveDirection.DOWN );
    
    // open cell editor if key typed is suitable
    if ( !Character.isISOControl( key ) )
      openEditor( event.getCharacter() );
    */

    redraw();
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // handle key presses
    boolean shift = event.isShiftDown();
    boolean ctrl = event.isControlDown();
    boolean alt = event.isAltDown();
    int columnPos = INVALID;
    int rowPos = INVALID;
    event.consume();

    // handle arrow keys
    if ( !alt )
      switch ( event.getCode() )
      {
        case RIGHT: // right -> arrow key
        case KP_RIGHT:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO columnPos = ctrl ? getVisibleLast() : getVisibleRight( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO columnPos = ctrl ? getVisibleFirst() : getVisibleLeft( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case DOWN: // down arrow key
        case KP_DOWN:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO rowPos = ctrl ? getVisibleBottom() : getVisibleDown( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case UP: // up arrow key
        case KP_UP:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO rowPos = ctrl ? getVisibleTop() : getVisibleUp( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case PAGE_UP: // page up key
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          break;

        case PAGE_DOWN: // page down key
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          break;

        case HOME: // home key - navigate to left-most visible column
          // TODO setSelectFocusPosition( getVisibleFirst(), getSelectRowPos(), !shift, !shift );
          redraw();
          break;

        case END: // end key - navigate to right-most visible column
          // TODO setSelectFocusPosition( getVisibleLast(), getSelectRowPos(), !shift, !shift );
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
          int columnIndex = m_columns.getIndexFromPosition( getFocusColumnPos() );
          int rowIndex = m_rows.getIndexFromPosition( getFocusRowPos() );
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
    // TODO selectTable();
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
    // TODO checkMouseCellPosition();
    if ( event.getButton() == MouseButton.NONE )
      setCursor( Cursors.DEFAULT );
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which table cell mouse is over and set cursor appropriately
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    // TODO checkMouseCellPosition();
    checkMouseCursor();
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
      int columnIndex = m_columns.getIndexFromPosition( getFocusColumnPos() );
      int rowIndex = m_rows.getIndexFromPosition( getFocusRowPos() );
      openEditor( m_data.getValue( columnIndex, rowIndex ) );
    }

    // double-click on column header resize to autofit
    if ( doubleClick && getCursor() == Cursors.H_RESIZE )
    {
      // TODO if ( m_x - m_cellXstart < m_cellXend - m_x )
      // TODO   autofitColumnWidth( getMouseColumnPos() - 1 );
      // TODO else
      // TODO   autofitColumnWidth( getMouseColumnPos() );
    }

    // double-click on row header resize to autofit
    if ( doubleClick && getCursor() == Cursors.V_RESIZE )
    {
      // TODO if ( m_y - m_cellYstart < m_cellYend - m_y )
      // TODO   autofitRowHeight( getMouseRowPos() - 1 );
      // TODO else
      // TODO   autofitRowHeight( getMouseRowPos() );
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
    // TODO checkMouseCellPosition();
    checkMouseCursor();

    // check if cell selected
    if ( getCursor() == Cursors.CROSS )
    {
      // get column and row positions
      int columnPos = getMouseColumnPos();
      int rowPos = getMouseRowPos();

      // TODO if ( ctrl && getFocusColumnPos() >= 0 )
      // TODO      startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      redraw();
      return;
    }

    // check if column selected
    if ( getCursor() == Cursors.DOWNARROW )
    {
      // get column and row positions
      int columnPos = getMouseColumnPos();
      int rowPos = getRowPositionAtY( getColumnHeaderHeight() );

      // TODO if ( ctrl && getFocusColumnPos() >= 0 )
      // TODO   startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      // TODO setSelectFocusPosition( columnPos, BELOW_TABLE, false, !ctrl );
      redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      // get column and row positions
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = getMouseRowPos();

      // TODO if ( ctrl && getFocusColumnPos() >= 0 )
      // TODO   startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      // TODO setSelectFocusPosition( RIGHT_OF_TABLE, rowPos, false, !ctrl );
      redraw();
      return;
    }

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      /*
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPos() - 1 );
      else
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPos() );
      
      m_resizeOffset = m_x - m_columns.getCellSize( m_resizeIndex );
      */
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      /*
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPos() - 1 );
      else
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPos() );
      
      m_resizeOffset = m_y - m_rows.getCellSize( m_resizeIndex );
      */
    }

    // check if whole table selected
    if ( getCursor() == Cursors.DEFAULT && getMouseColumnPos() == HEADER && getMouseRowPos() == HEADER )
    {
      // TODO
      redraw();
      return;
    }
  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // check if column selecting
    if ( getCursor() == Cursors.DOWNARROW )
    {
      int columnPos = getMouseColumnPos();
      // TODO if ( columnPos == RIGHT_OF_TABLE )
      // TODO   columnPos = getVisibleLast();
      // TODO if ( columnPos < 0 )
      // TODO   columnPos = getVisibleFirst();

      // TODO setCurrentSelection( getFocusColumnPos(), 0, columnPos, BELOW_TABLE );
      // TODO selectColumnPos.set( columnPos );
      // TODO ensureColumnShown( columnPos );
      redraw();
      return;
    }

    // check if row selecting
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      int rowPos = getMouseRowPos();
      // TODO if ( rowPos == BELOW_TABLE )
      // TODO   rowPos = getVisibleBottom();
      // TODO if ( rowPos < 0 )
      // TODO   rowPos = getVisibleTop();

      // TODO setCurrentSelection( getCurrentSelection().c1, getFocusRowPos(), getCurrentSelection().c2, rowPos );
      // TODO selectRowPos.set( rowPos );
      // TODO ensureRowShown( rowPos );
      redraw();
      return;
    }
  }

  /**************************************** mouseDragged *****************************************/
  protected void mouseDragged( MouseEvent event )
  {
    // user is moving mouse with button down
    boolean ctrl = event.isControlDown();
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    // TODO checkMouseCellPosition();

    // check if cell selecting
    if ( getCursor() == Cursors.CROSS )
    {
      /*
      int columnPos = getMouseColumnPos();
      if ( columnPos == RIGHT_OF_TABLE )
        columnPos = getVisibleLast();
      if ( columnPos <= HEADER )
        columnPos = getVisibleFirst();
      
      int rowPos = getMouseRowPos();
      if ( rowPos == BELOW_TABLE )
        rowPos = getVisibleBottom();
      if ( rowPos <= HEADER )
        rowPos = getVisibleTop();
      
      if ( !isCellSelected( getFocusColumnPos(), getFocusRowPos() ) )
        startNewSelection();
      if ( columnPos != getSelectColumnPos() || rowPos != getSelectRowPos() )
      {
        setSelectFocusPosition( columnPos, rowPos, false, !ctrl );
        redraw();
      }
      */
      return;
    }

    // check if column selecting
    if ( getCursor() == Cursors.DOWNARROW )
    {
      /*
      int columnPos = getMouseColumnPos();
      if ( columnPos == RIGHT_OF_TABLE )
        columnPos = getVisibleLast();
      if ( columnPos <= HEADER )
        columnPos = getVisibleFirst();
      
      int rowPos = getMouseRowPos();
      if ( rowPos == BELOW_TABLE )
        rowPos = getVisibleBottom();
      if ( rowPos <= HEADER )
        rowPos = getVisibleTop();
      
      if ( columnPos != getSelectColumnPos() || rowPos != getSelectRowPos() )
      {
      
        setCurrentSelection( getFocusColumnPos(), 0, columnPos, BELOW_TABLE );
        selectColumnPos.set( columnPos );
      
        if ( m_x > getCanvasWidth() )
          animateScrollToRightEdge();
        else
        {
          ensureColumnShown( columnPos );
          redraw();
        }
      }
      */
      return;
    }

    // check if row selecting
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      /*
      int rowPos = getMouseRowPos();
      if ( rowPos == BELOW_TABLE )
        rowPos = getVisibleBottom();
      if ( rowPos <= HEADER )
        rowPos = getVisibleTop();
      
      setCurrentSelection( getCurrentSelection().c1, getFocusRowPos(), getCurrentSelection().c2, rowPos );
      ensureRowShown( rowPos );
      selectRowPos.set( rowPos );
      */
      redraw();
      return;
    }

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= 0 )
    {
      /*
      setColumnIndexWidth( m_resizeIndex, m_x - m_resizeOffset );
      m_cellXend = INVALID;
      widthChange( getColumnIndexXStart( m_resizeIndex ), getCanvasWidth() );
      layoutDisplay();
      */
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= 0 )
    {
      /*
      setRowIndexHeight( m_resizeIndex, m_y - m_resizeOffset );
      m_cellYend = INVALID;
      heightChange( getRowIndexYStart( m_resizeIndex ), m_canvas.getHeight() );
      layoutDisplay();
      */
      return;
    }

    // check if column reorder
    if ( getCursor() == Cursors.H_MOVE )
    {
      // TODO
      return;
    }

    // check if row reorder
    if ( getCursor() == Cursors.V_MOVE )
    {
      // TODO
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
    }
  }

  /**************************************** tableScrolled ****************************************/
  protected void tableScrolled()
  {
    // handle any actions needed due to the table scrolled
    redraw();
  }

  /************************************** checkMouseCursor ***************************************/
  protected void checkMouseCursor()
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
      /*
      // if near column edge, set cursor to resize
      if ( m_cellXend - m_x <= PROXIMITY || ( m_x - m_cellXstart <= PROXIMITY && getMouseColumnPos() != 0 ) )
      {
        setCursor( Cursors.H_RESIZE );
        return;
      }
      
      // if column is selected, set cursor to move
      if ( isColumnSelected( getMouseColumnPos() ) )
      {
        setCursor( Cursors.H_MOVE );
        return;
      }
      
      // otherwise, set cursor to down-arrow for selecting 
      setCursor( Cursors.DOWNARROW );
      */
      return;
    }

    // if over vertical header, check if resize, move, or select
    if ( m_x < getRowHeaderWidth() )
    {
      /*
      // if near row edge, set cursor to resize
      if ( m_cellYend - m_y <= PROXIMITY || ( m_y - m_cellYstart <= PROXIMITY && getMouseRowPos() != 0 ) )
      {
        setCursor( Cursors.V_RESIZE );
        return;
      }
      
      // if row is selected, set cursor to move
      if ( isRowSelected( getMouseRowPos() ) )
      {
        setCursor( Cursors.V_MOVE );
        return;
      }
      
      // otherwise, set cursor to right-arrow for selecting 
      setCursor( Cursors.RIGHTARROW );
      */
      return;
    }

    // mouse over table cells
    setCursor( Cursors.CROSS );
  }

}
