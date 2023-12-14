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

import rjc.table.data.IDataReorderRows;
import rjc.table.view.TableView;

/*************************************************************************************************/
/****************************** Supports table-view row re-ordering ******************************/
/*************************************************************************************************/

public class ReorderRows
{
  private static TableView        m_view;     // table view for reordering
  private static HashSet<Integer> m_selected; // rows to be moved
  private static ReorderLine      m_line;     // red line to show new location
  private static int              m_y;        // latest y-coordinate used when table scrolled

  /******************************************** start ********************************************/
  public static void start( TableView view, int y )
  {
    // if selected is "null" means all indexes selected - cannot reorder
    m_view = view;
    m_selected = view.getSelection().getSelectedRows();
    if ( m_selected == null )
      return;
    m_line = new ReorderLine( view );

    // start reordering
    drag( y );
  }

  /******************************************** drag *********************************************/
  public static void drag( int y )
  {
    // position line at nearest row edge
    m_y = y;
    int row = m_view.getRowNearestStartIndex( y );
    m_line.setRow( row );
  }

  /***************************************** inProgress ******************************************/
  public static boolean inProgress()
  {
    // if no reorder in progress return false
    if ( m_view == null )
      return false;

    // return true as reorder in progress
    drag( m_y );
    return true;
  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // return without doing anything is reorder not started
    if ( m_view == null )
      return;

    // determine if data-model supports row reordering, otherwise reorder view only
    if ( m_view.getData() instanceof IDataReorderRows data )
      data.reorderRows( m_selected, m_line.getIndex() );
    else
      m_view.getRowsAxis().reorder( m_selected, m_line.getIndex() );

    m_view.remove( m_line );
    m_view = null;
    m_line = null;
  }
}