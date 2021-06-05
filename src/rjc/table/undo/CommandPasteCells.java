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

package rjc.table.undo;

import rjc.table.view.TableView;

/*************************************************************************************************/
/************* UndoCommand for settings multiple cell values at table-view position **************/
/*************************************************************************************************/

public class CommandPasteCells implements IUndoCommand
{

  /**************************************** constructor ******************************************/
  public CommandPasteCells( TableView view, int columnPos, int rowPos, Object[] array )
  {
    // TODO Auto-generated constructor stub
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // TODO Auto-generated method stub

  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // TODO Auto-generated method stub

  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // TODO Auto-generated method stub
    return null;
  }

  /******************************************** push *********************************************/
  @Override
  public boolean push( UndoStack undostack )
  {
    // do not redo command when pushed onto undo-stack
    return undostack.push( this, false );
  }

}
