/**************************************************************************
 *  Copyright (C) 2021 by Richard Crook                                   *
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

package rjc.table.undo;

import java.util.Set;
import java.util.TreeSet;

import javafx.geometry.Orientation;
import rjc.table.view.TableView;
import rjc.table.view.actions.Reorder;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************** UndoCommand for reordering position of columns or rows *********************/
/*************************************************************************************************/

public class CommandReorder implements IUndoCommand
{
  private TableView    m_view;        // table view
  private Orientation  m_orientation; // columns or rows being reordered
  private Set<Integer> m_positions;   // positions being moved
  private int          m_newPos;      // new position
  private String       m_text;        // text describing command

  /**************************************** constructor ******************************************/
  public CommandReorder( TableView view, Orientation orientation, Set<Integer> positions, int newPos )
  {
    // prepare reorder command
    m_view = view;
    m_orientation = orientation;
    m_positions = positions;
    m_newPos = newPos;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    TableAxis axis = m_orientation == Orientation.HORIZONTAL ? m_view.getColumnsAxis() : m_view.getRowsAxis();
    axis.movePositions( m_positions, m_newPos );

    // redraw table in this view only
    m_view.redraw();
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command
    TableAxis axis = m_orientation == Orientation.HORIZONTAL ? m_view.getColumnsAxis() : m_view.getRowsAxis();
    int newOffset = Reorder.countBefore( m_positions, m_newPos );
    int oldOffset = m_positions.size() - newOffset;

    // create ordered list to process moves in predictable order
    TreeSet<Integer> list = new TreeSet<>();
    for ( int pos : m_positions )
      list.add( pos );

    // move columns or rows back to their prior positions
    for ( int oldPos : list )
      if ( m_newPos > oldPos )
      {
        axis.movePosition( m_newPos - newOffset, oldPos );
        newOffset--;
      }
      else
      {
        axis.movePosition( m_newPos, oldPos + oldOffset );
        oldOffset--;
      }

    // redraw table in this view only
    m_view.redraw();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    if ( m_text == null )
      m_text = "Moving " + m_positions.size() + ( m_orientation == Orientation.HORIZONTAL ? " column" : " row" )
          + ( m_positions.size() > 1 ? "s" : "" );

    return m_text;
  }

}
