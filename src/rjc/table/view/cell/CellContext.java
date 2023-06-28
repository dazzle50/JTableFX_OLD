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

package rjc.table.view.cell;

import javafx.scene.canvas.GraphicsContext;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************************************ Table cell view context ************************************/
/*************************************************************************************************/

public class CellContext
{
  public int             columnIndex; // cell column index
  public int             rowIndex;    // cell row index

  public TableView       view;        // table view
  public GraphicsContext gc;          // graphics context drawing
  public double          x;           // start x coordinate of cell on canvas
  public double          y;           // start y coordinate of cell on canvas
  public double          w;           // width of cell on canvas
  public double          h;           // height of cell on canvas

  /**************************************** constructor ******************************************/
  public CellContext()
  {
    // create empty table cell context
  }

  /**************************************** constructor ******************************************/
  public CellContext( TableView view, int columnIndex, int rowIndex )
  {
    // create new table cell context
    setIndex( view, columnIndex, rowIndex );
  }

  /****************************************** setIndex *******************************************/
  public void setIndex( TableView view, int columnIndex, int rowIndex )
  {
    // set cell context for cell index
    this.view = view;
    this.columnIndex = columnIndex;
    this.rowIndex = rowIndex;

    gc = view.getCanvas().getGraphicsContext2D();
    x = view.getColumnStartX( columnIndex );
    y = view.getRowStartY( rowIndex );
    w = view.getColumnsAxis().getIndexPixels( columnIndex );
    h = view.getRowsAxis().getIndexPixels( rowIndex );
  }

}