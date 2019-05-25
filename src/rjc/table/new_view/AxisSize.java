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

package rjc.table.new_view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;

/*************************************************************************************************/
/*********************** Controls axis cell pixel size (including header) ************************/
/*************************************************************************************************/

public class AxisSize extends AxisBase
{
  // variables defining default & minimum cell pixel size (width or height)
  private int                          m_defaultSize;
  private int                          m_minimumSize;

  // exceptions to default size, -ve means hidden, key = cell-index+1 as key=0 is for header (-1)
  final private Map<Integer, Integer>  m_sizeExceptions         = new HashMap<Integer, Integer>();

  // cached cell position start pixel coordinate
  final private ArrayList<Integer>     m_cellPositionStartCache = new ArrayList<Integer>();

  // observable integer for axis total body size in pixels (excludes header)
  final private ReadOnlyIntegerWrapper m_bodySize               = new ReadOnlyIntegerWrapper( INVALID );

  /***************************************** constructor *****************************************/
  public AxisSize( ReadOnlyIntegerProperty countProperty )
  {
    // call super
    super( countProperty );

    // if axis count changes
    countProperty.addListener( ( observable, oldCount, newCount ) ->
    {
      // re-calculate size of table body cells
      int count = newCount.intValue();
      int exceptionsCount = 0;
      int bodySize = 0;
      for ( int key : m_sizeExceptions.keySet() )
        if ( key != 0 ) // ignore any exception for header size
        {
          if ( key < count )
          {
            exceptionsCount++;
            int size = m_sizeExceptions.get( key );
            if ( size > 0 )
              bodySize += size;
          }
          else
            m_sizeExceptions.remove( key ); // delete any exceptions beyond cell count
        }
      m_bodySize.set( bodySize + ( count - exceptionsCount ) * m_defaultSize );

      // truncate cell position start if size greater than new count
      if ( m_cellPositionStartCache.size() > count )
        m_cellPositionStartCache.subList( count, m_cellPositionStartCache.size() ).clear();
    } );
  }

  /******************************************** reset ********************************************/
  @Override
  public void reset()
  {
    // call super + clear all size exceptions, cache and reset sizes
    super.reset();
    m_defaultSize = 100;
    m_minimumSize = 20;
    m_sizeExceptions.clear();
    m_cellPositionStartCache.clear();
    m_bodySize.set( INVALID );
  }

  /***************************************** getBodySize *****************************************/
  public int getBodySize()
  {
    // return axis total  body size in pixels (excludes header) 
    if ( m_bodySize.get() == INVALID )
    {
      // cached size is invalid, so re-calculate size of table body cells
      int defaultCount = getCount();
      int bodySize = 0;

      for ( int key : m_sizeExceptions.keySet() )
        if ( key != 0 ) // ignore any exception for header size
        {
          defaultCount--;
          int size = m_sizeExceptions.get( key );
          if ( size > 0 )
            bodySize += size;
        }

      m_bodySize.set( bodySize + defaultCount * m_defaultSize );
    }

    return m_bodySize.get();
  }

  /************************************* getBodySizeProperty *************************************/
  final public ReadOnlyIntegerProperty getBodySizeProperty()
  {
    // return read-only property for  body size in pixels (excludes header)
    return m_bodySize.getReadOnlyProperty();
  }

  /*************************************** getDefaultSize ****************************************/
  public int getDefaultSize()
  {
    // return default cell pixel size
    return m_defaultSize;
  }

  /*************************************** getMinimumSize ****************************************/
  public int getMinimumSize()
  {
    // return minimum cell pixel size
    return m_minimumSize;
  }

  /*************************************** setDefaultSize ****************************************/
  public void setDefaultSize( int defaultSize )
  {
    // if different, set default cell pixel size and invalidate body size
    if ( m_defaultSize != defaultSize )
    {
      // check requested new default size is at least one
      if ( defaultSize < 1 )
        throw new IllegalArgumentException( "Default size must be at least one " + defaultSize );

      // if size smaller than minimum, increase minimum to size
      if ( defaultSize < m_minimumSize )
        setMinimumSize( defaultSize );

      m_defaultSize = defaultSize;
      m_bodySize.set( INVALID );
    }
  }

  /*************************************** setMinimumSize ****************************************/
  public void setMinimumSize( int minSize )
  {
    // if different, set minimum cell pixel size and invalidate body size
    if ( m_minimumSize != minSize )
    {
      // check requested new minimum size is at least zero
      if ( minSize < 0 )
        throw new IllegalArgumentException( "Minimum size must be at least zero " + minSize );

      // if default smaller than minimum, increase default to size
      if ( m_defaultSize < minSize )
        setDefaultSize( minSize );

      // if minimum size increasing, check exceptions
      if ( minSize > m_minimumSize )
      {
        var it = m_sizeExceptions.entrySet().iterator();
        while ( it.hasNext() )
        {
          var entry = it.next();
          if ( entry.getValue() > 0 && entry.getValue() < minSize )
            entry.setValue( minSize );
          if ( entry.getValue() < 0 && entry.getValue() > -minSize )
            entry.setValue( -minSize );
        }
      }

      m_minimumSize = minSize;
      m_bodySize.set( INVALID );
    }
  }

