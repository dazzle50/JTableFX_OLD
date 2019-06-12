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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import rjc.table.view.TableView;

/*************************************************************************************************/
/********************** Example customised table view for extra large table **********************/
/*************************************************************************************************/

public class LargeView extends TableView
{
  private int m_mouseColumnIndex = INVALID;
  private int m_mouseRowIndex    = INVALID;

  /**************************************** constructor ******************************************/
  public LargeView( LargeData data )
  {
    // construct customised table view
    super( data );
    m_columns.setCellSize( HEADER, 60 );

    // when mouse moved redraw old and new column to move highlighting
    getMouseColumnProperty().addListener( ( observable, oldColumn, newColumn ) ->
    {
      int old_index = m_mouseColumnIndex;
      m_mouseColumnIndex = m_columns.getIndexFromPosition( newColumn.intValue() );
      redrawColumn( old_index );
      redrawColumn( m_mouseColumnIndex );
      redrawOverlay();
    } );

    // when mouse moved redraw old and new row to move highlighting
    getMouseRowProperty().addListener( ( observable, oldRow, newRow ) ->
    {
      int old_index = m_mouseRowIndex;
      m_mouseRowIndex = m_rows.getIndexFromPosition( newRow.intValue() );
      redrawRow( old_index );
      redrawRow( m_mouseRowIndex );
      redrawOverlay();
    } );
  }

  /**************************************** constructor ******************************************/
  @Override
  protected Paint getCellBackgroundPaint()
  {
    // get default background paint
    Paint paint = super.getCellBackgroundPaint();

    // if white background
    if ( paint == Color.WHITE )
    {
      // highlight cell green where mouse is positioned
      if ( m_columnIndex == m_mouseColumnIndex && m_rowIndex == m_mouseRowIndex )
        return Color.PALEGREEN;

      // highlight row and column pale green where mouse is positioned
      if ( m_columnIndex == m_mouseColumnIndex || m_rowIndex == m_mouseRowIndex )
        return Color.PALEGREEN.desaturate().desaturate().desaturate().desaturate();
    }

    // otherwise default
    return paint;
  }

}
