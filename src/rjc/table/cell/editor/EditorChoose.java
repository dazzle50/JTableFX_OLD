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

import rjc.table.cell.ChooseField;

/*************************************************************************************************/
/************************** Table cell spin editor for choosing object ***************************/
/*************************************************************************************************/

public class EditorChoose extends CellEditorBase
{
  private ChooseField m_choose;

  /**************************************** constructor ******************************************/
  public EditorChoose( Object[] values )
  {
    // create spin table cell editor for integer
    super();
    m_choose = new ChooseField( values );
    setControl( m_choose );
  }

  /******************************************* getValue ******************************************/
  @Override
  public Object getValue()
  {
    // get editor choose value
    return null;
  }

  /******************************************* setValue ******************************************/
  @Override
  public void setValue( Object value )
  {
    // set editor choose value
  }
}
