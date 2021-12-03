/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

package rjc.table.undo;

import java.util.HashMap;

import rjc.table.Utils;
import rjc.table.data.TableData;
import rjc.table.view.TableView;

/*************************************************************************************************/
/************* UndoCommand for settings multiple cell values at table-view position **************/
/*************************************************************************************************/

public class CommandPasteCells implements IUndoCommand
{
  private TableData                m_data;        // table data model
  private int                      m_columnCount; // used to hash column & row indexes
  private HashMap<Integer, Object> m_oldValues;   // cell value before paste
  private HashMap<Integer, Object> m_newValues;   // cell value after paste
  private String                   m_text;        // text describing command

  /**************************************** constructor ******************************************/
  public CommandPasteCells( TableView view, int columnPos, int rowPos, Object[] array, String string )
  {
    // initialise private variables
    m_data = view.getData();
    m_columnCount = m_data.getColumnCount();
    m_oldValues = new HashMap<>();
    m_newValues = new HashMap<>();

    // xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

    if ( array != null )
    {
      Utils.trace( "Contains JTABLEFX !!!" );
      for ( int col = 0; col < array.length; col++ )
        Utils.trace( "Column " + col, array[col] );
    }

    // convert string into array
    var stringArray = convertToArray( string );

  }

  private Object convertToArray( String string )
  {
    // TODO Auto-generated method stub
    return null;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // set pasted cells to their new values
    m_newValues.forEach( ( hash, newValue ) ->
    {
      int col = hash % m_columnCount;
      int row = hash / m_columnCount;
      m_data.setValue( col, row, newValue );
      m_data.redrawCell( col, row );
    } );
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // set pasted cells back to their original values
    m_oldValues.forEach( ( hash, oldValue ) ->
    {
      int col = hash % m_columnCount;
      int row = hash / m_columnCount;
      m_data.setValue( col, row, oldValue );
      m_data.redrawCell( col, row );
    } );
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    if ( m_text == null )
      m_text = "Pasted " + m_oldValues.size() + " cell" + ( m_oldValues.size() == 1 ? "" : "s" );
    return m_text;
  }

  /******************************************* isValid *******************************************/
  @Override
  public boolean isValid()
  {
    // command is only ready and valid when old-values is not empty
    return !m_oldValues.isEmpty();
  }

}
