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

package rjc.table.view.axis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rjc.table.signal.IListener;
import rjc.table.signal.ISignal;
import rjc.table.signal.ObservableDouble.ReadOnlyDouble;
import rjc.table.signal.ObservableInteger;
import rjc.table.signal.ObservableInteger.ReadOnlyInteger;

/*************************************************************************************************/
/****************** Table axis with header & body cell sizing including zooming ******************/
/*************************************************************************************************/

public class AxisSize extends AxisBase implements IListener
{
  // variables defining default & minimum cell size (width or height) equals pixels if zoom is 1.0
  private int                         m_defaultSize      = 100;
  private int                         m_minimumSize      = 20;
  private int                         m_headerSize       = 50;
  private ReadOnlyDouble              m_zoomProperty;

  // exceptions to default size
  final private Map<Integer, Integer> m_sizeExceptions   = new HashMap<>();

  // cached cell index to start pixel coordinate
  final private ArrayList<Integer>    m_startPixelCache  = new ArrayList<>();

  // observable integer for cached axis size in pixels (includes header)
  private ObservableInteger           m_totalPixelsCache = new ObservableInteger( INVALID );

  /**************************************** constructor ******************************************/
  public AxisSize( ReadOnlyInteger countProperty )
  {
    // pass count property to super class
    super( countProperty );
  }

  /******************************************** slot *********************************************/
  @Override
  public void slot( ISignal sender, Object... msg )
  {
    // listen to signals sent to axis
    if ( sender == getCountProperty() )
    {
      // set cached axis size to invalid
      m_totalPixelsCache.set( INVALID );

      // remove any exceptions beyond count
      int oldCount = (int) msg[0];
      int newCount = getCount();
      if ( newCount < oldCount )
        for ( int key : m_sizeExceptions.keySet() )
          if ( key >= newCount )
            m_sizeExceptions.remove( key );

      // truncate cell start cache if new size smaller
      if ( newCount < m_startPixelCache.size() )
        m_startPixelCache.subList( newCount, m_startPixelCache.size() ).clear();
    }

    else if ( sender == m_zoomProperty )
    {
      // zoom value has changed so clear the pixel caches
      m_startPixelCache.clear();
      m_totalPixelsCache.set( INVALID );
    }
  }

  /************************************** getDefaultPixels ***************************************/
  public int getDefaultPixels()
  {
    // return default cell size in pixels
    return zoom( m_defaultSize );
  }

  /************************************** getMinimumPixels ***************************************/
  public int getMinimumPixels()
  {
    // return minimum cell size in pixels
    return zoom( m_minimumSize );
  }

  /*************************************** getHeaderPixels ***************************************/
  public int getHeaderPixels()
  {
    // return header cell size in pixels
    return zoom( m_headerSize );
  }

  /*************************************** setDefaultSize ****************************************/
  public void setDefaultSize( int defaultSize )
  {
    // if requested new default size is smaller than minimum, increase new default to minimum
    if ( defaultSize < m_minimumSize )
      defaultSize = m_minimumSize;

    // if different, set default cell size and invalidate the caches
    if ( m_defaultSize != defaultSize )
    {
      // check requested new default size is at least one
      if ( defaultSize < 1 )
        throw new IllegalArgumentException( "Default size must be at least one " + defaultSize );

      m_totalPixelsCache.set( INVALID );
      m_startPixelCache.clear();
      m_defaultSize = defaultSize;
    }
  }

  /*************************************** setMinimumSize ****************************************/
  public void setMinimumSize( int minSize )
  {
    // check requested new minimum size is at least zero
    if ( minSize < 0 )
      throw new IllegalArgumentException( "Minimum size must be at least zero " + minSize );

    // if different, set minimum cell pixel size and invalidate body size
    if ( m_minimumSize != minSize )
    {
      // if default smaller than minimum, increase default to size
      if ( m_defaultSize < minSize )
        setDefaultSize( minSize );

      // if minimum size increasing, check exceptions
      if ( minSize > m_minimumSize )
        for ( var exception : m_sizeExceptions.entrySet() )
          if ( exception.getValue() < minSize )
            exception.setValue( minSize );

      m_totalPixelsCache.set( INVALID );
      m_startPixelCache.clear();
      m_minimumSize = minSize;
    }
  }

  /**************************************** setHeaderSize ****************************************/
  public void setHeaderSize( int headerSize )
  {
    // check new size is valid
    if ( headerSize < 0 || headerSize >= 65536 )
      throw new IllegalArgumentException( "Header size must be at least zero " + headerSize );

    // if new size is different, clear cell position start cache
    if ( m_headerSize != headerSize )
    {
      if ( getTotalPixels() != INVALID )
        m_totalPixelsCache.set( getTotalPixels() - m_headerSize + headerSize );

      m_startPixelCache.clear();
      m_headerSize = headerSize;
    }

  }