  /***************************************** getCellSize *****************************************/
  public int getCellSize( int cellIndex )
  {
    // return cell size, key = index+1 as key=0 is for header
    int size = m_sizeExceptions.getOrDefault( cellIndex + 1, m_defaultSize );
    if ( size < 0 )
      return 0; // -ve means row hidden, so return zero

    return size;
  }

  /***************************************** setCellSize *****************************************/
  public void setCellSize( int cellIndex, int newSize )
  {
    // check cell index is valid
    if ( cellIndex < HEADER || cellIndex >= getCount() )
      throw new IndexOutOfBoundsException( "cell index=" + cellIndex + " but count=" + getCount() );

    // make sure size is not below minimum
    if ( newSize < m_minimumSize )
      newSize = m_minimumSize;

    // create a size exception (even if same as default)
    int oldSize = getCellSize( cellIndex );
    m_sizeExceptions.put( cellIndex + 1, newSize );

    // if new size is different, update body size and truncate cell position start cache if needed
    if ( newSize != oldSize )
    {
      if ( m_bodySize.get() != INVALID )
        m_bodySize.set( m_bodySize.get() - oldSize + newSize );

      // truncate cell position start cache if size greater than cell position
      int cellPos = getPositionFromIndex( cellIndex );
      if ( m_cellPositionStartCache.size() > cellPos )
        m_cellPositionStartCache.subList( cellPos, m_cellPositionStartCache.size() ).clear();
    }
  }

  /**************************************** clearCellSize ****************************************/
  public void clearCellSize( int cellIndex )
  {
    // check cell index is valid
    if ( cellIndex < HEADER || cellIndex >= getCount() )
      throw new IndexOutOfBoundsException( "cell index=" + cellIndex + " but count=" + getCount() );

    // remove cell index size exception if exists, key = index+1 as key=0 is for header
    m_sizeExceptions.remove( cellIndex + 1 );
  }

  /************************************ getStartFromPosition *************************************/
  public int getStartFromPosition( int cellPosition, int scroll )
  {
    // check position is valid
    if ( cellPosition < HEADER || cellPosition >= getCount() )
      throw new IndexOutOfBoundsException( "position=" + cellPosition + " but count=" + getCount() );

    // if header, return zero
    if ( cellPosition == HEADER )
      return 0;

    // if cell position is beyond cache, extend cache
    if ( cellPosition >= m_cellPositionStartCache.size() )
    {
      // position zero starts after header
      if ( m_cellPositionStartCache.isEmpty() )
        m_cellPositionStartCache.add( getCellSize( HEADER ) );

      int position = m_cellPositionStartCache.size() - 1;
      int start = m_cellPositionStartCache.get( position );
      while ( cellPosition > position )
      {
        start += getCellSize( getIndexFromPosition( position++ ) );
        m_cellPositionStartCache.add( start );
      }
    }

    // return start pixel coordinate for cell position taking scroll into account
    return m_cellPositionStartCache.get( cellPosition ) - scroll;
  }

  /********************************** getPositionFromCoordinate **********************************/
  public int getPositionFromCoordinate( int coordinate, int scroll )
  {
    // check if before table
    if ( coordinate < 0 )
      return BEFORE;

    // check if header
    if ( coordinate < getCellSize( HEADER ) )
      return HEADER;

    // check if after table
    coordinate += scroll;
    if ( coordinate >= getBodySize() )
      return AFTER;

    // check within start cache
    int position = m_cellPositionStartCache.size() - 1;
    int start = m_cellPositionStartCache.get( position );
    if ( coordinate >= start )
    {
      while ( coordinate > start )
      {
        start += getCellSize( getIndexFromPosition( position++ ) );
        m_cellPositionStartCache.add( start );
      }
      return position;
    }

    // find position by binary search of cache
    int startPos = 0;
    int endPos = m_cellPositionStartCache.size();
    while ( startPos != endPos )
    {
      int rowPos = ( endPos + startPos ) / 2;
      if ( m_cellPositionStartCache.get( rowPos ) <= coordinate )
        startPos = rowPos + 1;
      else
        endPos = rowPos;
    }
    return startPos - 1;
  }

  /**************************************** getTableSize *****************************************/
  public int getTableSize( int scroll )
  {
    // return table size for given scroll
    return getCellSize( HEADER ) + Math.max( 0, getBodySize() - scroll );
  }

  /************************************** isPositionHidden ***************************************/
  public boolean isPositionHidden( int position )
  {
    // return if position is hidden
    return getCellSize( getIndexFromPosition( position ) ) <= 0;
  }

  /**************************************** hidePosition *****************************************/
  public void hidePosition( int position )
  {
    // if position not already hidden, set size exception and update body size
    int index = getIndexFromPosition( position );
    int oldSize = m_sizeExceptions.getOrDefault( index + 1, m_defaultSize );
    if ( oldSize > 0 )
    {
      m_sizeExceptions.put( index, -oldSize );
      if ( m_bodySize.get() != INVALID )
        m_bodySize.set( m_bodySize.get() - oldSize );

      // truncate cell position start cache if size greater than position
      if ( m_cellPositionStartCache.size() > position )
        m_cellPositionStartCache.subList( position, m_cellPositionStartCache.size() ).clear();
    }
  }

}
