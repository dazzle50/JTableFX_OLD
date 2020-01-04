/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

package rjc.table.demo;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import rjc.table.cell.CellContext;
import rjc.table.cell.CellEditorBase;
import rjc.table.cell.EditorDouble;
import rjc.table.cell.EditorInteger;
import rjc.table.cell.EditorText;
import rjc.table.view.TableView;

/*************************************************************************************************/
/*********************** Example customised table view for editable table ************************/
/*************************************************************************************************/

public class EditView extends TableView
{
  protected final static Insets CELL_TEXT_INSERTS = new Insets( 0.0, 5.0, 1.0, 4.0 );

  /**************************************** constructor ******************************************/
  public EditView( EditData data )
  {
    // construct customised table view
    super( data );

    getColumns().setCellSize( EditData.SECTION_READONLY, 120 );
    getColumns().setCellSize( EditData.SECTION_TEXT, 120 );
    getColumns().setCellSize( EditData.SECTION_INTEGER, 80 );
    getColumns().setCellSize( EditData.SECTION_DOUBLE, 80 );
    getColumns().setCellSize( EditData.SECTION_DATETIME, 200 );
  }

  /************************************ getCellTextAlignment *************************************/
  @Override
  protected Pos getCellTextAlignment( CellContext cell )
  {
    // return left alignment for the two text columns
    if ( cell.rowIndex > HEADER )
      if ( cell.columnIndex == EditData.SECTION_READONLY || cell.columnIndex == EditData.SECTION_TEXT )
        return Pos.CENTER_LEFT;

    // otherwise centre alignment
    return Pos.CENTER;
  }

  /************************************** getCellTextInsets **************************************/
  @Override
  protected Insets getCellTextInsets( CellContext cell )
  {
    // return cell text insets
    return CELL_TEXT_INSERTS;
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

    // listen to editor status
    if ( editor != null )
      editor.getStatusProperty().addListener( ( property, oldS, newS ) -> DemoWindow.setStatus( newS ) );

    return editor;
  }

}
