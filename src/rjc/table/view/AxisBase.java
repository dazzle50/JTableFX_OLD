/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

import java.util.ArrayList;

import javafx.beans.property.ReadOnlyIntegerProperty;

/*************************************************************************************************/
/**************** Base class for table X or Y axis with index to position mapping ****************/
/*************************************************************************************************/

public class AxisBase
{
  // property from TableData defining axis count
  private ReadOnlyIntegerProperty  m_countProperty;

  // array mapping from position to index
  final private ArrayList<Integer> m_indexFromPosition = new ArrayList<Integer>();

  // axis index starts at 0 for table body, index of -1 is for axis header
  final static public int          INVALID             = -2;
  final static public int          HEADER              = -1;
  final static public int          FIRSTCELL           = 0;
  final static public int          BEFORE              = Integer.MIN_VALUE + 1;
  final static public int          AFTER               = Integer.MAX_VALUE - 1;

  /**************************************** constructor ******************************************/
  public AxisBase( ReadOnlyIntegerProperty countProperty )
  {
    // store private variable
    m_countProperty = countProperty;

    // if axis count changes
    countProperty.addListener( ( observable, oldCount, newCount ) ->
    {
      // truncate position to index mapping if size greater than new count
      if ( m_indexFromPosition.size() > getCount() )
        m_indexFromPosition.subList( getCount(), m_indexFromPosition.size() ).clear();
    } );
  }

  /****************************************** getCount *******************************************/
  final public int getCount()
  {
    // return axis count as defined in TableData column or row count property
    return m_countProperty.get();
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
    if ( index >= HEADER && index < getCount() )
      return index;

    // index is out of bounds, so throw exception
    throw new IndexOutOfBoundsException( "index=" + index + " but count=" + getCount() );
  }

  /************************************ getIndexFromPosition *************************************/
  final public int getIndexFromPosition( int position )
  {
    // return axis index from position using mapping (faster)
    if ( position >= FIRSTCELL && position < m_indexFromPosition.size() )
      return m_indexFromPosition.get( position );

    // if not in mapping but within count, then return position as not re-ordered
    if ( position >= HEADER && position < getCount() )
      return position;

    // position is out of bounds, so throw exception
    throw new IndexOutOfBoundsException( "position=" + position + " but count=" + getCount() );
  }

  /**************************************** movePosition *****************************************/
  final public void movePosition( int oldPosition, int newPosition )
  {
    // check positions are within axis count
    int count = getCount();
    if ( oldPosition < FIRSTCELL || oldPosition >= count )
      throw new IndexOutOfBoundsException( "old position=" + oldPosition + " but count=" + count );
    if ( newPosition < FIRSTCELL || newPosition >= count )
      throw new IndexOutOfBoundsException( "new position=" + newPosition + " but count=" + count );

    // if old and new position are same, nothing needs to be done
    if ( oldPosition == newPosition )
      return;

    // make sure index from position mapping is big enough
    int max = Math.max( oldPosition, newPosition );
    while ( m_indexFromPosition.size() <= max )
      m_indexFromPosition.add( m_indexFromPosition.size() );

    // move index from old position to new position
    int index = m_indexFromPosition.remove( oldPosition );
    m_indexFromPosition.add( newPosition, index );
  }

}
