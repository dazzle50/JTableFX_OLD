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

package rjc.table.view.axis;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import rjc.table.signal.ObservableInteger.ReadOnlyInteger;

/*************************************************************************************************/
/**************** Base class for table X or Y axis with index to position mapping ****************/
/*************************************************************************************************/

public class AxisBase
{
  // count of body cells on axis
  private ReadOnlyInteger          m_count;

  // array mapping from position to index
  final private ArrayList<Integer> m_indexFromPosition = new ArrayList<>();

  // axis index starts at 0 for table body, index of -1 is for axis header
  final static public int          INVALID             = -2;
  final static public int          HEADER              = -1;
  final static public int          FIRSTCELL           = 0;
  final static public int          BEFORE              = Integer.MIN_VALUE + 1;
  final static public int          AFTER               = Integer.MAX_VALUE - 1;

  /**************************************** constructor ******************************************/
  public AxisBase( ReadOnlyInteger count )
  {
    // store private variable
    m_count = count;

    // if axis count changes
    count.addListener( x ->
    {
      // truncate position to index mapping if size greater than new count
      if ( m_indexFromPosition.size() > getCount() )
        m_indexFromPosition.subList( getCount(), m_indexFromPosition.size() ).clear();
    } );
  }

  /****************************************** getCount *******************************************/
  final public int getCount()
  {
    // return count of body cells on axis
    return m_count.get();
  }

  /******************************************** reset ********************************************/
  public void reset()
  {
    // clear all axis position to index re-ordering
    m_indexFromPosition.clear();
  }

  /************************************ getPositionFromIndex *************************************/
  final public int getPositionFromIndex( int index )
  {
    // return axis position from index using mapping (slower)
    if ( index >= FIRSTCELL && index < m_indexFromPosition.size() )
      return m_indexFromPosition.indexOf( index );

    // if not in mapping but within count, then return index as not re-ordered
    if ( index >= INVALID && index < getCount() )
      return index;

    // index is out of bounds so return invalid
    return INVALID;
  }

  /************************************ getIndexFromPosition *************************************/
  final public int getIndexFromPosition( int position )
  {
    // return axis index from position using mapping (faster)
    if ( position >= FIRSTCELL && position < m_indexFromPosition.size() )
      return m_indexFromPosition.get( position );

    // if not in mapping but within count, then return position as not re-ordered
    if ( position >= INVALID && position < getCount() )
      return position;

    // position is out of bounds so return invalid
    return INVALID;
  }

  /**************************************** movePositions ****************************************/
  public void movePositions( Set<Integer> positions, int newPosition )
  {
    // create reverse ordered set by using negative value
    TreeSet<Integer> list = new TreeSet<>();
    for ( int pos : positions )
      list.add( -pos );

    // make sure index from position mapping is big enough
    int max = Math.max( -list.first(), newPosition );
    while ( m_indexFromPosition.size() <= max )
      m_indexFromPosition.add( m_indexFromPosition.size() );

    // remove list from mapping
    int offset = 0;
    ArrayList<Integer> toBeMoved = new ArrayList<>();
    for ( int pos : list )
    {
      toBeMoved.add( 0, m_indexFromPosition.remove( -pos ) );
      if ( -pos < newPosition )
        offset--;
    }

    // re-add list into mapping at new position
    m_indexFromPosition.addAll( newPosition + offset, toBeMoved );
  }

  /**************************************** orderHashcode ****************************************/
  public int orderHashcode()
  {
    // make sure index from position mapping is complete to ensure consistent hash code
    int max = getCount();
    while ( m_indexFromPosition.size() <= max )
      m_indexFromPosition.add( m_indexFromPosition.size() );

    // returns the hash code for cell position mapping (to support confirming changes)
    return m_indexFromPosition.hashCode();
  }

}
