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
import javafx.scene.input.KeyEvent;
import rjc.table.Utils;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************************* Handles keyboard typed events for table-view **************************/
/*************************************************************************************************/

public class KeyTyped implements EventHandler<KeyEvent>
{

  /******************************************* handle ********************************************/
  @Override
  public void handle( KeyEvent event )
  {
    // user typed using keyboard
    event.consume();
    var view = (TableView) event.getSource();
    var focus = view.getFocusCell();
    var select = view.getSelectCell();

    Utils.trace( event );
  }

}
