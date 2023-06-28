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

    Utils.trace( event.getCode(), event );
  }

}
