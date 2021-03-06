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

package rjc.table.view.actions;

import javafx.geometry.Orientation;
import rjc.table.undo.CommandResize;
import rjc.table.view.TableScrollBar.Animation;
import rjc.table.view.TableSelection.SelectedSet;
import rjc.table.view.TableView;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************************* Supports column and row re-sizing *******************************/
/*************************************************************************************************/

public class Resize
{
  private static TableView     m_view;    // table view for resizing
  private static TableAxis     m_axis;    // horizontal or vertical axis
  private static SelectedSet   m_indexes; // columns or rows being resized
  private static int           m_offset;  // resize coordinate offset
  private static int           m_before;  // number of positions being resized before current position
  private static double        m_scroll;  // scroll bar value
  private static CommandResize m_command; // command for undo-stack

  /******************************************** start ********************************************/
  public static void start( TableView view, Orientation orientation, int coordinate )
  {
    // determine set of columns or rows positions to be resized
    SelectedSet positions;
    int position;
    if ( orientation == Orientation.HORIZONTAL )
    {
      position = view.getColumnPositionAtX( coordinate );
      int xs = view.getXStartFromColumnPos( position );
      int xe = view.getXStartFromColumnPos( position + 1 );

      if ( coordinate - xs < xe - coordinate )
      {
        m_offset = xs;
        position--;
      }
      else
        m_offset = xe;

      m_scroll = view.getHorizontalScrollBar().getValue();
      m_axis = view.getColumnsAxis();
      positions = view.getSelection().getSelectedColumns();
    }
    else
    {
      position = view.getRowPositionAtY( coordinate );
      int ys = view.getYStartFromRowPos( position );
      int ye = view.getYStartFromRowPos( position + 1 );

      if ( coordinate - ys < ye - coordinate )
      {
        m_offset = ys;
        position--;
      }
      else
        m_offset = ye;

      m_scroll = view.getVerticalScrollBar().getValue();
      m_axis = view.getRowsAxis();
      positions = view.getSelection().getSelectedRows();
    }

    // determine set of columns or rows indexes to be resized
    m_indexes = new SelectedSet();
    m_indexes.all = positions.all;
    m_before = 0;
    int index = m_axis.getIndexFromPosition( position );
    if ( positions.set != null && positions.set.contains( position ) )
    {
      // resize all selected positions
      for ( var pos : positions.set )
      {
        int i = m_axis.getIndexFromPosition( pos );
        m_indexes.set.add( i );
        if ( pos < position )
        {
          m_offset -= m_axis.getCellPixels( i );
          m_before++;
        }
      }
    }
    else
    {
      if ( positions.all )
        // resize all positions
        m_before = position;
      else
        // resize just current (not-selected) position
        m_indexes.set.add( index );
    }

    // prepare resize command
    m_command = new CommandResize( view, orientation, m_indexes );

    // calculate offset
    if ( positions.all )
    {
      m_axis.clearSizeExceptions();
      if ( orientation == Orientation.HORIZONTAL )
        m_offset = view.getXStartFromColumnPos( TableAxis.FIRSTCELL );
      else
        m_offset = view.getYStartFromRowPos( TableAxis.FIRSTCELL );
    }
    else
      m_offset -= m_axis.getCellPixels( index );
    m_before++;

    // start reordering
    m_view = view;
    drag( coordinate );
  }

  /******************************************** drag *********************************************/
  public static void drag( int coordinate )
  {
    // resize columns or rows
    int pixels = ( coordinate - m_offset ) / m_before;
    int size = (int) ( pixels / m_view.getZoom().get() );

    // resize
    m_command.setNewSize( size );
    if ( m_indexes.all )
      m_axis.setDefaultSize( size );
    else
      for ( var index : m_indexes.set )
        m_axis.setCellSize( index, size );

    // redraw table and update scroll bars
    m_view.layoutDisplay();
    m_view.redraw();
  }

  /********************************************* end *********************************************/
  public static void end()
  {
    // end resizing, push resize command onto undo-stack
    m_view.getUndoStack().push( m_command );
    m_view = null;
  }

  /***************************************** inProgress ******************************************/
  public static boolean inProgress()
  {
    // if no resize in progress return false
    if ( m_view == null )
      return false;

    // check if table is scrolling horizontally and adjust offset accordingly
    var scrollbar = m_view.getHorizontalScrollBar();
    if ( scrollbar.getAnimation() == Animation.TO_START )
      updateOffset( scrollbar.getValue(), m_view.getHeaderWidth() );
    else if ( scrollbar.getAnimation() == Animation.TO_END )
      updateOffset( scrollbar.getValue(), m_view.getCanvas().getWidth() );

    // check if table is scrolling vertically and adjust offset accordingly
    scrollbar = m_view.getVerticalScrollBar();
    if ( scrollbar.getAnimation() == Animation.TO_START )
      updateOffset( scrollbar.getValue(), m_view.getHeaderHeight() );
    else if ( scrollbar.getAnimation() == Animation.TO_END )
      updateOffset( scrollbar.getValue(), m_view.getCanvas().getHeight() );

    // return true as resize in progress
    return true;
  }

  /**************************************** updateOffset *****************************************/
  private static void updateOffset( double value, double coordinate )
  {
    // update offset and resize columns or rows
    m_offset += m_scroll - value;
    m_scroll = value;
    drag( (int) coordinate );
  }
}
