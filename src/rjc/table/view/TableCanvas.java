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

package rjc.table.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/*************************************************************************************************/
/********************** Canvas where the table headers and cells are drawn ***********************/
/*************************************************************************************************/

public class TableCanvas extends Canvas
{
  private TableView m_view; // associated table view

  /***************************************** constructor *****************************************/
  public TableCanvas( TableView view )
  {
    // setup table canvas
    setFocusTraversable( true );
    m_view = view;

    // when size changes draw new bits
    widthProperty().addListener( ( observable, oldW, newW ) ->
    {
      // Utils.trace( "Canvas width change", oldW.intValue(), newW.intValue() );

      GraphicsContext gc = getGraphicsContext2D();
      gc.clearRect( 0.0, 0.0, getWidth(), getHeight() );
      Paint colour = Color.hsb( 100, 0.5, 1.0 );
      gc.setFill( colour );
      gc.fillRect( 4, 4, getWidth() - 8, getHeight() - 8 );
    } );

    heightProperty().addListener( ( observable, oldH, newH ) ->
    {
      // Utils.trace( "Canvas height change", oldH.intValue(), newH.intValue() );

      GraphicsContext gc = getGraphicsContext2D();
      gc.clearRect( 0.0, 0.0, getWidth(), getHeight() );
      Paint colour = Color.hsb( 200, 0.5, 1.0 );
      gc.setFill( colour );
      gc.fillRect( 4, 4, getWidth() - 8, getHeight() - 8 );
    } );

    // redraw table when focus changes
    focusedProperty().addListener( ( observable, oldF, newF ) ->
    {
      // TODO #####################################
      // Utils.trace( "Canvas focus change", oldF.booleanValue(), newF.booleanValue() );
    } );
  }

}
