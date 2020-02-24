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

package rjc.table.view;

import javafx.geometry.Orientation;
import rjc.table.view.TableSelect.SelectedSet;
import rjc.table.view.axis.TableAxis;

/*************************************************************************************************/
/******************************* Supports column and row re-sizing *******************************/
/*************************************************************************************************/

public class Resize
{
  private Orientation m_orientation; // orientation for the resize
  private TableView   m_view;        // table view for resizing
  private TableAxis   m_axis;        // horizontal or vertical axis
  private SelectedSet m_indexes;     // columns or rows being resized
  private int         m_offset;      // resize coordinate offset
  private int         m_before;      // number of positions being resized before current position

  /**************************************** constructor ******************************************/
  public Resize()
  {
    // initialise variables
    m_indexes = new SelectedSet();
  }

  /*************************************** getOrientation ****************************************/
  public Orientation getOrientation()
  {
    // return reordering orientation, or null is not started
    return m_orientation;
  }

  /******************************************** start ********************************************/
  public void start( TableView view, Orientation orientation, int position, int coordinate )
  {
    // checks before start reordering
    if ( getOrientation() != null )
      throw new IllegalStateException( "Reordering already started" );
    if ( view == null )
      throw new NullPointerException( "TableView must not be null" );

    // horizontal for columns, vertical for rows
    SelectedSet positions;
    if ( orientation == Orientation.HORIZONTAL )
    {
      m_axis = view.getColumns();
      m_offset = view.getXStartFromColumnPos( position + 1 );
      positions = view.getSelectedColumns();
    }
    else
    {
      m_axis = view.getRows();
      m_offset = view.getYStartFromRowPos( position + 1 );
      positions = view.getSelectedRows();
    }

    // prepare indexes to be re-sized 
    m_indexes.all = positions.all;
    m_indexes.set.clear();
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
      {
        // resize all positions
        m_before = position;
        m_axis.clearSizeExceptions();
      }
      else
        // resize just current position
        m_indexes.set.add( index );
    }

    // calculate offset
    if ( positions.all )
    {
      if ( orientation == Orientation.HORIZONTAL )
        m_offset = view.getXStartFromColumnPos( 0 );
      else
        m_offset = view.getYStartFromRowPos( 0 );
    }
    else
      m_offset -= m_axis.getCellPixels( index );
    m_before++;

    // start reordering
    m_orientation = orientation;
    m_view = view;
    resize( coordinate );
  }

  /******************************************* resize ********************************************/
  public void resize( int coordinate )
  {
    // resize columns or rows
    if ( getOrientation() == null )
      return;
    int pixels = ( coordinate - m_offset ) / m_before;

    // resize
    if ( m_indexes.all )
      m_axis.setDefaultSize( (int) ( pixels / m_view.getZoom() ) );
    else
      for ( var index : m_indexes.set )
        m_axis.setCellPixels( index, pixels );

    // redraw table and update scroll bars
    m_view.redraw();
    m_view.layoutDisplay();
  }

  /********************************************* end *********************************************/
  public void end()
  {
    // end any resizing
    if ( getOrientation() != null )
    {
      m_axis = null;
      m_view = null;
      m_orientation = null;
    }
  }

}
