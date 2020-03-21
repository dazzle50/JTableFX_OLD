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

package rjc.table.demo;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import rjc.table.cell.CellDraw;
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

    // when mouse moved to new cell, redraw table to move shading
    getMouseCellProperty().addListener( ( observable ) -> redraw() );
  }

  /**************************************** getCellDrawer ****************************************/
  @Override
  public CellDraw getCellDrawer()
  {
    // return new instance of class that draws table cells
    return new CellDraw()
    {
      /*********************************** getBackgroundPaint ************************************/
      @Override
      protected Paint getBackgroundPaint()
      {
        // get default background paint
        Paint paint = super.getBackgroundPaint();

        // if white background shade different colour if mouse pointer on row/column
        if ( paint == Color.WHITE )
        {
          int col = view.getMouseCellProperty().getColumnPos();
          int row = view.getMouseCellProperty().getRowPos();

          // highlight cell green where mouse is positioned
          if ( columnPos == col && rowPos == row )
            return Color.PALEGREEN;

          // highlight row and column pale green where mouse is positioned
          if ( columnPos == col || rowPos == row )
            return Color.PALEGREEN.desaturate().desaturate().desaturate().desaturate();
        }

        // otherwise default
        return paint;
      };
    };
  }

  /******************************************** reset ********************************************/
  @Override
  public void reset()
  {
    // reset table view to default settings with wider header
    super.reset();
    getColumns().setHeaderSize( 60 );
  }

}
