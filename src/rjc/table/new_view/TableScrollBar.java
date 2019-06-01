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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.util.Duration;

/*************************************************************************************************/
/*************** Extended version of ScrollBar with special increment & decrement ****************/
/*************************************************************************************************/

public class TableScrollBar extends ScrollBar
{
  private TableAxis       m_axis;                   // associated table axis
  private Timeline        m_timeline;               // used for animated table scrolling
  private int             m_scrollingTo      = -1;  // destination scroll-bar value for current animation

  final public static int SIZE               = 18;  // pixels
  final public static int SCROLL_TO_DURATION = 100; // milliseconds

  /**************************************** constructor ******************************************/
  public TableScrollBar( TableAxis axis, Orientation orientation )
  {
    // create scroll bar
    m_axis = axis;
    setOrientation( orientation );

    // set width/height
    if ( orientation == Orientation.VERTICAL )
    {
      setMinWidth( SIZE );
      setMinHeight( USE_PREF_SIZE );
      setMaxHeight( USE_PREF_SIZE );
    }
    else
    {
      setMinWidth( USE_PREF_SIZE );
      setMaxWidth( USE_PREF_SIZE );
      setMinHeight( SIZE );
    }
  }

  /****************************************** increment ******************************************/
  @Override
  public void increment()
  {
    // increase scroll bar value to next table cell boundary
    if ( getOrientation() == Orientation.VERTICAL )
    {
      // TODO
    }
    else
    {
      // TODO
    }
  }

  /****************************************** decrement ******************************************/
  @Override
  public void decrement()
  {
    // decrease scroll bar value to next table cell boundary
    if ( getOrientation() == Orientation.VERTICAL )
    {
      // TODO
    }
    else
    {
      // TODO
    }
  }

  /****************************************** scrollTo *******************************************/
  public void scrollTo( int position )
  {
    // if scroll bar not visible, no need to scroll
    if ( !isVisible() )
      return;

    // check if need to scroll towards start to show cell start
    int start = m_axis.getStartFromPosition( position, (int) getValue() ) - m_axis.getCellSize( TableView.HEADER );
    if ( start < 0 )
    {
      animate( (int) getValue() + start, SCROLL_TO_DURATION );
      return;
    }

    // check if need to scroll towards end to show cell end, without hiding start
    int size = getOrientation() == Orientation.VERTICAL ? (int) getHeight() : (int) getWidth();
    int end = size - m_axis.getStartFromPosition( position + 1, (int) getValue() );
    if ( -end > start )
      end = -start;
    if ( end < 0 )
      animate( (int) getValue() - end, SCROLL_TO_DURATION );
  }

  /******************************************* animate *******************************************/
  public void animate( int newValue, int duration_ms )
  {
    // if already scrolling to specified new-value, no need to start new animation
    if ( newValue == m_scrollingTo )
      return;

    // stop any current animation first
    if ( m_timeline != null )
      m_timeline.pause();

    // create scrolling animation
    m_scrollingTo = newValue;
    KeyValue kv = new KeyValue( valueProperty(), newValue );
    KeyFrame kf = new KeyFrame( Duration.millis( duration_ms ), kv );
    m_timeline = new Timeline( kf );
    m_timeline.setOnFinished( event ->
    {
      m_timeline = null;
      m_scrollingTo = -1;
    } );
    m_timeline.play();
  }

  /****************************************** toString *******************************************/
  @Override
  public String toString()
  {
    return "VAL=" + getValue() + " MIN=" + getMin() + " MAX=" + getMax() + " VIS=" + getVisibleAmount() + " BLK"
        + getBlockIncrement() + " UNIT" + getUnitIncrement() + " ORIENT=" + getOrientation();
  }

}
