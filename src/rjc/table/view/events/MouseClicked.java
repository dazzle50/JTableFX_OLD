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
import rjc.table.Utils;
import rjc.table.view.TableView;
import rjc.table.view.cursor.Cursors;

/*************************************************************************************************/
/************************* Handles mouse clicked events from table-view **************************/
/*************************************************************************************************/

public class MouseClicked implements EventHandler<MouseEvent>
{

  /******************************************* handle ********************************************/
  @Override
  public void handle( MouseEvent event )
  {
    // user has clicked the table
    event.consume();
    TableView view = (TableView) event.getSource();
    Cursor cursor = view.getCursor();

    // double-click on table body to start cell editor with cell contents
    boolean doubleClick = event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY;
    if ( doubleClick && cursor == Cursors.CROSS )
      view.openEditor();

    // double-click on column header resize to autofit
    if ( doubleClick && cursor == Cursors.H_RESIZE )
      autofitColumnWidth();

    // double-click on row header resize to autofit
    if ( doubleClick && cursor == Cursors.V_RESIZE )
      autofitRowHeight();
  }

  /************************************* autofitColumnWidth **************************************/
  private void autofitColumnWidth()
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO - not yet implemented" );
  }

  /************************************** autofitRowHeight ***************************************/
  private void autofitRowHeight()
  {
    // TODO Auto-generated method stub
    Utils.trace( "TODO - not yet implemented" );
  }

}
