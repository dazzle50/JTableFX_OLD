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

import rjc.table.Utils;
import rjc.table.data.Date;
import rjc.table.data.DateTime;
import rjc.table.data.TableData;
import rjc.table.data.Time;

/*************************************************************************************************/
/******************** Example customised table data source for editable table ********************/
/*************************************************************************************************/

public class EditData extends TableData
{
  public static final int SECTION_READONLY = 0;
  public static final int SECTION_TEXT     = 1;
  public static final int SECTION_INTEGER  = 2;
  public static final int SECTION_DOUBLE   = 3;
  public static final int SECTION_DATE     = 4;
  public static final int SECTION_TIME     = 5;
  public static final int SECTION_DATETIME = 6;
  public static final int SECTION_MAX      = SECTION_DATETIME;

  private final int       ROWS             = 20;

  private String[]        m_readonly       = new String[ROWS];
  private String[]        m_text           = new String[ROWS];
  private int[]           m_integer        = new int[ROWS];
  private double[]        m_double         = new double[ROWS];
  private Date[]          m_date           = new Date[ROWS];
  private Time[]          m_time           = new Time[ROWS];
  private DateTime[]      m_datetime       = new DateTime[ROWS];

  /**************************************** constructor ******************************************/
  public EditData()
  {
    // populate the private variables with table contents
    super();

    for ( int row = 0; row < ROWS; row++ )
    {
      m_readonly[row] = "Read-only text " + ( row + 1 );
      m_text[row] = "Editable text " + ( row + 1 );
      m_integer[row] = row + 100;
      m_double[row] = row + 10.0;
      m_date[row] = Date.now().plusDays( row * 5 - 20 );
      m_time[row] = Time.now().addMilliseconds( row * 12345678 );
      m_datetime[row] = new DateTime( m_date[row], m_time[row] );
    }
  }

  /*************************************** getColumnCount ****************************************/
  @Override
  public int getColumnCount()
  {
    // return number of columns to be displayed in table
    return SECTION_MAX + 1;
  }

  /**************************************** getRowCount ******************************************/
  @Override
  public int getRowCount()
  {
    // return number of rows to be displayed in table
    return ROWS;
  }

  /************************************** getColumnTitle *****************************************/
  @Override
  public String getColumnTitle( int columnIndex )
  {
    // return column title for specified column index
    switch ( columnIndex )
    {
      case SECTION_READONLY:
        return "ReadOnly";
      case SECTION_TEXT:
        return "Text";
      case SECTION_INTEGER:
        return "Integer";
      case SECTION_DOUBLE:
        return "Double";
      case SECTION_DATE:
        return "Date";
      case SECTION_TIME:
        return "Time";
      case SECTION_DATETIME:
        return "DateTime";
      default:
        throw new IllegalArgumentException( "Column index = " + columnIndex );
    }
  }

  /**************************************** getRowTitle ******************************************/
  @Override
  public String getRowTitle( int rowIndex )
  {
    // return row title for specified row
    return String.valueOf( rowIndex + 1 );
  }

  /****************************************** getValue *******************************************/
  @Override
  public Object getValue( int columnIndex, int rowIndex )
  {
    // return cell value for specified cell index
    switch ( columnIndex )
    {
      case SECTION_READONLY:
        return m_readonly[rowIndex];
      case SECTION_TEXT:
        return m_text[rowIndex];
      case SECTION_INTEGER:
        return m_integer[rowIndex];
      case SECTION_DOUBLE:
        return m_double[rowIndex];
      case SECTION_DATE:
        return m_date[rowIndex];
      case SECTION_TIME:
        return m_time[rowIndex];
      case SECTION_DATETIME:
        return m_datetime[rowIndex];
      default:
        throw new IllegalArgumentException( "Column index = " + columnIndex );
    }
  }

  /****************************************** setValue *******************************************/
  @Override
  public void setValue( int columnIndex, int rowIndex, Object newValue )
  {
    // set cell value for specified cell index
    Utils.trace( columnIndex, rowIndex, newValue );
  }

}
