/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

package rjc.table.data;

/*************************************************************************************************/
/*********************** Table data source (with default implementations) ************************/
/*************************************************************************************************/

public class TableData extends TableBase
{

  /*************************************** getColumnCount ****************************************/
  public int getColumnCount()
  {
    // return number of columns to be displayed in table
    return 3;
  }

  /**************************************** getRowCount ******************************************/
  public int getRowCount()
  {
    // return number of rows to be displayed in table
    return 10;
  }

  /************************************** getColumnTitle *****************************************/
  public String getColumnTitle( int columnIndex )
  {
    // return column title for specified column index
    return "C" + columnIndex;
  }

  /**************************************** getRowTitle ******************************************/
  public String getRowTitle( int rowIndex )
  {
    // return row title for specified row
    return "R" + rowIndex;
  }

  /****************************************** getValue *******************************************/
  public Object getValue( int columnIndex, int rowIndex )
  {
    // return cell value for specified cell index
    return "{" + columnIndex + "," + rowIndex + "}";
  }

  /****************************************** setValue *******************************************/
  public void setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // set cell value for specified cell index
  }

}
