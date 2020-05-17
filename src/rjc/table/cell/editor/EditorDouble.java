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

package rjc.table.cell.editor;

import rjc.table.cell.NumberSpinField;

/*************************************************************************************************/
/****************************** Table cell spin editor for double ********************************/
/*************************************************************************************************/

public class EditorDouble extends CellEditorBase
{
  private NumberSpinField m_spin = new NumberSpinField();

  /**************************************** constructor ******************************************/
  public EditorDouble()
  {
    // create spin table cell editor for double
    super();
    m_spin.setFormat( "0.0", 1 );
    setControl( m_spin );
  }

  /******************************************* getValue ******************************************/
  @Override
  public Object getValue()
  {
    // get editor double value
    return m_spin.getDouble();
  }

  /******************************************* setValue ******************************************/
  @Override
  public void setValue( Object value )
  {
    // set value depending on type
    if ( value instanceof Double )
      m_spin.setDouble( (double) value );
    else
      m_spin.setValue( value.toString() );
  }

}
