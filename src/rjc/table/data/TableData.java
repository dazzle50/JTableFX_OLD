/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

package rjc.table.data;

import rjc.table.undo.UndoStack;

/*************************************************************************************************/
/*********************** Table data source (with default implementations) ************************/
/*************************************************************************************************/

public class TableData extends TableBase
{
  private UndoStack m_undostack; // undo-stack for the table data

  /****************************************** getValue *******************************************/
  public Object getValue( int columnIndex, int rowIndex )
  {
    // return header corner cell value
    if ( columnIndex == HEADER && rowIndex == HEADER )
      return "-";

    // return row value for specified row index
    if ( columnIndex == HEADER )
      return "R" + rowIndex;

    // return column value for specified column index
    if ( rowIndex == HEADER )
      return "C" + columnIndex;

    // return cell value for specified cell index
    return "{" + columnIndex + "," + rowIndex + "}";
  }

  /****************************************** setValue *******************************************/
  public boolean setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // returns true if cell value successfully set for specified cell index
    return false;
  }

  /***************************************** getUndoStack ****************************************/
  public UndoStack getUndoStack()
  {
    // return the data undo-stack (create if necessary)
    if ( m_undostack == null )
      m_undostack = new UndoStack();
    return m_undostack;
  }

  /***************************************** setUndoStack ****************************************/
  public void setUndoStack( UndoStack undostack )
  {
    // set the data undo-stack
    m_undostack = undostack;
  }

}
