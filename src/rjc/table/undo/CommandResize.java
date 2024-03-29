/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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

import java.util.HashMap;

import rjc.table.view.TableSelection.SelectedSet;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************** UndoCommand for resizing columns widths or rows height *********************/
/*************************************************************************************************/

public class CommandResize implements IUndoCommand
{
  private TableView                 m_view;                 // table view
  private TableAxis                 m_axis;                 // columns or rows being resized
  private SelectedSet               m_indexes;              // indexes being resized
  private String                    m_text;                 // text describing command

  private HashMap<Integer, Integer> m_oldExceptions;        // old size exceptions before resize
  private int                       m_oldDefault;           // old default before resize
  private int                       m_newSize;              // new size

  final static private int          NO_EXCEPTION = -999999; // no size exception

  /**************************************** constructor ******************************************/
  public CommandResize( TableView view, TableAxis axis, SelectedSet indexes )
  {
    // prepare resize command
    m_view = view;
    m_indexes = indexes;
    m_axis = axis;

    // get old default size and exceptions before resizing starts
    m_oldDefault = axis.getDefaultSize();
    m_oldExceptions = new HashMap<>();
    if ( indexes.all )
      axis.getSizeExceptions().forEach( ( index, size ) -> m_oldExceptions.put( index, size ) );
    else
      indexes.set.forEach( ( index ) ->
      {
        int size = axis.getSizeExceptions().getOrDefault( index, NO_EXCEPTION );
        m_oldExceptions.put( index, size );
      } );
  }

  /***************************************** setNewSize ******************************************/
  public void setNewSize( int size )
  {
    // set command new size
    m_newSize = size;
  }

  /******************************************* redo **********************************************/
  @Override
  public void redo()
  {
    // action command
    if ( m_indexes.all )
    {
      m_axis.setDefaultSize( m_newSize );
      m_axis.clearSizeExceptions();
    }
    else
      for ( var index : m_indexes.set )
        m_axis.setCellSize( index, m_newSize );

    // update layout in case scroll-bar need changed and redraw table in this view only
    m_view.layoutDisplay();
    m_view.redraw();
  }

  /******************************************* undo **********************************************/
  @Override
  public void undo()
  {
    // revert command - if all revert default size
    if ( m_indexes.all )
      m_axis.setDefaultSize( m_oldDefault );

    // revert exceptions including hidden
    m_oldExceptions.forEach( ( index, size ) ->
    {
      if ( size == NO_EXCEPTION )
        m_axis.clearCellSize( index );
      else
      {
        if ( size < 0 )
        {
          m_axis.setCellSize( index, -size );
          m_axis.hideIndex( index );
        }
        else
          m_axis.setCellSize( index, size );
      }
    } );

    // update layout in case scroll-bar need changed and redraw table in this view only
    m_view.layoutDisplay();
    m_view.redraw();
  }

  /******************************************* text **********************************************/
  @Override
  public String text()
  {
    // command description
    if ( m_text == null )
      m_text = "Resizing " + ( m_indexes.all ? "all" : m_indexes.set.size() )
          + ( m_axis == m_view.getColumnsAxis() ? " column" : " row" )
          + ( m_indexes.all || m_indexes.set.size() > 1 ? "s" : "" );

    return m_text;
  }
}
