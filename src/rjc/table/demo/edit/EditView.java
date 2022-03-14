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

package rjc.table.demo.edit;

import rjc.table.demo.edit.EditData.Fruit;
import rjc.table.view.TableView;
import rjc.table.view.cell.CellContext;
import rjc.table.view.cell.CellDraw;
import rjc.table.view.cell.editor.CellEditorBase;
import rjc.table.view.cell.editor.EditorChoose;
import rjc.table.view.cell.editor.EditorDate;
import rjc.table.view.cell.editor.EditorDateTime;
import rjc.table.view.cell.editor.EditorDouble;
import rjc.table.view.cell.editor.EditorInteger;
import rjc.table.view.cell.editor.EditorText;
import rjc.table.view.cell.editor.EditorTime;

/*************************************************************************************************/
/*********************** Example customised table view for editable table ************************/
/*************************************************************************************************/

public class EditView extends TableView
{
  /**************************************** constructor ******************************************/
  public EditView( EditData data )
  {
    // construct customised table view
    super( data );

    getColumnsAxis().setCellSize( EditData.SECTION_READONLY, 120 );
    getColumnsAxis().setCellSize( EditData.SECTION_TEXT, 120 );
    getColumnsAxis().setCellSize( EditData.SECTION_INTEGER, 80 );
    getColumnsAxis().setCellSize( EditData.SECTION_DOUBLE, 80 );
    getColumnsAxis().setCellSize( EditData.SECTION_DATETIME, 200 );
  }

  /**************************************** getCellDrawer ****************************************/
  @Override
  public CellDraw getCellDrawer()
  {
    // return new instance of class that draws table cells
    return new EditCellDraw();
  }

  /**************************************** getCellEditor ****************************************/
  @Override
  public CellEditorBase getCellEditor( CellContext cell )
  {
    // determine editor appropriate for cell
    CellEditorBase editor = null;
    if ( cell.columnIndex == EditData.SECTION_TEXT )
      editor = new EditorText();
    if ( cell.columnIndex == EditData.SECTION_INTEGER )
      editor = new EditorInteger();
    if ( cell.columnIndex == EditData.SECTION_DOUBLE )
      editor = new EditorDouble();
    if ( cell.columnIndex == EditData.SECTION_DATE )
      editor = new EditorDate();
    if ( cell.columnIndex == EditData.SECTION_TIME )
      editor = new EditorTime();
    if ( cell.columnIndex == EditData.SECTION_DATETIME )
      editor = new EditorDateTime();
    if ( cell.columnIndex == EditData.SECTION_CHOOSE )
      editor = new EditorChoose( Fruit.values() );

    return editor;
  }

}
