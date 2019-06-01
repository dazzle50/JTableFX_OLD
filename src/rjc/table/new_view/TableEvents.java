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
          columnPos = ctrl ? m_columns.getLast() : m_columns.getNext( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          columnPos = ctrl ? m_columns.getFirst() : m_columns.getPrevious( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case DOWN: // down arrow key
        case KP_DOWN:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          rowPos = ctrl ? m_rows.getLast() : m_rows.getNext( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case UP: // up arrow key
        case KP_UP:
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          rowPos = ctrl ? m_rows.getFirst() : m_rows.getPrevious( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift );
          redraw();
          break;

        case PAGE_UP: // page up key
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO
          break;

        case PAGE_DOWN: // page down key
          columnPos = shift ? getSelectColumnPos() : getFocusColumnPos();
          rowPos = shift ? getSelectRowPos() : getFocusRowPos();
          // TODO
          break;

        case HOME: // home key - navigate to left-most visible column
          setSelectFocusPosition( m_columns.getFirst(), getSelectRowPos(), !shift, !shift );
          redraw();
          break;

        case END: // end key - navigate to right-most visible column
          setSelectFocusPosition( m_columns.getLast(), getSelectRowPos(), !shift, !shift );
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
    checkMouseCellPosition();
    if ( event.getButton() == MouseButton.NONE )
      setCursor( Cursors.DEFAULT );
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which table cell mouse is over and set cursor appropriately
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    checkMouseCellPosition();
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
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        autofitColumnWidth( getMouseColumnPos() - 1 );
      else
        autofitColumnWidth( getMouseColumnPos() );
    }

    // double-click on row header resize to autofit
    if ( doubleClick && getCursor() == Cursors.V_RESIZE )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        autofitRowHeight( getMouseRowPos() - 1 );
      else
        autofitRowHeight( getMouseRowPos() );
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
    checkMouseCellPosition();
    checkMouseCursor();

    // check if cell selected
    if ( getCursor() == Cursors.CROSS )
    {
      // get column and row positions
      int columnPos = getMouseColumnPos();
      int rowPos = getMouseRowPos();

      if ( ctrl )
        startNewSelection();
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

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      setSelectFocusPosition( columnPos, AFTER, false, !ctrl );
      redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      // get column and row positions
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = getMouseRowPos();

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl );
      setSelectFocusPosition( AFTER, rowPos, false, !ctrl );
      redraw();
      return;
    }

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPos() - 1 );
      else
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPos() );

      m_resizeOffset = m_x - m_columns.getCellSize( m_resizeIndex );
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPos() - 1 );
      else
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPos() );

      m_resizeOffset = m_y - m_rows.getCellSize( m_resizeIndex );
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
    m_resizeIndex = INVALID;
    m_resizeOffset = INVALID;

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
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    checkMouseCellPosition();

    // check if cell selecting
    if ( getCursor() == Cursors.CROSS )
    {
      int columnPos = getMouseColumnPos();
      if ( columnPos == AFTER )
        columnPos = m_columns.getLast();
      if ( columnPos <= HEADER )
        columnPos = m_columns.getFirst();

      int rowPos = getMouseRowPos();
      if ( rowPos == AFTER )
        rowPos = m_rows.getLast();
      if ( rowPos <= HEADER )
        rowPos = m_rows.getFirst();

      if ( columnPos != getSelectColumnPos() || rowPos != getSelectRowPos() )
      {
        setSelectFocusPosition( columnPos, rowPos, false, false );
        redraw();
      }
      return;
    }

    // check if column selecting
    if ( getCursor() == Cursors.DOWNARROW )
    {
      int columnPos = getMouseColumnPos();
      if ( columnPos == AFTER )
        columnPos = m_columns.getLast();
      if ( columnPos <= HEADER )
        columnPos = m_columns.getFirst();

      if ( columnPos != getSelectColumnPos() )
      {
        setCurrentSelection( getFocusColumnPos(), FIRSTCELL, columnPos, AFTER );
        setSelectColumnPos( columnPos );
        m_hScrollBar.scrollTo( columnPos );
        redraw();

        // TODO scrolling when mouse beyond canvas
      }
      return;
    }

    // check if row selecting
    if ( getCursor() == Cursors.RIGHTARROW )
    {
      int rowPos = getMouseRowPos();
      if ( rowPos == AFTER )
        rowPos = m_rows.getLast();
      if ( rowPos <= HEADER )
        rowPos = m_rows.getFirst();

      if ( rowPos != getSelectRowPos() )
      {
        setCurrentSelection( FIRSTCELL, getFocusRowPos(), AFTER, rowPos );
        setSelectRowPos( rowPos );
        m_vScrollBar.scrollTo( rowPos );
        redraw();

        // TODO scrolling when mouse beyond canvas
      }
      return;
    }

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= FIRSTCELL )
    {
      m_columns.setCellSize( m_resizeIndex, m_x - m_resizeOffset );
      m_cellXend = INVALID;
      widthChange( getXStartFromColumnPos( m_columns.getPositionFromIndex( m_resizeIndex ) ),
          (int) m_canvas.getWidth() );
      layoutDisplay();
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= FIRSTCELL )
    {
      m_rows.setCellSize( m_resizeIndex, m_y - m_resizeOffset );
      m_cellYend = INVALID;
      heightChange( getYStartFromRowPos( m_rows.getPositionFromIndex( m_resizeIndex ) ), (int) m_canvas.getWidth() );
      layoutDisplay();
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

  /*********************************** checkMouseCellPosition ************************************/
  protected void checkMouseCellPosition()
  {
    // check if mouse moved outside current column
    if ( m_x < m_cellXstart || m_x >= m_cellXend )
    {
      int columnPos = INVALID;
      if ( m_x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
        columnPos = BEFORE;
      }
      else if ( m_x + m_hScrollBar.getValue() >= getTableWidth() )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
        columnPos = AFTER;
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
        m_cellXstart = getXStartFromColumnPos( columnPos );
        if ( m_cellXstart < getRowHeaderWidth() )
          m_cellXstart = getRowHeaderWidth();
        m_cellXend = getXStartFromColumnPos( columnPos + 1 );
      }

      setMouseColumnPos( columnPos );
    }

    // check if mouse moved outside current row
    if ( m_y < m_cellYstart || m_y >= m_cellYend )
    {
      int rowPos = INVALID;
      if ( m_y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
        rowPos = BEFORE;
      }
      else if ( m_y + m_vScrollBar.getValue() >= getTableHeight() )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
        rowPos = AFTER;
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
        m_cellYstart = getYStartFromRowPos( rowPos );
        if ( m_cellYstart < getColumnHeaderHeight() )
          m_cellYstart = getColumnHeaderHeight();
        m_cellYend = getYStartFromRowPos( rowPos + 1 );
      }

      setMouseRowPos( rowPos );
    }
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
      // if near column edge, set cursor to resize
      if ( m_cellXend - m_x <= PROXIMITY || ( m_x - m_cellXstart <= PROXIMITY && getMouseColumnPos() != FIRSTCELL ) )
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
      return;
    }

    // if over vertical header, check if resize, move, or select
    if ( m_x < getRowHeaderWidth() )
    {
      // if near row edge, set cursor to resize
      if ( m_cellYend - m_y <= PROXIMITY || ( m_y - m_cellYstart <= PROXIMITY && getMouseRowPos() != FIRSTCELL ) )
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
      return;
    }

    // mouse over table body cells
    setCursor( Cursors.CROSS );
  }

  /************************************** autofitRowHeight ***************************************/
  public void autofitRowHeight( int rowPos )
  {
    // autofit column width to avoid text ellipsis TODO
    Utils.trace( "DOUBLE CLICK to autofit row position " + rowPos );
  }

  /************************************* autofitColumnWidth **************************************/
  public void autofitColumnWidth( int columnPos )
  {
    // autofit row height to avoid text ellipsis TODO
    Utils.trace( "DOUBLE CLICK to autofit column position " + columnPos );
  }

}
