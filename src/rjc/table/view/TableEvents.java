/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

import javafx.geometry.Orientation;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import rjc.table.Utils;
import rjc.table.cell.CellDraw;
import rjc.table.cell.CellEditorBase;
import rjc.table.undo.CommandZoom;
import rjc.table.view.TableScrollBar.Animation;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/*************************** Handles canvas mouse and keyboard events ****************************/
/*************************************************************************************************/

public class TableEvents extends TableSelect
{
  private int              m_x;                       // latest event mouse x coordinate
  private int              m_y;                       // latest event mouse y coordinate
  private int              m_cellXstart;              // current mouse cell X start
  private int              m_cellXend;                // current mouse cell X end
  private int              m_cellYstart;              // current mouse cell Y start
  private int              m_cellYend;                // current mouse cell Y end

  private Selecting        m_selecting;               // current cell selecting status

  private static final int PROXIMITY = 4;             // used to distinguish resize from reorder
  private static Reorder   m_reorder = new Reorder(); // supports column and row reordering
  private static Resize    m_resize  = new Resize();  // supports column and row resizing

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
    if ( !Character.isISOControl( key ) && getFocusCellProperty().isBody() )
      openEditor( event.getCharacter() );

    getView().redraw();
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // handle key presses
    boolean shift = event.isShiftDown();
    boolean ctrl = event.isControlDown();
    boolean alt = event.isAltDown();
    int columnPos = shift ? getSelectCellProperty().getColumnPos() : getFocusCellProperty().getColumnPos();
    int rowPos = shift ? getSelectCellProperty().getRowPos() : getFocusCellProperty().getRowPos();
    event.consume();

    // handle control keys
    if ( ctrl && !alt )
      switch ( event.getCode() )
      {
        case A: // select whole table (Ctrl-A)
          controlAPressed();
          return;

        case X: // cut cells contents (Ctrl-X)
          controlXPressed();
          return;

        case C: // copy cells contents (Ctrl-C)
          controlCPressed();
          return;

        case V: // paste cells contents (Ctrl-V)
          controlVPressed();
          return;

        case D: // fill-down cells contents (Ctrl-D)
          controlDPressed();
          return;

        case Z: // undo command (Ctrl-Z)
          controlZPressed();
          return;

        case Y: // redo command (Ctrl-Y)
          controlYPressed();
          return;

        case MINUS: // zoom out (Ctrl-minus)
        case SUBTRACT:
          commandZoom( getZoom() / Math.pow( 2.0, 0.0625 ) );
          return;

        case EQUALS: // zoom in (Ctrl-plus)
        case ADD:
          commandZoom( getZoom() * Math.pow( 2.0, 0.0625 ) );
          return;

        case DIGIT0: // reset zoom 1:1 (Ctrl-0)
          commandZoom( 1.0 );
          return;

        default:
          break;
      }

