/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

package rjc.table.cell;

import rjc.table.data.DateTime;

/*************************************************************************************************/
/******************************* Table cell editor for date-times ********************************/
/*************************************************************************************************/

public class EditorDateTime extends CellEditorBase
{
  private DateTimeField m_datetimeEditor = new DateTimeField();

  /**************************************** constructor ******************************************/
  public EditorDateTime()
  {
    // create table cell editor for date-time
    super();
    setControl( m_datetimeEditor );
  }

  /******************************************* getValue ******************************************/
  @Override
  public Object getValue()
  {
    // get editor date-time value
    return m_datetimeEditor.getDateTime();
  }

  /******************************************* setValue ******************************************/
  @Override
  public void setValue( Object value )
  {
    // set value depending on type
    if ( value == null )
      m_datetimeEditor.setDateTime( DateTime.now() );
    else if ( value instanceof DateTime )
      m_datetimeEditor.setDateTime( (DateTime) value );
    else if ( value instanceof String )
    {
      // seed editor with a valid date-time before setting with input string which may not be a valid date-time
      m_datetimeEditor.setDateTime( DateTime.now() );
      m_datetimeEditor.setText( (String) value );
      m_datetimeEditor.positionCaret( ( (String) value ).length() );
    }
    else
      throw new IllegalArgumentException( "Don't know how to handle " + value.getClass() + " " + value );
  }

}
