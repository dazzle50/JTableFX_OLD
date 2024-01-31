/**************************************************************************
 *  Copyright (C) 2024 by Richard Crook                                   *
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

package rjc.table.control;

import javafx.scene.input.ScrollEvent;

/*************************************************************************************************/
/***************************** Interface for control wrapping fields *****************************/
/*************************************************************************************************/

public interface IWrapField
{
  /***************************************** mouseScroll *****************************************/
  public default void mouseScroll( ScrollEvent event )
  {
    // default implementation for increment or decrement value depending on mouse wheel scroll event
    event.consume();
    if ( event.getDeltaY() > 0 )
      stepValue( 1 );
    else
      stepValue( -1 );
  }

  /****************************************** stepValue ******************************************/
  public default void stepValue( double delta )
  {
    // default behaviour is do nothing - overload as needed
  }

}