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

import java.util.HashSet;

import javafx.geometry.Orientation;
import rjc.table.Utils;
import rjc.table.data.IDataReorderColumns;
import rjc.table.data.IDataReorderRows;
import rjc.table.view.TableView;

/*************************************************************************************************/
/****************************** Supports column and row re-ordering ******************************/
/*************************************************************************************************/

public class Reorder
{
  // visible to subclasses in package
  static TableView        m_view;        // table view for reordering
  static HashSet<Integer> m_selected;    // columns or rows to be moved
  static Orientation      m_orientation; // orientation for the reordering
  static int              m_coordinate;  // latest coordinate used when table scrolled

  /******************************************** start ********************************************/
  public static void start( TableView view, Orientation orientation, int coordinate )
  {
    // determine selected columns or rows to be moved
    if ( orientation == Orientation.HORIZONTAL )
      m_selected = view.getSelection().getSelectedColumns();
    else
      m_selected = view.getSelection().getSelectedRows();

    // if selected is "null" means all indexes selected - cannot reorder
    if ( m_selected == null )
      return;

    // start reordering
    m_view = view;
    m_orientation = orientation;
    drag( coordinate );
  }

  /******************************************** drag *********************************************/
  public static void drag( int coordinate )
  {
    // reorder columns or rows
    m_coordinate = coordinate;

    // MOVE INDICATOR LINE
  }

  /***************************************** inProgress ******************************************/
  public static boolean inProgress()
  {
    // if no reorder in progress return false
    if ( m_view == null )
      return false;

    // return true as reorder in progress
    drag( m_coordinate );
    return true;
  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // return without doing anything is reorder not started
    if ( m_view == null )
      return;

    // CHECK IF DATA MODEL SUPPORTS REORDER, OR ONLY VIEW
    if ( m_orientation == Orientation.HORIZONTAL )
    {
      if ( m_view.getData() instanceof IDataReorderColumns )
        Utils.trace( "HORIZONTAL - DATA MODEL SUPPORTS REORDER" );
      else
        Utils.trace( "HORIZONTAL - VIEW REORDER" );
    }
    else
    {
      if ( m_view.getData() instanceof IDataReorderRows )
        Utils.trace( "VERTICAL - DATA MODEL SUPPORTS REORDER" );
      else
        Utils.trace( "VERTICAL - VIEW REORDER" );
    }

    m_view = null;
  }
}