    // handle arrow, page, home, end, insert, delete, F2 & F5 keys
    if ( !alt )
      switch ( event.getCode() )
      {
        case RIGHT: // right -> arrow key
        case KP_RIGHT:
          columnPos = ctrl ? getColumns().getLast() : getColumns().getNext( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          getView().redraw();
          return;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          columnPos = ctrl ? getColumns().getFirst() : getColumns().getPrevious( columnPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          getView().redraw();
          return;

        case DOWN: // down arrow key
        case KP_DOWN:
          rowPos = ctrl ? getRows().getLast() : getRows().getNext( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          getView().redraw();
          return;

        case UP: // up arrow key
        case KP_UP:
          rowPos = ctrl ? getRows().getFirst() : getRows().getPrevious( rowPos );
          setSelectFocusPosition( columnPos, rowPos, !shift, !shift, true );
          getView().redraw();
          return;

        case PAGE_DOWN: // page down key
          getVerticalScrollBar().finishAnimation();
          if ( getVerticalScrollBar().isVisible()
              && getVerticalScrollBar().getValue() < getVerticalScrollBar().getMax() )
          {
            // bottom of table not visible, make sure scroll down at least one row
            int newTopRow = getRowPositionAtY( (int) getCanvas().getHeight() + 1 );
            if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
              newTopRow = getRows().getNext( newTopRow );
            int newYScroll = getRows().getStartFromPosition( newTopRow, 0 ) - getColumnHeaderHeight();

            // determine new position for select/focus 
            int ySelect = ( getYStartFromRowPos( rowPos ) + getYStartFromRowPos( rowPos + 1 ) ) / 2;
            ySelect = Utils.clamp( ySelect, getColumnHeaderHeight(), (int) getCanvas().getHeight() );
            rowPos = getRowPositionAtY( ySelect + newYScroll - (int) getVerticalScrollBar().getValue() );
            if ( rowPos >= AFTER )
              rowPos = getRows().getLast();
            boolean rowNotCompletelyVisible = getYStartFromRowPos( rowPos + 1 ) > getCanvas().getHeight() + newYScroll
                - (int) getVerticalScrollBar().getValue();
            boolean rowAboveCompletelyVisible = getYStartFromRowPos(
                getRows().getPrevious( rowPos ) ) > getColumnHeaderHeight() + newYScroll
                    - (int) getVerticalScrollBar().getValue();
            if ( rowNotCompletelyVisible && rowAboveCompletelyVisible )
              rowPos = getRows().getPrevious( rowPos );

            // move select/focus and scroll table
            setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
            getVerticalScrollBar().animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
          }
          else
          {
            // bottom of table already visible so move to last row
            setSelectFocusPosition( columnPos, getRows().getLast(), !shift, !ctrl, true );
            getView().redraw();
          }
          return;

        case PAGE_UP: // page up key
          getVerticalScrollBar().finishAnimation();
          if ( getVerticalScrollBar().isVisible() && getVerticalScrollBar().getValue() > 0.0 )
          {
            // top of table not visible, make sure scroll up at least one row
            int newTopY = (int) getVerticalScrollBar().getValue() + 2 * getColumnHeaderHeight()
                - (int) getCanvas().getHeight();
            int newTopRow = getRows().getPositionFromCoordinate( newTopY, 0 );
            if ( newTopRow <= HEADER )
              newTopRow = getRows().getFirst();
            if ( newTopRow == getRowPositionAtY( getColumnHeaderHeight() ) )
              newTopRow = getRows().getPrevious( newTopRow );
            int newYScroll = getRows().getStartFromPosition( newTopRow, 0 ) - getColumnHeaderHeight();

            // determine new position for select/focus
            int ySelect = ( getYStartFromRowPos( rowPos ) + getYStartFromRowPos( rowPos + 1 ) ) / 2;
            ySelect = Utils.clamp( ySelect, getColumnHeaderHeight(), (int) getCanvas().getHeight() );
            rowPos = getRows().getPositionFromCoordinate( ySelect, newYScroll );
            if ( rowPos <= HEADER )
              rowPos = getRows().getFirst();

            // move select/focus and scroll table
            setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
            getVerticalScrollBar().animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
          }
          else
          {
            // top of table visible so move to first row
            setSelectFocusPosition( columnPos, getRows().getFirst(), !shift, !ctrl, true );
            getView().redraw();
          }
          return;

        case HOME: // home key - navigate to left-most visible column
          setSelectFocusPosition( getColumns().getFirst(), getSelectCellProperty().getRowPos(), !shift, !ctrl, true );
          getView().redraw();
          return;

        case END: // end key - navigate to right-most visible column
          setSelectFocusPosition( getColumns().getLast(), getSelectCellProperty().getRowPos(), !shift, !ctrl, true );
          getView().redraw();
          return;

        case DELETE: // delete key - delete selected cells content
          deleteKeyPressed();
          return;

        case INSERT: // insert key - insert row or column
          insertKeyPressed();
          return;

        case F2: // F2 key - open cell editor with current focus cell contents
          int columnIndex = getColumns().getIndexFromPosition( getFocusCellProperty().getColumnPos() );
          int rowIndex = getRows().getIndexFromPosition( getFocusCellProperty().getRowPos() );
          openEditor( getData().getValue( columnIndex, rowIndex ) );
          return;

        case F5: // F5 key - redraw table
          getView().redraw();
          return;

        default:
          break;
      }

  }

  /***************************************** commandZoom *****************************************/
  public void commandZoom( double zoom )
  {
    // check if can merge with previous undo command 
    var command = getData().getUndoStack().getUndoCommand();
    if ( command instanceof CommandZoom && ( (CommandZoom) command ).isThisView( getView() ) )
    {
      // merge with previous zoom command
      CommandZoom zc = (CommandZoom) command;
      zc.setNewZoom( zoom );
      zc.redo();
      getData().getUndoStack().triggerListeners();
    }
    else
    {
      // create new command for zoom change
      CommandZoom zc = new CommandZoom( getView(), getZoom(), zoom );
      getData().getUndoStack().push( zc );
    }
  }

  /***************************************** setViewZoom *****************************************/
  public void setViewZoom( double zoom )
  {
    // is focus or select cells visible
    boolean focusVisible = getHorizontalScrollBar().isPosVisible( getFocusCellProperty().getColumnPos() )
        && getVerticalScrollBar().isPosVisible( getFocusCellProperty().getRowPos() );
    boolean selectVisible = getHorizontalScrollBar().isPosVisible( getSelectCellProperty().getColumnPos() )
        && getVerticalScrollBar().isPosVisible( getSelectCellProperty().getRowPos() );

    // set zoom scale for this view
    setZoom( zoom );

    // adjust table display and redraw
    CellEditorBase.endEditing();
    layoutDisplay();
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    checkMouseCellPosition();
    checkMouseCursor();

    // scroll if appropriate to keep focus and/or select cells visible
    if ( focusVisible )
    {
      getHorizontalScrollBar().scrollToPos( getFocusCellProperty().getColumnPos() );
      getVerticalScrollBar().scrollToPos( getFocusCellProperty().getRowPos() );
    }
    if ( selectVisible )
    {
      getHorizontalScrollBar().scrollToPos( getSelectCellProperty().getColumnPos() );
      getVerticalScrollBar().scrollToPos( getSelectCellProperty().getRowPos() );
    }
    getHorizontalScrollBar().finishAnimation();
    getVerticalScrollBar().finishAnimation();
  }

  /***************************************** openEditor ******************************************/
  protected void openEditor( Object value )
  {
    // get editor for focus cell
    CellDraw cell = getView().getCellDrawer();
    cell.setPosition( getView(), getFocusCellProperty().getColumnPos(), getFocusCellProperty().getRowPos() );
    CellEditorBase editor = getView().getCellEditor( cell );

    // open editor if provided and valid value
    if ( editor != null && editor.isValueValid( value ) )
      editor.open( value, cell );
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
    getView().redraw();
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

  /*************************************** controlZPressed ***************************************/
  protected void controlZPressed()
  {
    // undo command (Ctrl-Z)
    getData().getUndoStack().undo();
  }

  /*************************************** controlYPressed ***************************************/
  protected void controlYPressed()
  {
    // redo command (Ctrl-Y)
    getData().getUndoStack().redo();
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
      int columnPos = getMouseCellProperty().getColumnPos();
      int rowPos = getMouseCellProperty().getRowPos();

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      getView().redraw();
      return;
    }

    // check if column selected
    if ( getCursor() == Cursors.DOWNARROW && button == MouseButton.PRIMARY )
    {
      // get column and row positions
      m_selecting = Selecting.COLUMNS;
      int columnPos = getMouseCellProperty().getColumnPos();
      int rowPos = getRowPositionAtY( getColumnHeaderHeight() );

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      setSelectFocusPosition( columnPos, AFTER, false, !ctrl, false );
      getView().redraw();
      return;
    }

    // check if row selected
    if ( getCursor() == Cursors.RIGHTARROW && button == MouseButton.PRIMARY )
    {
      // get column and row positions
      m_selecting = Selecting.ROWS;
      int columnPos = getColumnPositionAtX( getRowHeaderWidth() );
      int rowPos = getMouseCellProperty().getRowPos();

      if ( ctrl )
        startNewSelection();
      setSelectFocusPosition( columnPos, rowPos, !shift, !ctrl, true );
      setSelectFocusPosition( AFTER, rowPos, false, !ctrl, false );
      getView().redraw();
      return;
    }

    // check if column resize started
    if ( getCursor() == Cursors.H_RESIZE && button == MouseButton.PRIMARY )
    {
      int pos = getMouseCellProperty().getColumnPos() - ( m_x - m_cellXstart < m_cellXend - m_x ? 1 : 0 );
      m_resize.start( getView(), Orientation.HORIZONTAL, pos, m_x );
    }

    // check if row resize started
    if ( getCursor() == Cursors.V_RESIZE && button == MouseButton.PRIMARY )
    {
      int pos = getMouseCellProperty().getRowPos() - ( m_y - m_cellYstart < m_cellYend - m_y ? 1 : 0 );
      m_resize.start( getView(), Orientation.VERTICAL, pos, m_y );
    }

    // check if whole table selected
    if ( getCursor() == Cursors.DEFAULT && getMouseCellProperty().getColumnPos() == HEADER
        && getMouseCellProperty().getRowPos() == HEADER )
    {
      selectTable();
      getView().redraw();
      return;
    }
  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // user has press a mouse button
    event.consume();
    checkMouseCursor();

    // stop any selecting, resizing, and reordering
    m_selecting = Selecting.NONE;
    m_resize.end();
    m_reorder.end();

    // stop any scrolling to edges
    getHorizontalScrollBar().stopAnimationStartEnd();
    getVerticalScrollBar().stopAnimationStartEnd();
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
      int columnIndex = getColumns().getIndexFromPosition( getFocusCellProperty().getColumnPos() );
      int rowIndex = getRows().getIndexFromPosition( getFocusCellProperty().getRowPos() );
      openEditor( getData().getValue( columnIndex, rowIndex ) );
    }

    // double-click on column header resize to autofit
    if ( doubleClick && getCursor() == Cursors.H_RESIZE )
    {
      if ( m_x - m_cellXstart < m_cellXend - m_x )
        autofitColumnWidth( getMouseCellProperty().getColumnPos() - 1 );
      else
        autofitColumnWidth( getMouseCellProperty().getColumnPos() );
    }

    // double-click on row header resize to autofit
    if ( doubleClick && getCursor() == Cursors.V_RESIZE )
    {
      if ( m_y - m_cellYstart < m_cellYend - m_y )
        autofitRowHeight( getMouseCellProperty().getRowPos() - 1 );
      else
        autofitRowHeight( getMouseCellProperty().getRowPos() );
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

    getVerticalScrollBar().stopAnimation();
    getHorizontalScrollBar().stopAnimation();
  }

  /***************************************** mouseExited *****************************************/
  protected void mouseExited( MouseEvent event )
  {
    // mouse has left table
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    m_cellXend = INVALID;
    m_cellYend = INVALID;
    getMouseCellProperty().setPosition( INVALID, INVALID );

    getVerticalScrollBar().stopAnimation();
    getHorizontalScrollBar().stopAnimation();
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

    // check if column/row resizing
    if ( getCursor() == Cursors.H_RESIZE )
      m_resize.resize( m_x );
    if ( getCursor() == Cursors.V_RESIZE )
      m_resize.resize( m_y );

    // check mouse cell position (need to do after any resizing)
    checkMouseCellPosition();

    // determine whether any horizontal scrolling needed
    boolean scroll = m_selecting != Selecting.ROWS && getCursor() != Cursors.V_RESIZE && getCursor() != Cursors.V_MOVE;
    if ( scroll && m_x >= getCanvas().getWidth() )
      getHorizontalScrollBar().scrollToEnd( m_x - getCanvas().getWidth() );
    else if ( scroll && m_x < getRowHeaderWidth() )
      getHorizontalScrollBar().scrollToStart( getRowHeaderWidth() - m_x );
    else
      getHorizontalScrollBar().stopAnimationStartEnd();

    // determine whether any vertical scrolling needed
    scroll = m_selecting != Selecting.COLUMNS && getCursor() != Cursors.H_RESIZE && getCursor() != Cursors.H_MOVE;
    if ( scroll & m_y >= getCanvas().getHeight() )
      getVerticalScrollBar().scrollToEnd( m_y - getCanvas().getHeight() );
    else if ( scroll && m_y < getColumnHeaderHeight() )
      getVerticalScrollBar().scrollToStart( getColumnHeaderHeight() - m_y );
    else
      getVerticalScrollBar().stopAnimationStartEnd();

    // check if selecting only if no animation happening
    if ( getHorizontalScrollBar().getAnimation() == Animation.NONE
        && getVerticalScrollBar().getAnimation() == Animation.NONE )
    {
      // check if cell selecting
      if ( m_selecting == Selecting.CELLS )
      {
        int columnPos = Utils.clamp( getMouseCellProperty().getColumnPos(), getColumns().getFirst(),
            getColumns().getLast() );
        int rowPos = Utils.clamp( getMouseCellProperty().getRowPos(), getRows().getFirst(), getRows().getLast() );

        if ( columnPos != getSelectCellProperty().getColumnPos() || rowPos != getSelectCellProperty().getRowPos() )
        {
          setSelectFocusPosition( columnPos, rowPos, false, false, false );
          getView().redraw();
        }
        return;
      }

      // check if column selecting
      if ( m_selecting == Selecting.COLUMNS )
      {
        int columnPos = Utils.clamp( getMouseCellProperty().getColumnPos(), getColumns().getFirst(),
            getColumns().getLast() );

        if ( columnPos != getSelectCellProperty().getColumnPos() )
        {
          setCurrentSelection( getFocusCellProperty().getColumnPos(), FIRSTCELL, columnPos, AFTER );
          getSelectCellProperty().setColumnPos( columnPos );
          getHorizontalScrollBar().scrollToPos( columnPos );
          getView().redraw();
        }
        return;
      }

      // check if row selecting
      if ( m_selecting == Selecting.ROWS )
      {
        int rowPos = Utils.clamp( getMouseCellProperty().getRowPos(), getRows().getFirst(), getRows().getLast() );

        if ( rowPos != getSelectCellProperty().getRowPos() )
        {
          setCurrentSelection( FIRSTCELL, getFocusCellProperty().getRowPos(), AFTER, rowPos );
          getSelectCellProperty().setRowPos( rowPos );
          getVerticalScrollBar().scrollToPos( rowPos );
          getView().redraw();
        }
        return;
      }
    }

    // check if column reorder
    if ( getCursor() == Cursors.H_MOVE )
    {
      if ( m_reorder.getOrientation() == null )
        m_reorder.start( getView(), Orientation.HORIZONTAL, getSelectedColumns() );
      if ( m_reorder.getOrientation() == Orientation.HORIZONTAL )
        m_reorder.setPlacement( m_x );
      return;
    }

    // check if row reorder
    if ( getCursor() == Cursors.V_MOVE )
    {
      if ( m_reorder.getOrientation() == null )
        m_reorder.start( getView(), Orientation.VERTICAL, getSelectedRows() );
      if ( m_reorder.getOrientation() == Orientation.VERTICAL )
        m_reorder.setPlacement( m_y );
      return;
    }
  }

  /**************************************** mouseScroll ******************************************/
  public void mouseScroll( ScrollEvent event )
  {
    // scroll up or down depending on mouse wheel scroll event
    if ( getVerticalScrollBar().isVisible() )
    {
      if ( event.getDeltaY() > 0 )
      {
        getVerticalScrollBar().finishAnimation();
        getVerticalScrollBar().decrement();
      }
      else
      {
        getVerticalScrollBar().finishAnimation();
        getVerticalScrollBar().increment();
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
      int columnPos = getMouseCellProperty().getColumnPos();
      if ( columnPos < FIRSTCELL )
      {
        if ( getHorizontalScrollBar().getValue() == getHorizontalScrollBar().getMin() )
          columnPos = getColumns().getFirst();
        else
          columnPos = getColumns().getNext( getColumnPositionAtX( getRowHeaderWidth() ) );
      }
      if ( columnPos >= AFTER )
      {
        if ( getHorizontalScrollBar().getValue() == getHorizontalScrollBar().getMax() )
          columnPos = getColumns().getLast();
        else
          columnPos = getColumns().getPrevious( getColumnPositionAtX( (int) getCanvas().getWidth() - 1 ) );
      }

      int rowPos = getMouseCellProperty().getRowPos();
      if ( rowPos < FIRSTCELL )
      {
        if ( getVerticalScrollBar().getValue() == getVerticalScrollBar().getMin() )
          rowPos = getColumns().getFirst();
        else
          rowPos = getRows().getNext( getRowPositionAtY( getColumnHeaderHeight() ) );
      }
      if ( rowPos >= AFTER )
      {
        if ( getVerticalScrollBar().getValue() == getVerticalScrollBar().getMax() )
          rowPos = getColumns().getLast();
        else
          rowPos = getRows().getPrevious( getRowPositionAtY( (int) getCanvas().getHeight() - 1 ) );
      }

      if ( m_selecting == Selecting.CELLS )
        setSelectFocusPosition( columnPos, rowPos, false, false, false );
      else if ( m_selecting == Selecting.COLUMNS )
      {
        setCurrentSelection( getFocusCellProperty().getColumnPos(), FIRSTCELL, columnPos, AFTER );
        getSelectCellProperty().setColumnPos( columnPos );
      }
      else if ( m_selecting == Selecting.ROWS )
      {
        setCurrentSelection( FIRSTCELL, getFocusCellProperty().getRowPos(), AFTER, rowPos );
        getSelectCellProperty().setRowPos( rowPos );
      }
    }

    // if column or row reordering, ensure reorder mark placed correctly
    if ( m_reorder.getOrientation() == Orientation.HORIZONTAL )
      m_reorder.setPlacement( m_x );
    if ( m_reorder.getOrientation() == Orientation.VERTICAL )
      m_reorder.setPlacement( m_y );

    // if column or row resizing, update
    if ( m_resize.getOrientation() == Orientation.HORIZONTAL )
      m_resize.resize( m_x );
    if ( m_resize.getOrientation() == Orientation.VERTICAL )
      m_resize.resize( m_y );

    // redraw table to reflect new scroll values
    getView().redraw();
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
      else if ( m_x >= getCanvas().getWidth() )
      {
        m_cellXstart = (int) getCanvas().getWidth();
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

      getMouseCellProperty().setColumnPos( columnPos );
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
      else if ( m_y >= getCanvas().getHeight() )
      {
        m_cellYstart = (int) getCanvas().getHeight();
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

      getMouseCellProperty().setRowPos( rowPos );
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
          || ( m_x - m_cellXstart <= PROXIMITY && getMouseCellProperty().getColumnPos() != FIRSTCELL ) )
      {
        setCursor( Cursors.H_RESIZE );
        return;
      }

      // if column is selected, set cursor to move
      if ( isColumnSelected( getMouseCellProperty().getColumnPos() ) )
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
      if ( m_cellYend - m_y <= PROXIMITY
          || ( m_y - m_cellYstart <= PROXIMITY && getMouseCellProperty().getRowPos() != FIRSTCELL ) )
      {
        setCursor( Cursors.V_RESIZE );
        return;
      }

      // if row is selected, set cursor to move
      if ( isRowSelected( getMouseCellProperty().getRowPos() ) )
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
