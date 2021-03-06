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
import rjc.table.view.TableView;
import rjc.table.view.actions.Reorder;
import rjc.table.view.actions.Resize;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/********************* Handles mouse button released events from table-view **********************/
/*************************************************************************************************/

public class MouseReleased implements EventHandler<MouseEvent>
{

  /******************************************* handle ********************************************/
  @Override
  public void handle( MouseEvent event )
  {
    // user has released a mouse button
    event.consume();
    int x = (int) event.getX();
    int y = (int) event.getY();
    TableView view = (TableView) event.getSource();

    // check for ending resize and reorder before updating cursor
    if ( event.getButton() == MouseButton.PRIMARY )
    {
      Cursor cursor = view.getCursor();

      // check if ending resize column or row
      if ( cursor == Cursors.H_RESIZE || cursor == Cursors.V_RESIZE )
        Resize.end();

      // check if ending column or row reordering
      if ( cursor == Cursors.H_MOVE || cursor == Cursors.V_MOVE )
        Reorder.end();
    }

    // update mouse cell position and cursor
    view.getMouseCell().setXY( x, y, true );

    // stop any scrolling to edges
    view.getHorizontalScrollBar().stopAnimationStartEnd();
    view.getVerticalScrollBar().stopAnimationStartEnd();
  }

}
