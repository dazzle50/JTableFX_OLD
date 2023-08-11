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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************* Handles mouse drag (move with button pressed) events from table-view **************/
/*************************************************************************************************/

public class MouseDragged implements EventHandler<MouseEvent>
{

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
    int x = (int) event.getX();
    int y = (int) event.getY();
    TableView view = (TableView) event.getSource();

    // update mouse cell position
    view.getMouseCell().setXY( x, y, false );
  }
}
