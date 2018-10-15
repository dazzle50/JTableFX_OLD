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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import rjc.table.view.TableView;

/*************************************************************************************************/
/********************** Example customised table view for extra large table **********************/
/*************************************************************************************************/

public class LargeView extends TableView
{

  /**************************************** constructor ******************************************/
  public LargeView( LargeData data )
  {
    // construct customised table view
    super( data );
    setRowHeaderWidth( 60 );

    // when mouse moved redraw old and new column to move highlighting
    mouseColumnIndex.addListener( ( observable, oldColumn, newColumn ) ->
    {
      redrawColumn( oldColumn.intValue() );
      redrawColumn( newColumn.intValue() );
    } );

    // when mouse moved redraw old and new row to move highlighting
    mouseRowIndex.addListener( ( observable, oldRow, newRow ) ->
    {
      redrawRow( oldRow.intValue() );
      redrawRow( newRow.intValue() );
    } );
  }

  /**************************************** constructor ******************************************/
  @Override
  protected Paint getCellBackgroundPaint()
  {
    int columnIndex = mouseColumnIndex.get();
    int rowIndex = mouseRowIndex.get();

    // highlight cell blue where mouse is positioned
    if ( m_columnIndex == columnIndex && m_rowIndex == rowIndex )
      return Color.CORNFLOWERBLUE;

    // highlight row and column light blue where mouse is positioned
    if ( m_columnIndex == columnIndex || m_rowIndex == rowIndex )
      return Color.ALICEBLUE;

    // otherwise default
    return super.getCellBackgroundPaint();
  }

}