  /*************************************** setZoomProperty ***************************************/
  public void setZoomProperty( ReadOnlyDouble zoomProperty )
  {
    // remove listening from old zoom property
    if ( m_zoomProperty != null )
      m_zoomProperty.removeListener( this );

    // add listening to new zoom property
    if ( zoomProperty != null )
      zoomProperty.addListener( this );

    // adopt new zoom
    m_zoomProperty = zoomProperty;
    m_startPixelCache.clear();
    m_totalPixelsCache.set( INVALID );
  }

  /******************************************** zoom *********************************************/
  private int zoom( int size )
  {
    // convenience method to return pixels from size
    if ( m_zoomProperty == null )
      return size;

    return (int) ( size * m_zoomProperty.get() );
  }

  /*************************************** isIndexVisible ****************************************/
  public boolean isIndexVisible( int index )
  {
    // overload this function if row/column hiding is wanted
    return true;
  }

  /*************************************** getTotalPixels ****************************************/
  public int getTotalPixels()
  {
    // return axis total size in pixels (including header)
    if ( m_totalPixelsCache.get() == INVALID )
    {
      // cached size is invalid, so re-calculate
      int defaultCount = getCount() - m_sizeExceptions.size();

      int pixels = getHeaderPixels();
      for ( var exception : m_sizeExceptions.entrySet() )
      {
        if ( isIndexVisible( exception.getKey() ) )
          pixels += zoom( exception.getValue() );
      }

      m_totalPixelsCache.set( pixels + defaultCount * getDefaultPixels() );
    }

    return m_totalPixelsCache.get();
  }

  /*********************************** getTotalPixelsProperty ************************************/
  public ReadOnlyInteger getTotalPixelsProperty()
  {
    // return return read-only version of axis total pixels size
    return m_totalPixelsCache.getReadOnly();
  }

  /*************************************** getIndexPixels ****************************************/
  public int getIndexPixels( int index )
  {
    // return header size if that was requested
    if ( index == HEADER )
      return m_headerSize;

    // if index is hidden return zero
    if ( !isIndexVisible( index ) )
      return 0;

    // return cell size from exception or default
    int size = m_sizeExceptions.getOrDefault( index, m_defaultSize );
    return zoom( size );
  }

  /**************************************** getStartPixel ****************************************/
  public int getStartPixel( int index, int scroll )
  {
    // check index is valid
    if ( index < HEADER || index > getCount() )
      throw new IndexOutOfBoundsException( "position=" + index + " but count=" + getCount() );

    // if header, return zero
    if ( index == HEADER )
      return 0;

    // if cell index is beyond cache, extend cache
    if ( index >= m_startPixelCache.size() )
    {
      // index zero starts after header
      if ( m_startPixelCache.isEmpty() )
        m_startPixelCache.add( getHeaderPixels() );

      int position = m_startPixelCache.size() - 1;
      int start = m_startPixelCache.get( position );
      while ( index > position )
      {
        start += getIndexPixels( position++ );
        m_startPixelCache.add( start );
      }
    }

    // return start pixel coordinate for cell index taking scroll into account
    return m_startPixelCache.get( index ) - scroll;
  }

  /*********************************** getIndexFromCoordinate ************************************/
  public int getIndexFromCoordinate( int coordinate, int scroll )
  {
    // check if before table
    if ( coordinate < 0 )
      return BEFORE;

    // check if header
    if ( coordinate < getHeaderPixels() )
      return HEADER;

    // check if after table
    coordinate += scroll;
    if ( coordinate >= getTotalPixels() )
      return AFTER;

    // check within start cache
    int position = m_startPixelCache.size() - 1;
    int start = position < 0 ? 0 : m_startPixelCache.get( position );
    if ( coordinate > start )
    {
      while ( coordinate > start )
      {
        start += getIndexPixels( position++ );
        m_startPixelCache.add( start );
      }
      return coordinate == start ? position : position - 1;
    }

    // find position by binary search of cache
    int startPos = 0;
    int endPos = m_startPixelCache.size();
    while ( startPos != endPos )
    {
      int rowPos = ( endPos + startPos ) / 2;
      if ( m_startPixelCache.get( rowPos ) <= coordinate )
        startPos = rowPos + 1;
      else
        endPos = rowPos;
    }
    return startPos - 1;
  }

}
