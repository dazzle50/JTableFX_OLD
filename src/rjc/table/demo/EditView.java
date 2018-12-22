/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

    setColumnIndexWidth( EditData.SECTION_READONLY, 120 );
    setColumnIndexWidth( EditData.SECTION_TEXT, 120 );
    setColumnIndexWidth( EditData.SECTION_INTEGER, 80 );
    setColumnIndexWidth( EditData.SECTION_DOUBLE, 80 );
    setColumnIndexWidth( EditData.SECTION_DATETIME, 200 );
  }

  /************************************ getCellTextAlignment *************************************/
  @Override
  protected Pos getCellTextAlignment()
  {
    // return left alignment for the two text columns
    if ( m_rowIndex > HEADER )
      if ( m_columnIndex == EditData.SECTION_READONLY || m_columnIndex == EditData.SECTION_TEXT )
        return Pos.CENTER_LEFT;

    // otherwise centre alignment
    return Pos.CENTER;
  }

  /************************************** getCellTextInsets **************************************/
  @Override
  protected Insets getCellTextInsets()
  {
    // return cell text insets
    return CELL_TEXT_INSERTS;
  }

}
