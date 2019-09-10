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

package rjc.table.cell;

import javafx.scene.input.KeyCode;

/*************************************************************************************************/
/******************************* Table cell editor for simple text *******************************/
/*************************************************************************************************/

public class EditorText extends CellEditorBase
{

  /**************************************** constructor ******************************************/
  public EditorText()
  {
    // create text table cell editor
    super();
    setControl( new XTextField() );

    // close the editor and move editor focus on table if up or down arrows pressed
    getControl().setOnKeyPressed( event ->
    {
      if ( event.getCode() == KeyCode.UP )
      {
        event.consume();
        // TODO move( MoveDirection.UP );
      }

      if ( event.getCode() == KeyCode.DOWN )
      {
        event.consume();
        // TODO move( MoveDirection.DOWN );
      }
    } );
  }

  /******************************************* getValue ******************************************/
  @Override
  public Object getValue()
  {
    // get editor text
    return ( (XTextField) getControl() ).getText();
  }

  /******************************************* setValue ******************************************/
  @Override
  public void setValue( Object value )
  {
    // set editor text
    String str = value == null ? "" : value.toString();
    ( (XTextField) getControl() ).setText( str );
    ( (XTextField) getControl() ).positionCaret( str.length() );
  }

  /****************************************** setAllowed *****************************************/
  public void setAllowed( String regex )
  {
    // regular expression that limits what can be entered into editor
    ( (XTextField) getControl() ).setAllowed( regex );
  }

  /***************************************** isValueValid ****************************************/
  @Override
  public boolean isValueValid( Object value )
  {
    // value is valid if null or allowed by editor
    return value == null || ( (XTextField) getControl() ).isAllowed( value.toString() );
  }

}
