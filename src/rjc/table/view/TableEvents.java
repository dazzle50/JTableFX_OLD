/**************************************************************************
 *  Copyright (C) 2018 by Richard Crook                                   *
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

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/*************************************************************************************************/
/*************************** Handles canvas mouse and keyboard events ****************************/
/*************************************************************************************************/

public class TableEvents extends TableSelection
{
  private int m_cellXstart; // current mouse cell X start
  private int m_cellXend;   // current mouse cell X end
  private int m_cellYstart; // current mouse cell Y start
  private int m_cellYend;   // current mouse cell Y end

  /****************************************** setCursor ******************************************/
  protected void setCursor( int x, int y )
  {
    // TODO Auto-generated method stub <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

  }

  /****************************************** keyTyped *******************************************/
  protected void keyTyped( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** keyPressed ******************************************/
  protected void keyPressed( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** mouseExited *****************************************/
  protected void mouseExited( MouseEvent event )
  {
    // mouse has left table, so set mouse column/row to invalid
    mouseColumnPos.set( INVALID );
    mouseRowPos.set( INVALID );
    m_cellXstart = INVALID;
    m_cellXend = INVALID;
    m_cellYstart = INVALID;
    m_cellYend = INVALID;
  }

  /***************************************** mouseMoved ******************************************/
  protected void mouseMoved( MouseEvent event )
  {
    // determine which cell mouse is over
    int x = (int) event.getX();
    int y = (int) event.getY();
    setCursor( x, y );

    // check if mouse moved outside current column 
    if ( x < m_cellXstart || x >= m_cellXend )
    {
      int columnPos = getColumnPositionAtX( x );

      if ( columnPos == HEADER )
      {
        m_cellXstart = 0;
        m_cellXend = getRowHeaderWidth();
      }
      else if ( columnPos == LEFT )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
      }
      else if ( columnPos == RIGHT )
      {
        m_cellXstart = getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellXstart = getColumnPositionXStart( columnPos );
        m_cellXend = getColumnPositionXStart( columnPos + 1 );
      }

      mouseColumnPos.set( columnPos );
    }

    // check if mouse moved outside current row 
    if ( y < m_cellYstart || y >= m_cellYend )
    {
      int rowPos = getRowPositionAtY( y );

      if ( rowPos == HEADER )
      {
        m_cellYstart = 0;
        m_cellYend = getColumnHeaderHeight();
      }
      else if ( rowPos == ABOVE )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
      }
      else if ( rowPos == BELOW )
      {
        m_cellYstart = getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellYstart = getRowPositionYStart( rowPos );
        m_cellYend = getRowPositionYStart( rowPos + 1 );
      }

      mouseRowPos.set( rowPos );
    }
  }

  /**************************************** mouseClicked *****************************************/
  protected void mouseClicked( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mousePressed *****************************************/
  protected void mousePressed( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /*************************************** mouseReleased *****************************************/
  protected void mouseReleased( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mouseDragged *****************************************/
  protected void mouseDragged( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

}
