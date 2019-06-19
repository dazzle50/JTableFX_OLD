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
  private int                          m_headerSize;

  // exceptions to default size, -ve means hidden
  final private Map<Integer, Integer>  m_sizeExceptions         = new HashMap<Integer, Integer>();

  // cached cell position start pixel coordinate
  final private ArrayList<Integer>     m_cellPositionStartCache = new ArrayList<Integer>();

  // observable integer for axis total body size in pixels (excludes header)
  final private ReadOnlyIntegerWrapper m_bodySizeCache          = new ReadOnlyIntegerWrapper( INVALID );

  /***************************************** constructor *****************************************/
  public AxisSize( ReadOnlyIntegerProperty countProperty )
  {
    // call super
    super( countProperty );

    // if axis count changes
    countProperty.addListener( ( observable, oldCount, newCount ) ->
    {
      // set cached body size to invalid and remove any exceptions beyond count
      m_bodySizeCache.set( INVALID );
      int count = newCount.intValue();
      for ( int key : m_sizeExceptions.keySet() )
        if ( key >= count )
          m_sizeExceptions.remove( key );

      // truncate cell position start if size greater than new count
      count++;
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
    m_headerSize = 40;
    m_sizeExceptions.clear();
    m_cellPositionStartCache.clear();
    m_bodySizeCache.set( INVALID );
  }

  /***************************************** getBodySize *****************************************/
  public int getBodySize()
  {
    // return axis total body size in pixels (excludes header) 
    if ( m_bodySizeCache.get() == INVALID )
    {
      // cached size is invalid, so re-calculate size of table body cells
      int defaultCount = getCount();
      int bodySize = 0;

      for ( int key : m_sizeExceptions.keySet() )
      {
        defaultCount--;
        int size = m_sizeExceptions.get( key );
        if ( size > 0 )
          bodySize += size;
      }

      m_bodySizeCache.set( bodySize + defaultCount * m_defaultSize );
    }

    return m_bodySizeCache.get();
  }

  /************************************* getBodySizeProperty *************************************/
  final public ReadOnlyIntegerProperty getBodySizeProperty()
  {
    // return read-only property for body size in pixels (excludes header)
    return m_bodySizeCache.getReadOnlyProperty();
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
      m_bodySizeCache.set( INVALID );
      m_cellPositionStartCache.clear();
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

        m_bodySizeCache.set( INVALID );
        m_cellPositionStartCache.clear();
      }

      m_minimumSize = minSize;
    }
  }

  /**************************************** getHeaderSize ****************************************/
  public int getHeaderSize()
  {
    // return header cell size
    return m_headerSize;
  }

  /**************************************** setHeaderSize ****************************************/
  public void setHeaderSize( int newSize )
  {
    // check new size is valid
    if ( newSize < 0 || newSize >= 65536 )
      throw new IllegalArgumentException( "Header size must be at least zero " + newSize );

    // if new size is different, clear cell position start cache
    if ( m_headerSize != newSize )
      m_cellPositionStartCache.clear();

    m_headerSize = newSize;
  }

  /***************************************** getCellSize *****************************************/
  public int getCellSize( int cellIndex )
  {
    // return header size if that was requested
    if ( cellIndex == HEADER )
      return m_headerSize;

    // return cell size from exception (-ve means hidden) or default
    int size = m_sizeExceptions.getOrDefault( cellIndex, m_defaultSize );
    if ( size < 0 )
      return 0; // -ve means row hidden, so return zero

    return size;
  }

  /***************************************** setCellSize *****************************************/
  public void setCellSize( int cellIndex, int newSize )
  {
    // check cell index is valid
    if ( cellIndex < FIRSTCELL || cellIndex >= getCount() )
      throw new IndexOutOfBoundsException( "cell index=" + cellIndex + " but count=" + getCount() );

    // make sure size is not below minimum
    if ( newSize < m_minimumSize )
      newSize = m_minimumSize;

    // create a size exception (even if same as default)
    int oldSize = getCellSize( cellIndex );
    m_sizeExceptions.put( cellIndex, newSize );

    // if new size is different, update body size and truncate cell position start cache if needed
    if ( newSize != oldSize )
    {
      if ( m_bodySizeCache.get() != INVALID )
        m_bodySizeCache.set( m_bodySizeCache.get() - oldSize + newSize );

      // truncate cell position start cache if size greater than cell position
      int cellPos = getPositionFromIndex( cellIndex ) + 1;
      if ( m_cellPositionStartCache.size() > cellPos )
        m_cellPositionStartCache.subList( cellPos, m_cellPositionStartCache.size() ).clear();
    }
  }

  /**************************************** clearCellSize ****************************************/
  public void clearCellSize( int cellIndex )
  {
    // check cell index is valid
    if ( cellIndex < FIRSTCELL || cellIndex >= getCount() )
      throw new IndexOutOfBoundsException( "cell index=" + cellIndex + " but count=" + getCount() );

    // remove cell index size exception if exists
    m_sizeExceptions.remove( cellIndex );
  }

  /************************************ getStartFromPosition *************************************/
  public int getStartFromPosition( int cellPosition, int scroll )
  {
    // check position is valid
    if ( cellPosition < HEADER || cellPosition > getCount() )
      throw new IndexOutOfBoundsException( "position=" + cellPosition + " but count=" + getCount() );

    // if header, return zero
    if ( cellPosition == HEADER )
      return 0;

    // if cell position is beyond cache, extend cache
    if ( cellPosition >= m_cellPositionStartCache.size() )
    {
      // position zero starts after header
      if ( m_cellPositionStartCache.isEmpty() )
        m_cellPositionStartCache.add( m_headerSize );

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
    if ( coordinate < m_headerSize )
      return HEADER;

    // check if after table
    coordinate += scroll;
    if ( coordinate >= getBodySize() + m_headerSize )
      return AFTER;

    // check within start cache
    int position = m_cellPositionStartCache.size() - 1;
    int start = position < 0 ? 0 : m_cellPositionStartCache.get( position );
    if ( coordinate > start )
    {
      while ( coordinate > start )
      {
        start += getCellSize( getIndexFromPosition( position++ ) );
        m_cellPositionStartCache.add( start );
      }
      return coordinate == start ? position : position - 1;
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

  /************************************** isPositionHidden ***************************************/
  public boolean isPositionHidden( int position )
  {
    // return if position is hidden (or zero size)
    return getCellSize( getIndexFromPosition( position ) ) <= 0;
  }

  /**************************************** hidePosition *****************************************/
  public void hidePosition( int position )
  {
    // if position not already hidden, set size exception and update body size
    int index = getIndexFromPosition( position );
    int oldSize = m_sizeExceptions.getOrDefault( index, m_defaultSize );
    if ( oldSize > 0 )
    {
      m_sizeExceptions.put( index, -oldSize );
      if ( m_bodySizeCache.get() != INVALID )
        m_bodySizeCache.set( m_bodySizeCache.get() - oldSize );

      // truncate cell position start cache if size greater than position
      if ( m_cellPositionStartCache.size() > position )
        m_cellPositionStartCache.subList( position, m_cellPositionStartCache.size() ).clear();
    }
  }

  /*************************************** unhidePosition ****************************************/
  public void unhidePosition( int position )
  {
    // if position hidden, set size exception and update body size
    int index = getIndexFromPosition( position );
    int oldSize = m_sizeExceptions.getOrDefault( index, m_defaultSize );
    if ( oldSize < 0 )
    {
      if ( oldSize == -m_defaultSize )
        m_sizeExceptions.remove( index );
      else
        m_sizeExceptions.put( index, -oldSize );

      if ( m_bodySizeCache.get() != INVALID )
        m_bodySizeCache.set( m_bodySizeCache.get() - oldSize );

      // truncate cell position start cache if size greater than position
      if ( m_cellPositionStartCache.size() > position )
        m_cellPositionStartCache.subList( position, m_cellPositionStartCache.size() ).clear();
    }
  }

  /****************************************** getFirst *******************************************/
  public int getFirst()
  {
    // return first cell body position visible
    return getNext( HEADER );
  }

  /******************************************* getLast *******************************************/
  public int getLast()
  {
    // return last cell body position visible
    return getPrevious( getCount() );
  }

  /******************************************* getNext *******************************************/
  public int getNext( int position )
  {
    // return next cell body position visible, or last if there isn't one
    int max = getCount() - 1;
    boolean hidden = true;
    while ( position < max && hidden )
      hidden = isPositionHidden( ++position );

    if ( hidden )
      return getLast();
    return position;
  }

  /***************************************** getPrevious *****************************************/
  public int getPrevious( int position )
  {
    // return previous cell body position visible, or first if there isn't one
    boolean hidden = true;
    while ( position > FIRSTCELL && hidden )
      hidden = isPositionHidden( --position );

    if ( hidden )
      return getFirst();
    return position;
  }

}
