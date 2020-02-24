/**************************************************************************
 *  Copyright (C) 2020 by Richard Crook                                   *
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

import javafx.scene.Node;
import javafx.scene.Parent;

/*************************************************************************************************/
/****************************** JavaFX parent node for table views *******************************/
/*************************************************************************************************/

class TableParent extends Parent
{
  private int m_height; // table node height
  private int m_width;  // table node width

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

}
