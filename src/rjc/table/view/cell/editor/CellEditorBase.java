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

package rjc.table.view.cell.editor;

import javafx.scene.control.Control;
import rjc.table.view.cell.CellStyle;

/*************************************************************************************************/
/****************************** Base class for a table cell editor *******************************/
/*************************************************************************************************/

public class CellEditorBase
{
  private static CellEditorBase m_cellEditorInProgress; // only one editor can be open at any time

  private Control               m_control;              // primary control that has focus
  private CellStyle             m_cell;                 // cell style and position etc

  /***************************************** constructor *****************************************/
  public CellEditorBase()
  {
    // nothing needs doing here
  }

  /***************************************** getControl ******************************************/
  public Control getControl()
  {
    // return editor main control
    return m_control;
  }
}