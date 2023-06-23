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

import rjc.table.Utils;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

public class CellDrawer extends CellStyle
{

  /**************************************** constructor ******************************************/
  public CellDrawer( TableView tableView )
  {
    // prepare the cell drawer
    view = tableView;
    gc = view.getCanvas().getGraphicsContext2D();
  }

  /******************************************** draw *********************************************/
  public void draw()
  {
    // clip drawing to cell boundaries
    gc.save();
    gc.beginPath();

    if ( columnIndex == TableAxis.HEADER || rowIndex == TableAxis.HEADER )
      gc.rect( x, y, w, h );
    else
    {
      double cx = x > view.getHeaderWidth() ? x : view.getHeaderWidth();
      double cy = y > view.getHeaderHeight() ? y : view.getHeaderHeight();
      double cw = w + x - cx;
      double ch = h + y - cy;
      gc.rect( cx, cy, cw, ch );
    }

    gc.clip();
    drawUnclipped();

    // remove clip
    gc.restore();
  }

  /**************************************** drawUnclipped ****************************************/
  public void drawUnclipped()
  {
    // draw table body or header cell
    drawBackground();
    drawContent();
    drawBorder();
  }

  /*************************************** drawBackground ****************************************/
  protected void drawBackground()
  {
    // draw cell background
    gc.setFill( getBackgroundPaint() );
    gc.fillRect( x, y, w, h );
  }

  /***************************************** drawBorder ******************************************/
  protected void drawBorder()
  {
    // draw cell border
    gc.setStroke( getBorderPaint() );
    gc.strokeLine( x + w - 0.5, y + 0.5, x + w - 0.5, y + h - 0.5 );
    gc.strokeLine( x + 0.5, y + h - 0.5, x + w - 1.5, y + h - 0.5 );
  }

  /**************************************** drawContent ******************************************/
  protected void drawContent()
  {
    // draw cell contents
    drawText( getText() );
  }

  /****************************************** drawText *******************************************/
  protected void drawText( String cellText )
  {
    // get text inserts, font, alignment, and convert string into text lines
    Utils.trace( "TODO", cellText );
  }

}
