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

import javafx.geometry.Orientation;
import javafx.scene.paint.Color;
import rjc.table.data.TableData;
import rjc.table.support.Utils;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableDraw
{

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // setup and register table view
    m_view = this;
    m_data = data;
    data.register( m_view );

    // create table canvas and scroll bars
    m_canvas = new TableCanvas( m_view );
    m_vScrollBar = new TableScrollBar( m_view, Orientation.VERTICAL );
    m_hScrollBar = new TableScrollBar( m_view, Orientation.HORIZONTAL );

    // add canvas and scroll bars to parent displayed children
    add( m_canvas );
    add( m_vScrollBar );
    add( m_hScrollBar );
  }

  /****************************************** drawCell *******************************************/
  public void drawCell()
  {
    // TODO #########################################################################
    Utils.trace( m_columnIndex, m_rowIndex, x, y, w, h, gc );

    gc.setFill( Color.hsb( ( m_columnIndex + m_rowIndex ) * 20, 1.0, 1.0 ) );
    gc.fillRect( x, y, w, h );
  }

}
