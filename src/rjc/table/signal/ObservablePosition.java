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

package rjc.table.signal;

import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/********************* Observable position (column, row) & read-only position ********************/
/*************************************************************************************************/

public class ObservablePosition implements ISignal
{
  private int m_column = TableAxis.INVALID;
  private int m_row    = TableAxis.INVALID;

  /**************************************** constructor ******************************************/
  public ObservablePosition()
  {
    // construct
  }

  /**************************************** constructor ******************************************/
  public ObservablePosition( int column, int row )
  {
    // construct and set position
    m_column = column;
    m_row = row;
  }

  /***************************************** setPosition *****************************************/
  public void setPosition( int column, int row )
  {
    // if position is change, fire listeners
    if ( column != m_column || row != m_row )
    {
      m_column = column;
      m_row = row;
      signal();
    }
  }

}
