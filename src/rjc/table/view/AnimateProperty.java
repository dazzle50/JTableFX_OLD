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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.util.Duration;

/*************************************************************************************************/
/*************************** Supports animated smooth table scrolling ****************************/
/*************************************************************************************************/

public class AnimateProperty
{
  private Timeline m_timeline; // used for smooth table scrolling

  /******************************************* animate *******************************************/
  public void animate( DoubleProperty property, int endValue, int duration_ms )
  {
    // create animation stopping any current animation first
    stopAnimation();

    KeyValue kv = new KeyValue( property, endValue );
    KeyFrame kf = new KeyFrame( Duration.millis( duration_ms ), kv );
    m_timeline = new Timeline( kf );
    m_timeline.play();
  }

  /*************************************** finishAnimation ***************************************/
  public void finishAnimation()
  {
    // finish animation (jump to end)
    if ( m_timeline != null )
    {
      m_timeline.jumpTo( m_timeline.getTotalDuration() );
      m_timeline = null;
    }
  }

  /**************************************** stopAnimation ****************************************/
  public void stopAnimation()
  {
    // stop (pause where is) animation
    if ( m_timeline != null )
    {
      m_timeline.pause();
      m_timeline = null;
    }
  }

}
