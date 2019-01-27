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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;

/*************************************************************************************************/
/****************************** JavaFX parent node for table views *******************************/
/*************************************************************************************************/

class TableParent extends Parent
{
  private int      m_height;    // table node height
  private int      m_width;     // table node width
  private Timeline m_animation; // used for smooth table scrolling

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // resize the table parent
    m_width = (int) width;
    m_height = (int) height;
  }

  /**************************************** isResizable ******************************************/
  @Override
  public boolean isResizable()
  {
    // table parent is resizable
    return true;
  }

  /***************************************** minHeight *******************************************/
  @Override
  public double minHeight( double width )
  {
    // table parent minimum height is zero
    return 0.0;
  }

  /****************************************** minWidth *******************************************/
  @Override
  public double minWidth( double height )
  {
    // table parent minimum width is zero
    return 0.0;
  }

  /***************************************** prefHeight ******************************************/
  @Override
  public double prefHeight( double width )
  {
    // table parent will take as much space as it can get so scroll-bars at edge of area
    return Integer.MAX_VALUE;
  }

  /****************************************** prefWidth ******************************************/
  @Override
  public double prefWidth( double height )
  {
    // table parent will take as much space as it can get so scroll-bars at edge of area
    return Integer.MAX_VALUE;
  }

  /****************************************** getWidth *******************************************/
  public int getWidth()
  {
    // return table parent node width
    return m_width;
  }

  /****************************************** getHeight ******************************************/
  public int getHeight()
  {
    // return table parent node height
    return m_height;
  }

  /********************************************* add *********************************************/
  public void add( Node node )
  {
    // add node to table displayed children
    getChildren().add( node );
  }

  /******************************************* remove ********************************************/
  public void remove( Node node )
  {
    // remove node from table displayed children
    getChildren().remove( node );
  }

  /******************************************* animate *******************************************/
  public void animate( DoubleProperty property, int endValue, int duration_ms )
  {
    // create animation stopping any current animation first
    stopAnimation();

    KeyValue kv = new KeyValue( property, endValue );
    KeyFrame kf = new KeyFrame( Duration.millis( duration_ms ), kv );
    m_animation = new Timeline( kf );
    m_animation.play();

    // when animation finished clear pointer
    m_animation.setOnFinished( event -> m_animation = null );
  }

  /*************************************** finishAnimation ***************************************/
  public void finishAnimation()
  {
    // finish animation
    if ( m_animation == null )
      return;

    m_animation.jumpTo( "end" );
    m_animation = null;
  }

  /**************************************** stopAnimation ****************************************/
  public void stopAnimation()
  {
    // stop animation
    if ( m_animation == null )
      return;

    m_animation.stop();
    m_animation = null;
  }

  /************************************* isAnimationFinished *************************************/
  public boolean isAnimationFinished()
  {
    // return true if no animation is in progress
    return m_animation == null;
  }

}
