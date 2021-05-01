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

package rjc.table.view.axis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rjc.table.signal.ObservableInteger;
import rjc.table.signal.ObservableInteger.ReadOnlyInteger;

/*************************************************************************************************/
/*********************** Controls axis cell pixel size (including header) ************************/
/*************************************************************************************************/

public class AxisSize extends AxisBase
{
  // variables defining default & minimum cell size (width or height) equals pixels if zoom is 1.0
  private int                         m_defaultSize;
  private int                         m_minimumSize;
  private int                         m_headerSize;
  private double                      m_zoom                   = 1.0;

  // exceptions to default size, -ve means hidden
  final private Map<Integer, Integer> m_sizeExceptions         = new HashMap<>();

  // cached cell position start pixel coordinate
  final private ArrayList<Integer>    m_cellPositionStartCache = new ArrayList<>();

  // observable integer for axis total body size in pixels (excludes header)
  private ObservableInteger           m_bodyPixelsCache        = new ObservableInteger( INVALID );

  /***************************************** constructor *****************************************/
  public AxisSize( ReadOnlyInteger countProperty )
  {
    // call super
    super( countProperty );

    // if axis count changes
    countProperty.addListener( x ->
    {
      // set cached body size to invalid and remove any exceptions beyond count
      m_bodyPixelsCache.set( INVALID );
      int count = countProperty.get();
      for ( int key : m_sizeExceptions.keySet() )
        if ( key >= count )
          m_sizeExceptions.remove( key );

      // truncate cell position start if size greater than new count
      truncateCache( count, 0 );
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
    m_zoom = 1.0;
    m_sizeExceptions.clear();
    m_cellPositionStartCache.clear();
    m_bodyPixelsCache.set( INVALID );
  }

  /******************************************* setZoom *******************************************/
  public void setZoom( double zoom )
  {
    // set zoom scale for this axis
    m_zoom = zoom;
    m_cellPositionStartCache.clear();
    m_bodyPixelsCache.set( INVALID );
  }

  /******************************************** zoom *********************************************/
  private int zoom( int size )
  {
    // convenience method to return pixels from size
    return (int) ( size * m_zoom );
  }

  /**************************************** getBodyPixels ****************************************/
  public int getBodyPixels()
  {
    // return axis total body size in pixels (excludes header) 
    if ( m_bodyPixelsCache.get() == INVALID )
    {
      // cached size is invalid, so re-calculate size of table body cells
      int defaultCount = getCount();
      int bodySize = 0;

      for ( int key : m_sizeExceptions.keySet() )
      {
        defaultCount--;
        int size = zoom( m_sizeExceptions.get( key ) );
        if ( size > 0 )
          bodySize += size;
      }

      m_bodyPixelsCache.set( bodySize + defaultCount * zoom( m_defaultSize ) );
    }

    return m_bodyPixelsCache.get();
  }

  /************************************ getBodyPixelsProperty ************************************/
  final public ReadOnlyInteger getBodyPixelsProperty()
  {
    // return read-only property for body size in pixels (excludes header)
    return m_bodyPixelsCache.getReadOnly();
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
    // if requested new default size is smaller than minimum, increase new default to minimum
    if ( defaultSize < m_minimumSize )
      defaultSize = m_minimumSize;

    // if different, set default cell pixel size and invalidate body size
    if ( m_defaultSize != defaultSize )
    {
      // check requested new default size is at least one
      if ( defaultSize < 1 )
        throw new IllegalArgumentException( "Default size must be at least one " + defaultSize );

      m_defaultSize = defaultSize;
      m_bodyPixelsCache.set( INVALID );
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

        m_bodyPixelsCache.set( INVALID );
        m_cellPositionStartCache.clear();
      }

      m_minimumSize = minSize;
    }
  }

  /*************************************** getHeaderPixels ***************************************/
  public int getHeaderPixels()
  {
    // return header cell size in pixels
    return zoom( m_headerSize );
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

  /**************************************** getCellPixels ****************************************/
  public int getCellPixels( int cellIndex )
  {
    // return cell size in pixels
    return zoom( getCellSize( cellIndex ) );
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
      int position = getPositionFromIndex( cellIndex ) + 1;
      int delta = zoom( newSize ) - zoom( oldSize );
      truncateCache( position, delta );
    }
  }

  /**************************************** clearCellSize ****************************************/
  public void clearCellSize( int cellIndex )
  {
    // check cell index is valid
    if ( cellIndex < FIRSTCELL || cellIndex >= getCount() )
      throw new IndexOutOfBoundsException( "cell index=" + cellIndex + " but count=" + getCount() );

    // remove cell index size exception if exists
    if ( m_sizeExceptions.remove( cellIndex ) != null )
    {
      m_bodyPixelsCache.set( INVALID );
      m_cellPositionStartCache.clear();
    }
  }

  /************************************* clearSizeExceptions *************************************/
  public void clearSizeExceptions()
  {
    // clear all size exceptions
    m_sizeExceptions.clear();
    m_cellPositionStartCache.clear();
    m_bodyPixelsCache.set( INVALID );
  }

  /************************************** getSizeExceptions **************************************/
  public Map<Integer, Integer> getSizeExceptions()
  {
    // return size exceptions
    return Collections.unmodifiableMap( m_sizeExceptions );
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
        m_cellPositionStartCache.add( getHeaderPixels() );

      int position = m_cellPositionStartCache.size() - 1;
      int start = m_cellPositionStartCache.get( position );
      while ( cellPosition > position )
      {
        start += getCellPixels( getIndexFromPosition( position++ ) );
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
    if ( coordinate < getHeaderPixels() )
      return HEADER;

    // check if after table
    coordinate += scroll;
    if ( coordinate >= getBodyPixels() + getHeaderPixels() )
      return AFTER;

    // check within start cache
    int position = m_cellPositionStartCache.size() - 1;
    int start = position < 0 ? 0 : m_cellPositionStartCache.get( position );
    if ( coordinate > start )
    {
      while ( coordinate > start )
      {
        start += getCellPixels( getIndexFromPosition( position++ ) );
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

  /****************************************** hideIndex ******************************************/
  public void hideIndex( int index )
  {
    // if index not already hidden, set size exception and update body size
    int oldSize = m_sizeExceptions.getOrDefault( index, m_defaultSize );
    if ( oldSize > 0 )
    {
      m_sizeExceptions.put( index, -oldSize );
      truncateCache( TableAxis.FIRSTCELL, -zoom( oldSize ) );
    }
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
      truncateCache( position, -zoom( oldSize ) );
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

      truncateCache( position, -zoom( oldSize ) );
    }
  }

  /**************************************** truncateCache ****************************************/
  public void truncateCache( int position, int deltaSize )
  {
    // update body size cache if not invalid
    if ( m_bodyPixelsCache.get() != INVALID )
      m_bodyPixelsCache.set( m_bodyPixelsCache.get() + deltaSize );

    // truncate cell position start cache if size greater than position
    if ( m_cellPositionStartCache.size() > position )
      m_cellPositionStartCache.subList( position, m_cellPositionStartCache.size() ).clear();
  }

  /**************************************** movePosition *****************************************/
  public void movePosition( int oldPosition, int newPosition )
  {
    // re-order index on axis and truncate cell location cache
    HashSet<Integer> set = new HashSet<>( 1 );
    set.add( oldPosition );
    movePositions( set, newPosition );
  }

  /**************************************** movePositions ****************************************/
  @Override
  public void movePositions( Set<Integer> positions, int newPosition )
  {
    // re-order indexes on axis and truncate cell location cache
    int pos = Math.min( Collections.min( positions ), newPosition );
    truncateCache( pos, 0 );
    super.movePositions( positions, newPosition );
  }

  /************************************** getVisibleIndexes **************************************/
  public ArrayList<Integer> getVisibleIndexes( int position1, int position2 )
  {
    // return list of visible cell indexes between two positions inclusive
    ArrayList<Integer> indexes = new ArrayList<>();
    for ( int pos = position1; pos <= position2; pos++ )
    {
      int index = getIndexFromPosition( pos );
      if ( getCellSize( index ) > 0 )
        indexes.add( index );
    }

    return indexes;
  }

}