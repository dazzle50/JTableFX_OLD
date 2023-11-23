/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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
import rjc.table.view.action.Resize;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/************* Handles mouse drag (move with button pressed) events from table-view **************/
/*************************************************************************************************/

public class MouseDragged implements EventHandler<MouseEvent>
{
  private TableView m_view;
  private int       m_x;
  private int       m_y;
  private Cursor    m_cursor;

  /******************************************* handle ********************************************/
  @Override
  public void handle( MouseEvent event )
  {
    // exit immediately if not dragging with primary mouse button
    event.consume();
    if ( event.getButton() != MouseButton.PRIMARY )
      return;

    // handle mouse drag events (movement with button pressed)
    event.consume();
    m_x = (int) event.getX();
    m_y = (int) event.getY();
    m_view = (TableView) event.getSource();
    m_cursor = m_view.getCursor();

    // check if table scrolling is wanted
    verticalScrolling();
    horizontalScrolling();

    // update mouse cell position
    m_view.getMouseCell().setXY( m_x, m_y, false );

    // check if resize columns or rows
    if ( m_cursor == Cursors.H_RESIZE )
      Resize.drag( m_x );
    if ( m_cursor == Cursors.V_RESIZE )
      Resize.drag( m_y );
  }

  /************************************* horizontalScrolling *************************************/
  private void horizontalScrolling()
  {
    // determine whether any horizontal scrolling needed
    TableScrollBar scrollbar = m_view.getHorizontalScrollBar();
    int header = m_view.getHeaderWidth();
    int width = (int) m_view.getCanvas().getWidth();
    boolean scroll = m_cursor == Cursors.H_RESIZE || m_cursor == Cursors.H_MOVE || m_cursor == Cursors.SELECTING_CELLS
        || m_cursor == Cursors.SELECTING_COLS;

    // update or stop view scrolling depending on mouse position
    if ( scroll && m_x >= width && scrollbar.getValue() < scrollbar.getMax() )
      scrollbar.scrollToEnd( m_x - width );
    else if ( scroll && m_x < header && scrollbar.getValue() > 0.0 )
      scrollbar.scrollToStart( header - m_x );
    else
      scrollbar.stopAnimationStartEnd();
  }

  /************************************** verticalScrolling **************************************/
  private void verticalScrolling()
  {
    // determine whether any vertical scrolling needed
    TableScrollBar scrollbar = m_view.getVerticalScrollBar();
    int height = (int) m_view.getCanvas().getHeight();
    int header = m_view.getHeaderHeight();
    boolean scroll = m_cursor == Cursors.V_RESIZE || m_cursor == Cursors.V_MOVE || m_cursor == Cursors.SELECTING_CELLS
        || m_cursor == Cursors.SELECTING_ROWS;

    // update or stop view scrolling depending on mouse position
    if ( scroll & m_y >= height && scrollbar.getValue() < scrollbar.getMax() )
      scrollbar.scrollToEnd( m_y - height );
    else if ( scroll && m_y < header && scrollbar.getValue() > 0.0 )
      scrollbar.scrollToStart( header - m_y );
    else
      scrollbar.stopAnimationStartEnd();
  }
}
