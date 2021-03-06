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

package rjc.table.view.events;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import rjc.table.Utils;
import rjc.table.view.TableScrollBar;
import rjc.table.view.TableView;
import rjc.table.view.actions.Content;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/************************ Handles keyboard pressed events for table-view *************************/
/*************************************************************************************************/

public class KeyPressed implements EventHandler<KeyEvent>
{
  private TableView m_view;
  private boolean   m_shift;
  private boolean   m_ctrl;
  private boolean   m_alt;

  /******************************************* handle ********************************************/
  @Override
  public void handle( KeyEvent event )
  {
    // user has pressed a keyboard key
    event.consume();
    m_view = (TableView) event.getSource();
    m_shift = event.isShiftDown();
    m_ctrl = event.isControlDown();
    m_alt = event.isAltDown();

    // handle control keys
    if ( m_ctrl && !m_alt )
      switch ( event.getCode() )
      {
        case A: // select whole table (Ctrl-A)
          m_view.getSelection().selectAll();
          return;

        case X: // cut cells contents (Ctrl-X)
          Content.cut( m_view );
          return;

        case C: // copy cells contents (Ctrl-C)
          Content.copy( m_view );
          return;

        case V: // paste cells contents (Ctrl-V)
          Content.paste( m_view );
          return;

        case D: // fill-down cells contents (Ctrl-D)
          Content.fillDown( m_view );
          return;

        case Z: // undo command (Ctrl-Z)
          m_view.getUndoStack().undo();
          return;

        case Y: // redo command (Ctrl-Y)
          m_view.getUndoStack().redo();
          return;

        case MINUS: // zoom out (Ctrl-minus)
        case SUBTRACT:
          //commandZoom( getZoom() / Math.pow( 2.0, 0.0625 ) );
          return;

        case EQUALS: // zoom in (Ctrl-plus)
        case ADD:
          //commandZoom( getZoom() * Math.pow( 2.0, 0.0625 ) );
          return;

        case DIGIT0: // reset zoom 1:1 (Ctrl-0)
          //commandZoom( 1.0 );
          return;

        default:
          break;
      }

    // handle arrow, page, home, end, insert, delete, F2 & F5 keys
    if ( !m_alt )
      switch ( event.getCode() )
      {
        case RIGHT: // right -> arrow key
        case KP_RIGHT:
          moveRight();
          return;

        case LEFT: // left <- arrow key
        case KP_LEFT:
          moveLeft();
          return;

        case DOWN: // down arrow key
        case KP_DOWN:
          moveDown();
          return;

        case UP: // up arrow key
        case KP_UP:
          moveUp();
          return;

        case PAGE_DOWN: // page down key
          pageDown();
          return;

        case PAGE_UP: // page up key
          pageUp();
          return;

        case HOME: // home key - navigate to left-most visible column
          moveHome();
          return;

        case END: // end key - navigate to right-most visible column
          moveEnd();
          return;

        case DELETE: // delete key - delete selected cells content
          Content.delete();
          return;

        case INSERT: // insert key - insert row or column
          Content.insert();
          return;

        case F2: // F2 key - open cell editor with current focus cell contents
          openEditor();
          return;

        case F5: // F5 key - redraw table
          m_view.redraw();
          return;

        default:
          break;
      }

  }

  /****************************************** moveRight ******************************************/
  private void moveRight()
  {
    // move selected and focus cell position right
    if ( m_ctrl )
      m_view.getSelectCell().moveRightEdge();
    else
      m_view.getSelectCell().moveRight();

    if ( !m_shift )
      m_view.getFocusCell().setPosition( m_view.getSelectCell() );
  }

  /****************************************** moveLeft *******************************************/
  private void moveLeft()
  {
    // move selected and focus cell position left
    if ( m_ctrl )
      m_view.getSelectCell().moveLeftEdge();
    else
      m_view.getSelectCell().moveLeft();

    if ( !m_shift )
      m_view.getFocusCell().setPosition( m_view.getSelectCell() );
  }

  /****************************************** moveDown *******************************************/
  private void moveDown()
  {
    // move selected and focus cell position down
    if ( m_ctrl )
      m_view.getSelectCell().moveBottom();
    else
      m_view.getSelectCell().moveDown();

    if ( !m_shift )
      m_view.getFocusCell().setPosition( m_view.getSelectCell() );
  }

  /******************************************* moveUp ********************************************/
  private void moveUp()
  {
    // move selected and focus cell position up
    if ( m_ctrl )
      m_view.getSelectCell().moveTop();
    else
      m_view.getSelectCell().moveUp();

    if ( !m_shift )
      m_view.getFocusCell().setPosition( m_view.getSelectCell() );
  }

