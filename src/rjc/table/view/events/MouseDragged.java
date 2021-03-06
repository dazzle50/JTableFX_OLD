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
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import rjc.table.view.TableScrollBar;
import rjc.table.view.TableView;
import rjc.table.view.actions.Reorder;
import rjc.table.view.actions.Resize;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.MousePosition;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/************* Handles mouse drag (move with button pressed) events from table-view **************/
/*************************************************************************************************/

public class MouseDragged implements EventHandler<MouseEvent>
{
  TableView m_view;
  int       m_x;
  int       m_y;
  int       m_columnPos;
  int       m_rowPos;
  Cursor    m_cursor;

  /******************************************* handle ********************************************/
  @Override
  public void handle( MouseEvent event )
  {
    // exit immediately if not dragging with primary mouse button
    event.consume();
    if ( event.getButton() != MouseButton.PRIMARY )
      return;

    // handle mouse drag events (movement with button pressed)
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    m_view = (TableView) event.getSource();
    m_view.requestFocus();

    // update mouse cell position
    MousePosition mouse = m_view.getMouseCell();
    mouse.setXY( m_x, m_y, false );
    m_columnPos = mouse.getColumnPos();
    m_rowPos = mouse.getRowPos();

    // update or stop view scrolling depending on mouse position
    m_cursor = m_view.getCursor();
    viewScrolling();

    // check if selecting
    if ( m_cursor == Cursors.CROSS )
      m_view.getSelectCell().setPosition( m_columnPos, m_rowPos );
    else if ( m_cursor == Cursors.DOWNARROW )
    {
      if ( m_columnPos == TableAxis.AFTER )
        m_columnPos = m_view.getData().getColumnCount() - 1;
      m_view.getSelectCell().setPosition( m_columnPos, TableAxis.AFTER );
    }
    else if ( m_cursor == Cursors.RIGHTARROW )
    {
      if ( m_rowPos == TableAxis.AFTER )
        m_rowPos = m_view.getData().getRowCount() - 1;
      m_view.getSelectCell().setPosition( TableAxis.AFTER, m_rowPos );
    }

    // check if ending resize columns or rows
    else if ( m_cursor == Cursors.H_RESIZE )
      Resize.drag( m_x );
    else if ( m_cursor == Cursors.V_RESIZE )
      Resize.drag( m_y );

    // check if ending reorder columns or rows
    else if ( m_cursor == Cursors.H_MOVE )
      Reorder.drag( m_x );
    else if ( m_cursor == Cursors.V_MOVE )
      Reorder.drag( m_y );
  }

  /**************************************** viewScrolling ****************************************/
  private void viewScrolling()
  {
    // update or stop view scrolling depending on mouse position
    int header = m_view.getHeaderWidth();

    // determine whether any horizontal scrolling needed
    TableScrollBar scrollbar = m_view.getHorizontalScrollBar();
    int width = (int) m_view.getCanvas().getWidth();
    boolean scroll = m_cursor != Cursors.V_RESIZE && m_cursor != Cursors.V_MOVE && m_cursor != Cursors.RIGHTARROW;
    if ( scroll && m_x >= width && scrollbar.getValue() < scrollbar.getMax() )
    {
      scrollbar.scrollToEnd( m_x - width );
      m_columnPos = m_view.getColumnsAxis().getPrevious( m_view.getColumnPositionAtX( width ) );
    }
    else if ( scroll && m_x < header && scrollbar.getValue() > 0.0 )
    {
      scrollbar.scrollToStart( header - m_x );
      m_columnPos = m_view.getColumnsAxis().getNext( m_view.getColumnPositionAtX( header ) );
    }
    else
      scrollbar.stopAnimationStartEnd();

    // determine whether any vertical scrolling needed
    scrollbar = m_view.getVerticalScrollBar();
    int height = (int) m_view.getCanvas().getHeight();
    header = m_view.getHeaderHeight();
    scroll = m_cursor != Cursors.H_RESIZE && m_cursor != Cursors.H_MOVE && m_cursor != Cursors.DOWNARROW;
    if ( scroll & m_y >= height && scrollbar.getValue() < scrollbar.getMax() )
    {
      scrollbar.scrollToEnd( m_y - height );
      m_rowPos = m_view.getRowsAxis().getPrevious( m_view.getRowPositionAtY( height ) );
    }
    else if ( scroll && m_y < header && scrollbar.getValue() > 0.0 )
    {
      scrollbar.scrollToStart( header - m_y );
      m_rowPos = m_view.getRowsAxis().getNext( m_view.getRowPositionAtY( header ) );
    }
    else
      scrollbar.stopAnimationStartEnd();
  }

}
