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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import rjc.table.Utils;
import rjc.table.signal.ObservableDouble.ReadOnlyDouble;
import rjc.table.signal.ObservableInteger;
import rjc.table.signal.ObservableInteger.ReadOnlyInteger;

/*************************************************************************************************/
/***************** Base class for table X or Y axis with index to pixel mapping ******************/
/*************************************************************************************************/

public class AxisBase
{
  // axis index starts at 0 for table body, index of -1 is for axis header
  final static public int             INVALID               = -2;
  final static public int             HEADER                = -1;
  final static public int             FIRSTCELL             = 0;

  // count of body cells on axis
  private ReadOnlyInteger             m_countProperty;

  // variables defining default & minimum cell size (width or height) equals pixels if zoom is 1.0
  private int                         m_defaultSize         = 100;
  private int                         m_minimumSize         = 20;
  private int                         m_headerSize          = 50;
  private ReadOnlyDouble              m_zoomProperty;

  // exceptions to default size
  final private Map<Integer, Integer> m_sizeExceptions      = new HashMap<>();

  // cached cell index to start pixel coordinate
  final private ArrayList<Integer>    m_cellStartPixelCache = new ArrayList<>();

  // observable integer for cached axis size in pixels (includes header)
  private ObservableInteger           m_totalPixelsCache    = new ObservableInteger( INVALID );

  /**************************************** constructor ******************************************/
  public AxisBase( ReadOnlyInteger countProperty )
  {
    // check arguments
    if ( countProperty == null || countProperty.get() < 0 )
      throw new IllegalArgumentException( "Bad body cell count = " + countProperty );

    // if axis count changes
    m_countProperty = countProperty;
    m_countProperty.addListener( signal ->
    {
      // set cached axis size to invalid and remove any exceptions beyond count
      m_totalPixelsCache.set( INVALID );

      int old_count = (int) Array.get( signal, 1 );
      int new_count = m_countProperty.get();
      if ( new_count < old_count )
        for ( int key : m_sizeExceptions.keySet() )
          if ( key >= new_count )
            m_sizeExceptions.remove( key );

      // truncate cell start cache if new size smaller
      if ( new_count < m_cellStartPixelCache.size() )
        m_cellStartPixelCache.subList( new_count, m_cellStartPixelCache.size() ).clear();
    } );
  }

  /****************************************** getCount *******************************************/
  final public int getCount()
  {
    // return count of body cells on axis
    return m_countProperty.get();
  }

  /*************************************** getDefaultSize ****************************************/
  public int getDefaultSize()
  {
    // return default cell size
    return m_defaultSize;
  }

  /*************************************** getMinimumSize ****************************************/
  public int getMinimumSize()
  {
    // return minimum cell size
    return m_minimumSize;
  }

  /**************************************** getHeaderSize ****************************************/
  public int getHeaderSize()
  {
    // return header cell size
    return m_headerSize;
  }

  /*************************************** setDefaultSize ****************************************/
  public void setDefaultSize()
  {
    // return default cell size
    Utils.trace( "TODO" );
  }

  /*************************************** setMinimumSize ****************************************/
  public void setMinimumSize()
  {
    // return minimum cell size
    Utils.trace( "TODO" );
  }

  /**************************************** setHeaderSize ****************************************/
  public void setHeaderSize()
  {
    // return header cell size
    Utils.trace( "TODO" );
  }

  /*************************************** setZoomProperty ***************************************/
  public void setZoomProperty( ReadOnlyDouble zoomProperty )
  {
    // set zoom property
    if ( zoomProperty == null )
    {
    }
    else
    {

      // if zoom changes
      m_zoomProperty.addListener( x ->
      {
        // clear cell start cache and axis size cache
        m_cellStartPixelCache.clear();
        m_totalPixelsCache.set( INVALID );
      } );
    }
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

  /**************************************** getAxisPixels ****************************************/
  public int getAxisPixels()
  {
    // return axis total size in pixels (including header)
    if ( m_totalPixelsCache.get() == INVALID )
    {
      // cached size is invalid, so re-calculate
      int defaultCount = getCount() - m_sizeExceptions.size();

      int pixels = zoom( m_headerSize );
      for ( var exception : m_sizeExceptions.entrySet() )
      {
        if ( isIndexVisible( exception.getKey() ) )
          pixels += zoom( exception.getValue() );
      }

      m_totalPixelsCache.set( pixels + defaultCount * zoom( m_defaultSize ) );
    }

    return m_totalPixelsCache.get();
  }

  /*********************************** getTotalPixelsProperty ************************************/
  public ReadOnlyInteger getTotalPixelsProperty()
  {
    // return return read-only version of axis total pixels size
    return m_totalPixelsCache.getReadOnly();
  }

}