  /****************************************** pageDown *******************************************/
  private void pageDown()
  {
    // scroll table down one page
    var scrollbar = m_view.getVerticalScrollBar();
    scrollbar.finishAnimation();
    int value = (int) scrollbar.getValue();

    if ( scrollbar.isVisible() && value < scrollbar.getMax() )
    {
      // bottom of table not visible
      int header = m_view.getHeaderHeight();
      int canvas = (int) m_view.getCanvas().getHeight();
      int newTopRow = m_view.getRowPositionAtY( canvas + 1 );

      // make sure scroll down at least one row
      if ( newTopRow == m_view.getRowPositionAtY( header ) )
        newTopRow = m_view.getRowsAxis().getNext( newTopRow );
      int newYScroll = m_view.getRowsAxis().getStartFromPosition( newTopRow, 0 ) - header;

      // determine new position for select/focus
      int rowPos = m_view.getSelectCell().getRowPos();
      int ySelect = ( m_view.getYStartFromRowPos( rowPos ) + m_view.getYStartFromRowPos( rowPos + 1 ) ) / 2;
      ySelect = Utils.clamp( ySelect, header, canvas );
      rowPos = m_view.getRowPositionAtY( ySelect + newYScroll - value );
      if ( rowPos >= TableAxis.AFTER )
        rowPos = m_view.getRowsAxis().getLast();
      boolean rowNotCompletelyVisible = m_view.getYStartFromRowPos( rowPos + 1 ) > canvas + newYScroll - value;
      boolean rowAboveCompletelyVisible = m_view
          .getYStartFromRowPos( m_view.getRowsAxis().getPrevious( rowPos ) ) > header + newYScroll - value;
      if ( rowNotCompletelyVisible && rowAboveCompletelyVisible )
        rowPos = m_view.getRowsAxis().getPrevious( rowPos );

      // move select/focus and scroll table
      Utils.trace( rowPos, value );
      m_view.getSelectCell().setRowPos( rowPos );
      if ( !m_shift )
        m_view.getFocusCell().setPosition( m_view.getSelectCell() );
      scrollbar.animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
    }
    else
    {
      // bottom of table already visible so move to last row
      m_view.getSelectCell().moveBottom();
      if ( !m_shift )
        m_view.getFocusCell().setPosition( m_view.getSelectCell() );
    }
  }

  /******************************************* pageUp ********************************************/
  private void pageUp()
  {
    // scroll table up one page
    var scrollbar = m_view.getVerticalScrollBar();
    scrollbar.finishAnimation();
    int value = (int) scrollbar.getValue();

    if ( scrollbar.isVisible() && value > 0 )
    {
      // top of table not visible
      int header = m_view.getHeaderHeight();
      int canvas = (int) m_view.getCanvas().getHeight();
      int bottomRow = m_view.getRowPositionAtY( canvas );
      int newTopY = value + 2 * header - m_view.getYStartFromRowPos( bottomRow );

      // top of table not visible, make sure scroll up at least one row
      int newTopRow = m_view.getRowsAxis().getPositionFromCoordinate( newTopY, 0 );
      if ( newTopRow <= TableAxis.HEADER )
        newTopRow = m_view.getRowsAxis().getFirst();
      if ( newTopRow == m_view.getRowPositionAtY( header ) )
        newTopRow = m_view.getRowsAxis().getPrevious( newTopRow );
      int newYScroll = m_view.getRowsAxis().getStartFromPosition( newTopRow, 0 ) - header;

      // determine new position for select/focus
      int rowPos = m_view.getSelectCell().getRowPos();
      int ySelect = ( m_view.getYStartFromRowPos( rowPos ) + m_view.getYStartFromRowPos( rowPos + 1 ) ) / 2;
      ySelect = Utils.clamp( ySelect, header, canvas );
      rowPos = m_view.getRowsAxis().getPositionFromCoordinate( ySelect, newYScroll );
      if ( rowPos <= TableAxis.HEADER )
        rowPos = m_view.getRowsAxis().getFirst();

      // move select/focus and scroll table
      m_view.getSelectCell().setRowPos( rowPos );
      if ( !m_shift )
        m_view.getFocusCell().setPosition( m_view.getSelectCell() );
      scrollbar.animate( newYScroll, TableScrollBar.SCROLL_TO_DURATION );
    }
    else
    {
      // top of table already visible so move to first row
      m_view.getSelectCell().moveTop();
      if ( !m_shift )
        m_view.getFocusCell().setPosition( m_view.getSelectCell() );
    }
  }

  /****************************************** moveHome *******************************************/
  private void moveHome()
  {
    // move selected and focus cell position home (left edge)
    m_view.getSelectCell().moveLeftEdge();
    if ( !m_shift )
      m_view.getFocusCell().setPosition( m_view.getSelectCell() );
  }

  /****************************************** moveEnd ********************************************/
  private void moveEnd()
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO - Not implemented yet" );
  }

  /***************************************** openEditor ******************************************/
  private void openEditor()
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO - Not implemented yet" );
  }

  /**
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
    **/

}
