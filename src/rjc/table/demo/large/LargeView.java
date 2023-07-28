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

package rjc.table.demo.large;

import rjc.table.data.TableData;
import rjc.table.view.TableView;
import rjc.table.view.cell.CellDrawer;

/*************************************************************************************************/
/********************** Example customised table view for extra large table **********************/
/*************************************************************************************************/

public class LargeView extends TableView
{
  private CellDrawer m_drawer;

  /**************************************** constructor ******************************************/
  public LargeView( TableData data )
  {
    // construct customised table view
    super( data );

    // when mouse moved to new cell, redraw table to move shading
    getMouseCell().addListener( ( mousePosition, oldPos ) -> redraw() );
  }

  /**************************************** getCellDrawer ****************************************/
  @Override
  public CellDrawer getCellDrawer()
  {
    // return class responsible for drawing the cells on canvas
    if ( m_drawer == null )
      m_drawer = new LargeCellDrawer( this );
    return m_drawer;
  }

  /******************************************** reset ********************************************/
  @Override
  public void reset()
  {
    // reset table view to default settings with wider header
    super.reset();
    getColumnsAxis().setHeaderSize( 60 );
  }

}
