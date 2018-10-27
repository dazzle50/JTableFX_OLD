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

package rjc.table.demo;

import rjc.table.data.TableData;

/*************************************************************************************************/
/****************** Example customised table data source for extra large table *******************/
/*************************************************************************************************/

public class LargeData extends TableData
{

  /*************************************** getColumnCount ****************************************/
  @Override
  public int getColumnCount()
  {
    // very large number of columns
    return 1000000;
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // very large number of rows
    return 1000000;
  }

}