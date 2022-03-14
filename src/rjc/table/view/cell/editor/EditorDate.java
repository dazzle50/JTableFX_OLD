/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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

package rjc.table.view.cell.editor;

import rjc.table.control.DateField;
import rjc.table.data.Date;

/*************************************************************************************************/
/********************************** Table cell editor for dates **********************************/
/*************************************************************************************************/

public class EditorDate extends CellEditorBase
{
  private DateField m_dateEditor = new DateField();

  /**************************************** constructor ******************************************/
  public EditorDate()
  {
    // create table cell editor for date
    super();
    setControl( m_dateEditor );
  }

  /******************************************* getValue ******************************************/
  @Override
  public Object getValue()
  {
    // get editor date value
    return m_dateEditor.getDate();
  }

  /******************************************* setValue ******************************************/
  @Override
  public void setValue( Object value )
  {
    // set value depending on type
    if ( value == null )
      m_dateEditor.setDate( Date.now() );
    else if ( value instanceof Date )
      m_dateEditor.setDate( (Date) value );
    else if ( value instanceof String )
    {
      // seed editor with a valid date before setting with input string which may not be a valid date
      m_dateEditor.setDate( Date.now() );
      m_dateEditor.setText( (String) value );
      m_dateEditor.positionCaret( ( (String) value ).length() );
    }
    else
      throw new IllegalArgumentException( "Don't know how to handle " + value.getClass() + " " + value );
  }

}
