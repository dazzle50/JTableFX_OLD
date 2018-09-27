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

package rjc.table.view;

import javafx.beans.property.SimpleIntegerProperty;

/*************************************************************************************************/
/******************************** Table cell/row/column selection ********************************/
/*************************************************************************************************/

public class TableSelection extends TableSizing
{
  public final SimpleIntegerProperty mouseColumnPos  = new SimpleIntegerProperty( INVALID );
  public final SimpleIntegerProperty mouseRowPos     = new SimpleIntegerProperty( INVALID );

  public final SimpleIntegerProperty focusColumnPos  = new SimpleIntegerProperty();
  public final SimpleIntegerProperty focusRowPos     = new SimpleIntegerProperty();

  public final SimpleIntegerProperty selectColumnPos = new SimpleIntegerProperty();
  public final SimpleIntegerProperty selectRowPos    = new SimpleIntegerProperty();

}
