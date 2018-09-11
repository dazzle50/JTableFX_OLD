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

import rjc.table.data.TableData;
import rjc.table.support.Utils;

/*************************************************************************************************/
/********************************** Base class for table views ***********************************/
/*************************************************************************************************/

public class TableView extends TableSelection
{

  /**************************************** constructor ******************************************/
  public TableView( TableData data )
  {
    // construct table view
    m_view = this;
    m_data = data;
    data.register( m_view );
  }

  /***************************************** redrawCell ******************************************/
  public void redrawCell( int columnIndex, int rowIndex )
  {
    // TODO Auto-generated method stub #########################################################################
    Utils.trace( columnIndex, rowIndex );
  }

  /**************************************** redrawColumn *****************************************/
  public void redrawColumn( int columnIndex )
  {
    // TODO Auto-generated method stub #########################################################################
    Utils.trace( columnIndex );
  }

  /****************************************** redrawRow ******************************************/
  public void redrawRow( int rowIndex )
  {
    // TODO Auto-generated method stub #########################################################################
    Utils.trace( rowIndex );
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // TODO Auto-generated method stub #########################################################################
    Utils.trace( "RESET" );
  }

}
