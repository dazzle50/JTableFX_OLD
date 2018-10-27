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
    widthProperty().addListener( ( observable, oldW, newW ) -> m_view.widthChange( oldW.intValue(), newW.intValue() ) );
    heightProperty()
        .addListener( ( observable, oldH, newH ) -> m_view.heightChange( oldH.intValue(), newH.intValue() ) );

    // redraw table when focus changes
    focusedProperty().addListener( ( observable, oldF, newF ) -> m_view.redraw() );

    // react to mouse events
    setOnMouseExited( event -> m_view.mouseExited( event ) );
    setOnMouseMoved( event -> m_view.mouseMoved( event ) );
    setOnMouseDragged( event -> m_view.mouseDragged( event ) );
    setOnMouseReleased( event -> m_view.mouseReleased( event ) );
    setOnMousePressed( event -> m_view.mousePressed( event ) );
    setOnMouseClicked( event -> m_view.mouseClicked( event ) );
    setOnScroll( event -> m_view.mouseScroll( event ) );

    // react to keyboard events
    setOnKeyPressed( event -> m_view.keyPressed( event ) );
    setOnKeyTyped( event -> m_view.keyTyped( event ) );
  }

}
