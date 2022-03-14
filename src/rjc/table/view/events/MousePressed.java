/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import rjc.table.view.TableView;
import rjc.table.view.actions.Reorder;
import rjc.table.view.actions.Resize.HorizontalResize;
import rjc.table.view.actions.Resize.VerticalResize;
import rjc.table.view.axis.TableAxis;
import rjc.table.view.cell.MousePosition;
import rjc.table.view.cell.ViewPosition;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/********************** Handles mouse button pressed events from table-view **********************/
/*************************************************************************************************/

public class MousePressed implements EventHandler<MouseEvent>
{

  /******************************************* handle ********************************************/
  @Override
  public void handle( MouseEvent event )
  {
    {
      // user has pressed a mouse button
      event.consume();
      int x = (int) event.getX();
      int y = (int) event.getY();
      TableView view = (TableView) event.getSource();
      MouseButton button = event.getButton();
      ViewPosition select = view.getSelectCell();
      ViewPosition focus = view.getFocusCell();
      MousePosition mouse = view.getMouseCell();
      Cursor cursor = view.getCursor();

      // clear status, request focus & update mouse cell position and cursor
      view.getStatus().clear();
      view.requestFocus();
      mouse.setXY( x, y, true );

      // clear previous selections if shift & ctrl are not pressed
      boolean clearPrevious = !event.isShiftDown() && !event.isControlDown()
          && ( cursor == Cursors.CROSS || cursor == Cursors.DOWNARROW || cursor == Cursors.RIGHTARROW );
      if ( clearPrevious )
        view.getSelection().clear();

      // if primary mouse button not pressed, don't do anything else
      if ( button != MouseButton.PRIMARY )
        return;

      // check if selecting table body cells
      boolean moveFocus = !event.isShiftDown() || event.isControlDown();
      if ( cursor == Cursors.CROSS )
      {
        if ( moveFocus )
        {
          view.getSelection().start();
          focus.setPosition( mouse );
        }
        select.setPosition( mouse );
      }

      // check if selecting table columns
      else if ( cursor == Cursors.DOWNARROW )
      {
        if ( moveFocus )
        {
          view.getSelection().start();
          int topRow = view.getRowPositionAtY( view.getHeaderHeight() );
          focus.setPosition( mouse.getColumnPos(), topRow );
        }
        select.setPosition( mouse.getColumnPos(), TableAxis.AFTER );
      }

      // check if selecting table rows
      else if ( cursor == Cursors.RIGHTARROW )
      {
        if ( moveFocus )
        {
          view.getSelection().start();
          int leftColumn = view.getColumnPositionAtX( view.getHeaderWidth() );
          focus.setPosition( leftColumn, mouse.getRowPos() );
        }
        select.setPosition( TableAxis.AFTER, mouse.getRowPos() );
      }

      // check if resizing
      else if ( cursor == Cursors.H_RESIZE )
        HorizontalResize.start( view, x );
      else if ( cursor == Cursors.V_RESIZE )
        VerticalResize.start( view, y );

      // check if reordering
      else if ( cursor == Cursors.H_MOVE )
        Reorder.start( view, Orientation.HORIZONTAL, x );
      else if ( cursor == Cursors.V_MOVE )
        Reorder.start( view, Orientation.VERTICAL, y );

      // check if header corner to select whole table
      else if ( x < view.getHeaderWidth() && y < view.getHeaderHeight() )
      {
        view.getSelection().selectAll();
        view.redraw();
      }
    }

  }
}
