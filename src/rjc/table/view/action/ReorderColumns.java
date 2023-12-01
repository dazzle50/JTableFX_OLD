/**************************************************************************
 *  Copyright (C) 2023 by Richard Crook                                   *
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

package rjc.table.view.action;

import rjc.table.view.TableView;

/*************************************************************************************************/
/**************************** Supports table-view column re-ordering *****************************/
/*************************************************************************************************/

public class ReorderColumns extends Reorder
{
  /******************************************** start ********************************************/
  public static void start( TableView view, int x )
  {
    // if selected is "null" means all indexes selected - cannot reorder
    m_view = view;
    m_selected = view.getSelection().getSelectedColumns();
    if ( m_selected == null )
      return;

    // start reordering
    drag( x );
  }

}
