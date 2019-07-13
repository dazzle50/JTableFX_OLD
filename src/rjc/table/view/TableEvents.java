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
import rjc.table.view.TableScrollBar.Animation;

/*************************************************************************************************/
/*************************** Handles canvas mouse and keyboard events ****************************/
/*************************************************************************************************/

public class TableEvents extends TableSelect
{
  private int              m_x;                      // latest event mouse x coordinate
  private int              m_y;                      // latest event mouse y coordinate
  private int              m_cellXstart;             // current mouse cell X start
  private int              m_cellXend;               // current mouse cell X end
  private int              m_cellYstart;             // current mouse cell Y start
  private int              m_cellYend;               // current mouse cell Y end

  private int              m_resizeIndex  = INVALID; // column or row index being resized or INVALID
  private int              m_resizeOffset = INVALID; // column or row resize offset
  private Selecting        m_selecting;              // current cell selecting status

  private static final int PROXIMITY      = 4;       // used to distinguish resize from reorder

  // types of scroll bar animations
  public static enum Selecting
  {
    NONE, CELLS, ROWS, COLUMNS
  }

  /****************************************** keyTyped *******************************************/
  protected void keyTyped( KeyEvent event )
  {
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
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
          columnPos = ctrl ? m_columns.getLast() : m_columns.getNext( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          redraw();
          break;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
          columnPos = ctrl ? m_columns.getFirst() : m_columns.getPrevious( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          redraw();
          break;

        case DOWN: // down arrow key
        case KP_DOWN:
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
          rowPos = ctrl ? m_rows.getLast() : m_rows.getNext( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          redraw();
          break;

        case UP: // up arrow key
        case KP_UP:
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
          rowPos = ctrl ? m_rows.getFirst() : m_rows.getPrevious( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          redraw();
          break;

        case PAGE_DOWN: // page down key
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          m_vScrollBar.finishAnimation();
          if ( m_vScrollBar.isVisible() && m_vScrollBar.getValue() < m_vScrollBar.getMax() )
          {
            // bottom of table not visible, make sure scroll down at least one row
            int newTopRow = getRowPositionAtY( (int) m_canvas.getHeight() + 1 );
            if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
              newTopRow = m_rows.getNext( newTopRow );
            int newYScroll = m_rows.getStartFromPosition( newTopRow, 0 ) - getColumnHeaderHeight();

            // determine new position for select/focus 
            rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
            int ySelect = ( getYStartFromRowPos( rowPos ) + getYStartFromRowPos( rowPos + 1 ) ) / 2;
            ySelect = Utils.clamp( ySelect, getColumnHeaderHeight(), (int) m_canvas.getHeight() );
            rowPos = getRowPositionAtY( ySelect + newYScroll - (int) m_vScrollBar.getValue() );
            if ( rowPos >= AFTER )
              rowPos = m_rows.getLast();
            boolean rowNotCompletelyVisible = getYStartFromRowPos( rowPos + 1 ) > m_canvas.getHeight() + newYScroll
                - (int) m_vScrollBar.getValue();
            boolean rowAboveCompletelyVisible = getYStartFromRowPos(
                m_rows.getPrevious( rowPos ) ) > getColumnHeaderHeight() + newYScroll - (int) m_vScrollBar.getValue();
            if ( rowNotCompletelyVisible && rowAboveCompletelyVisible )
              rowPos = m_rows.getPrevious( rowPos );

            // move select/focus and scroll table
            setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
            m_vScrollBar.animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
          }
          else
          {
            // bottom of table already visible so move to last row
            setSelectFocusPosition( columnPos, m_rows.getLast(), !shift, !ctrl, true );
            redraw();
          }
          break;

        case PAGE_UP: // page up key
          columnPos = shift ? getSelectColumnPosition() : getFocusColumnPosition();
          m_vScrollBar.finishAnimation();
          if ( m_vScrollBar.isVisible() && m_vScrollBar.getValue() > 0.0 )
          {
            // top of table not visible, make sure scroll up at least one row
            int newTopY = (int) m_vScrollBar.getValue() + 2 * getColumnHeaderHeight() - (int) m_canvas.getHeight();
            int newTopRow = m_rows.getPositionFromCoordinate( newTopY, 0 );
            if ( newTopRow <= HEADER )
              newTopRow = m_rows.getFirst();
            if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
              newTopRow = m_rows.getPrevious( newTopRow );
            int newYScroll = m_rows.getStartFromPosition( newTopRow, 0 ) - getColumnHeaderHeight();

            // determine new position for select/focus
            rowPos = shift ? getSelectRowPosition() : getFocusRowPosition();
            int ySelect = ( getYStartFromRowPos( rowPos ) + getYStartFromRowPos( rowPos + 1 ) ) / 2;
            ySelect = Utils.clamp( ySelect, getColumnHeaderHeight(), (int) m_canvas.getHeight() );
            rowPos = m_rows.getPositionFromCoordinate( ySelect, newYScroll );
            if ( rowPos <= HEADER )
              rowPos = m_rows.getFirst();

            // move select/focus and scroll table
            setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
            m_vScrollBar.animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
          }
          else
          {
            // top of table visible so move to first row
            setSelectFocusPosition( columnPos, m_rows.getFirst(), !shift, !ctrl, true );
            redraw();
          }
          break;

        case HOME: // home key - navigate to left-most visible column
          setSelectFocusPosition( m_columns.getFirst(), getSelectRowPosition(), !shift, !ctrl, true );
          redraw();
          break;

        case END: // end key - navigate to right-most visible column
          setSelectFocusPosition( m_columns.getLast(), getSelectRowPosition(), !shift, !ctrl, true );
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
          int columnIndex = m_columns.getIndexFromPosition( getFocusColumnPosition() );
          int rowIndex = m_rows.getIndexFromPosition( getFocusRowPosition() );
          openEditor( m_data.getValue( columnIndex, rowIndex ) );
          break;

        case MINUS:
        case SUBTRACT:
          // zoom out (Ctrl-minus)
          if ( ctrl )
            setViewZoom( getZoom() / Math.pow( 2.0, 0.0625 ) );
          break;

        case EQUALS:
        case ADD:
          // zoom in (Ctrl-plus)
          if ( ctrl )
            setViewZoom( getZoom() * Math.pow( 2.0, 0.0625 ) );
          break;

        case DIGIT0:
          // zoom 1:1 (Ctrl-0)
          if ( ctrl )
            setViewZoom( 1.0 );
          break;

        default: // anything else
          //Utils.trace( "DEFAULT " + event.getCode() );
          break;
      }

  }

  /***************************************** setViewZoom *****************************************/
  public void setViewZoom( double zoom )
  {
    // set zoom scale for this view
    setZoom( zoom );

    layoutDisplay();
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    checkMouseCellPosition();
    checkMouseCursor();
    redraw();
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

  /**************************************** mousePressed *****************************************/
  protected void mousePressed( MouseEvent event )
  {
    // user has press a mouse button
    event.consume();
    requestFocus();
    checkMouseCursor();
    m_selecting = Selecting.NONE;
    MouseButton button = event.getButton();
    boolean shift = event.isShiftDown();
    boolean ctrl = event.isControlDown();

    // check if cell selected
    if ( getCursor() == Cursors.CROSS && button == MouseButton.PRIMARY )
    {
      // get column and row positions
      m_selecting = Selecting.CELLS;
      int columnPos = getMouseColumnPosition();
      int rowPos = getMouseRowPosition();

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      redraw();
      return;
    }

    // check if column selected
    if ( getCursor() == Cursors.DOWNARROW && button == MouseButton.PRIMARY )
    {
      // get column and row positions
      m_selecting = Selecting.COLUMNS;
      int columnPos = getMouseColumnPosition();
      int rowPos = getRowPositionAtY( getColumnHeaderHeight() );

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      setSelectFocusPosition( columnPos, AFTER, false, !ctrl, false );
      redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW && button == MouseButton.PRIMARY )
    {
      // get column and row positions
      m_selecting = Selecting.ROWS;
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = getMouseRowPosition();

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      setSelectFocusPosition( AFTER, rowPos, false, !ctrl, false );
      redraw();
      return;
    }

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPosition() - 1 );
      else
        m_resizeIndex = m_columns.getIndexFromPosition( getMouseColumnPosition() );

      m_resizeOffset = m_x - m_columns.getCellPixels( m_resizeIndex );
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPosition() - 1 );
      else
        m_resizeIndex = m_rows.getIndexFromPosition( getMouseRowPosition() );

      m_resizeOffset = m_y - m_rows.getCellPixels( m_resizeIndex );
    }

    // check if whole table selected
    if ( getCursor() == Cursors.DEFAULT && getMouseColumnPosition() == HEADER && getMouseRowPosition() == HEADER )
    {
      selectTable();
      redraw();
      return;
    }
  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // user has press a mouse button
    event.consume();
    checkMouseCursor();

    // stop any selecting and resizing
    m_selecting = Selecting.NONE;
    m_resizeIndex = INVALID;
    m_resizeOffset = INVALID;

    // stop any scrolling to edges
    m_hScrollBar.stopAnimationStartEnd();
    m_vScrollBar.stopAnimationStartEnd();
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
      int columnIndex = m_columns.getIndexFromPosition( getFocusColumnPosition() );
      int rowIndex = m_rows.getIndexFromPosition( getFocusRowPosition() );
      openEditor( m_data.getValue( columnIndex, rowIndex ) );
    }

    // double-click on column header resize to autofit
    if ( doubleClick && getCursor() == Cursors.H_RESIZE )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        autofitColumnWidth( getMouseColumnPosition() - 1 );
      else
        autofitColumnWidth( getMouseColumnPosition() );
    }

