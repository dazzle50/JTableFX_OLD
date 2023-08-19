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

package rjc.table.view.action;

import rjc.table.undo.CommandZoom;
import rjc.table.view.TableView;

/*************************************************************************************************/
/***************************** Set table-view zoom via undo command ******************************/
/*************************************************************************************************/

public class Zoom
{
  /***************************************** setZoom ******************************************/
  public static void setZoom( TableView view, double zoom )
  {
    // check if can merge with previous undo command
    var command = view.getUndoStack().getUndoCommand();
    if ( command instanceof CommandZoom && ( (CommandZoom) command ).getView() == view )
    {
      // merge with previous zoom command
      CommandZoom zc = (CommandZoom) command;
      zc.setZoom( zoom );
      zc.redo();
      view.getUndoStack().signal();
    }
    else
    {
      // create new command for zoom change
      CommandZoom zc = new CommandZoom( view, view.getZoom().get(), zoom );
      view.getUndoStack().push( zc );
    }
  }

}