    // double-click on row header resize to autofit
    if ( doubleClick && getCursor() == Cursors.V_RESIZE )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        autofitRowHeight( getMouseRowPosition() - 1 );
      else
        autofitRowHeight( getMouseRowPosition() );
    }
  }

  /**************************************** mouseEntered *****************************************/
  protected void mouseEntered( MouseEvent event )
  {
    // mouse has entered table
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    checkMouseCellPosition();
    if ( m_selecting == Selecting.NONE )
      checkMouseCursor();

    m_vScrollBar.stopAnimation();
    m_hScrollBar.stopAnimation();
  }

  /***************************************** mouseExited *****************************************/
  protected void mouseExited( MouseEvent event )
  {
    // mouse has left table
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    setMouseColumnPosition( INVALID );
    setMouseRowPosition( INVALID );

    m_vScrollBar.stopAnimation();
    m_hScrollBar.stopAnimation();
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which table cell mouse is over and set cursor appropriately (no dragging)
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    checkMouseCellPosition();
    checkMouseCursor();
  }

  /**************************************** mouseDragged *****************************************/
  protected void mouseDragged( MouseEvent event )
  {
    // user is moving mouse with button down
    m_x = (int) event.getX();
    m_y = (int) event.getY();

    // check if column resizing
    if ( getCursor() == Cursors.H_RESIZE && m_resizeIndex >= FIRSTCELL )
    {
      m_columns.setCellPixels( m_resizeIndex, m_x - m_resizeOffset );
      m_cellXend = INVALID;
      widthChange( getXStartFromColumnPos( m_columns.getPositionFromIndex( m_resizeIndex ) ),
          (int) m_canvas.getWidth() );
      layoutDisplay();
      return;
    }

    // check if row resizing
    if ( getCursor() == Cursors.V_RESIZE && m_resizeIndex >= FIRSTCELL )
    {
      m_rows.setCellPixels( m_resizeIndex, m_y - m_resizeOffset );
      m_cellYend = INVALID;
      heightChange( getYStartFromRowPos( m_rows.getPositionFromIndex( m_resizeIndex ) ), (int) m_canvas.getWidth() );
      layoutDisplay();
      return;
    }

    // check mouse cell position after any resizing
    checkMouseCellPosition();

    // determine whether any horizontal scrolling needed
    if ( m_x >= m_canvas.getWidth() && m_selecting != Selecting.ROWS )
      m_hScrollBar.scrollToEnd( m_x - m_canvas.getWidth() );
    else if ( m_x < getRowHeaderWidth() && m_selecting != Selecting.ROWS )
      m_hScrollBar.scrollToStart( getRowHeaderWidth() - m_x );
    else
      m_hScrollBar.stopAnimationStartEnd();

    // determine whether any vertical scrolling needed
    if ( m_y >= m_canvas.getHeight() && m_selecting != Selecting.COLUMNS )
      m_vScrollBar.scrollToEnd( m_y - m_canvas.getHeight() );
    else if ( m_y < getColumnHeaderHeight() && m_selecting != Selecting.COLUMNS )
      m_vScrollBar.scrollToStart( getColumnHeaderHeight() - m_y );
    else
      m_vScrollBar.stopAnimationStartEnd();

    // check if selecting only if no animation happening
    if ( m_hScrollBar.getAnimation() == Animation.NONE && m_vScrollBar.getAnimation() == Animation.NONE )
    {
      // check if cell selecting
      if ( m_selecting == Selecting.CELLS )
      {
        int columnPos = Utils.clamp( getMouseColumnPosition(), m_columns.getFirst(), m_columns.getLast() );
        int rowPos = Utils.clamp( getMouseRowPosition(), m_rows.getFirst(), m_rows.getLast() );

        if ( columnPos != getSelectColumnPosition() || rowPos != getSelectRowPosition() )
        {
          setSelectFocusPosition( columnPos, rowPos, false, false, false );
          redraw();
        }
        return;
      }

      // check if column selecting
      if ( m_selecting == Selecting.COLUMNS )
      {
        int columnPos = Utils.clamp( getMouseColumnPosition(), m_columns.getFirst(), m_columns.getLast() );

        if ( columnPos != getSelectColumnPosition() )
        {
          setCurrentSelection( getFocusColumnPosition(), FIRSTCELL, columnPos, AFTER );
          setSelectColumnPosition( columnPos );
          m_hScrollBar.scrollTo( columnPos );
          redraw();
        }
        return;
      }

      // check if row selecting
      if ( m_selecting == Selecting.ROWS )
      {
        int rowPos = Utils.clamp( getMouseRowPosition(), m_rows.getFirst(), m_rows.getLast() );

        if ( rowPos != getSelectRowPosition() )
        {
          setCurrentSelection( FIRSTCELL, getFocusRowPosition(), AFTER, rowPos );
          setSelectRowPosition( rowPos );
          m_vScrollBar.scrollTo( rowPos );
          redraw();
        }
        return;
      }
    }

    // check if column reorder TODO
    if ( getCursor() == Cursors.H_MOVE )
    {
      Utils.trace( "TODO - column reordering !!!" );
      return;
    }

    // check if row reorder TODO
    if ( getCursor() == Cursors.V_MOVE )
    {
      Utils.trace( "TODO - row reordering !!!" );
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
      {
        m_vScrollBar.finishAnimation();
        m_vScrollBar.decrement();
      }
      else
      {
        m_vScrollBar.finishAnimation();
        m_vScrollBar.increment();
      }
    }
  }

  /**************************************** tableScrolled ****************************************/
  protected void tableScrolled()
  {
    // handle any actions needed due to the table scrolled
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    checkMouseCellPosition();

    // if mouse dragging happening, then check for selecting
    if ( m_selecting != Selecting.NONE )
    {
      int columnPos = getMouseColumnPosition();
      if ( columnPos < FIRSTCELL )
      {
        if ( m_hScrollBar.getValue() == m_hScrollBar.getMin() )
          columnPos = m_columns.getFirst();
        else
          columnPos = m_columns.getNext( getColumnPositionAtX( getRowHeaderWidth() ) );
      }
      if ( columnPos >= AFTER )
      {
        if ( m_hScrollBar.getValue() == m_hScrollBar.getMax() )
          columnPos = m_columns.getLast();
        else
          columnPos = m_columns.getPrevious( getColumnPositionAtX( (int) m_canvas.getWidth() - 1 ) );
      }

      int rowPos = getMouseRowPosition();
      if ( rowPos < FIRSTCELL )
      {
        if ( m_vScrollBar.getValue() == m_vScrollBar.getMin() )
          rowPos = m_columns.getFirst();
        else
          rowPos = m_rows.getNext( getRowPositionAtY( getColumnHeaderHeight() ) );
      }
      if ( rowPos >= AFTER )
      {
        if ( m_vScrollBar.getValue() == m_vScrollBar.getMax() )
          rowPos = m_columns.getLast();
        else
          rowPos = m_rows.getPrevious( getRowPositionAtY( (int) m_canvas.getHeight() - 1 ) );
      }

      if ( m_selecting == Selecting.CELLS )
        setSelectFocusPosition( columnPos, rowPos, false, false, false );
      else if ( m_selecting == Selecting.COLUMNS )
      {
        setCurrentSelection( getFocusColumnPosition(), FIRSTCELL, columnPos, AFTER );
        setSelectColumnPosition( columnPos );
      }
      else if ( m_selecting == Selecting.ROWS )
      {
        setCurrentSelection( FIRSTCELL, getFocusRowPosition(), AFTER, rowPos );
        setSelectRowPosition( rowPos );
      }
    }

    redraw();
  }

  /*********************************** checkMouseCellPosition ************************************/
  protected void checkMouseCellPosition()
  {
    // check if mouse moved outside current column
    int header = getRowHeaderWidth();
    if ( m_x < m_cellXstart || m_x >= m_cellXend )
    {
      int columnPos = INVALID;
      if ( m_x < 0 )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
        columnPos = BEFORE;
      }
      else if ( m_x >= m_canvas.getWidth() )
      {
        m_cellXstart = (int) m_canvas.getWidth();
        m_cellXend = Integer.MAX_VALUE;
        columnPos = AFTER;
      }
      else if ( m_x >= getTableWidth() )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
        columnPos = AFTER;
      }
      else if ( m_x < header )
      {
        m_cellXstart = 0;
        m_cellXend = header;
        columnPos = HEADER;
      }
      else
      {
        columnPos = getColumnPositionAtX( m_x );
        m_cellXstart = Math.max( getXStartFromColumnPos( columnPos ), header );
        m_cellXend = getXStartFromColumnPos( columnPos + 1 );
      }

      setMouseColumnPosition( columnPos );
    }

    // check if mouse moved outside current row
    header = getColumnHeaderHeight();
    if ( m_y < m_cellYstart || m_y >= m_cellYend )
    {
      int rowPos = INVALID;
      if ( m_y < 0 )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
        rowPos = BEFORE;
      }
      else if ( m_y >= m_canvas.getHeight() )
      {
        m_cellYstart = (int) m_canvas.getHeight();
        m_cellYend = Integer.MAX_VALUE;
        rowPos = AFTER;
      }
      else if ( m_y >= getTableHeight() )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
        rowPos = AFTER;
      }
      else if ( m_y < header )
      {
        m_cellYstart = 0;
        m_cellYend = header;
        rowPos = HEADER;
      }
      else
      {
        rowPos = getRowPositionAtY( m_y );
        m_cellYstart = Math.max( getYStartFromRowPos( rowPos ), header );
        m_cellYend = getYStartFromRowPos( rowPos + 1 );
      }

      setMouseRowPosition( rowPos );
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
      if ( m_cellXend - m_x <= PROXIMITY
          || ( m_x - m_cellXstart <= PROXIMITY && getMouseColumnPosition() != FIRSTCELL ) )
      {
        setCursor( Cursors.H_RESIZE );
        return;
      }

      // if column is selected, set cursor to move
      if ( isColumnSelected( getMouseColumnPosition() ) )
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
      if ( m_cellYend - m_y <= PROXIMITY || ( m_y - m_cellYstart <= PROXIMITY && getMouseRowPosition() != FIRSTCELL ) )
      {
        setCursor( Cursors.V_RESIZE );
        return;
      }

      // if row is selected, set cursor to move
      if ( isRowSelected( getMouseRowPosition() ) )
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
