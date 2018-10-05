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

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/*************************************************************************************************/
/********************** Canvas where the table headers and cells are drawn ***********************/
/*************************************************************************************************/

public class TableCanvas extends Canvas
{
  private TableView m_view;       // associated table view

  private int       m_cellXstart; // current mouse cell X start
  private int       m_cellXend;   // current mouse cell X end
  private int       m_cellYstart; // current mouse cell Y start
  private int       m_cellYend;   // current mouse cell Y end

  /***************************************** constructor *****************************************/
  public TableCanvas( TableView view )
  {
    // setup table canvas
    setFocusTraversable( true );
    m_view = view;

    // when size changes draw new bits
    widthProperty().addListener( ( observable, oldW, newW ) -> widthChange( oldW.intValue(), newW.intValue() ) );
    heightProperty().addListener( ( observable, oldH, newH ) -> heightChange( oldH.intValue(), newH.intValue() ) );

    // redraw table when focus changes
    focusedProperty().addListener( ( observable, oldF, newF ) -> redraw() );

    // react to mouse events
    setOnMouseExited( event -> mouseExited( event ) );
    setOnMouseMoved( event -> mouseMoved( event ) );
    setOnMouseDragged( event -> mouseDragged( event ) );
    setOnMouseReleased( event -> mouseReleased( event ) );
    setOnMousePressed( event -> mousePressed( event ) );
    setOnMouseClicked( event -> mouseClicked( event ) );

    // react to keyboard events
    setOnKeyPressed( event -> keyPressed( event ) );
    setOnKeyTyped( event -> keyTyped( event ) );
  }

  /****************************************** keyTyped *******************************************/
  private void keyTyped( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** keyPressed ******************************************/
  private void keyPressed( KeyEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /***************************************** mouseExited *****************************************/
  private void mouseExited( MouseEvent event )
  {
    // mouse has left table, so set mouse column/row to invalid
    m_view.mouseColumnPos.set( TableView.INVALID );
    m_view.mouseRowPos.set( TableView.INVALID );
    m_cellXstart = TableView.INVALID;
    m_cellXend = TableView.INVALID;
    m_cellYstart = TableView.INVALID;
    m_cellYend = TableView.INVALID;
  }

  /***************************************** mouseMoved ******************************************/
  private void mouseMoved( MouseEvent event )
  {
    // determine which cell mouse is over
    int x = (int) event.getX();
    int y = (int) event.getY();

    // check if mouse moved outside current column 
    if ( x < m_cellXstart || x >= m_cellXend )
    {
      int columnPos = m_view.getColumnPositionAtX( x );

      if ( columnPos == TableView.HEADER )
      {
        m_cellXstart = 0;
        m_cellXend = m_view.getRowHeaderWidth();
      }
      else if ( columnPos == TableView.LEFT )
      {
        m_cellXstart = Integer.MIN_VALUE;
        m_cellXend = 0;
      }
      else if ( columnPos == TableView.RIGHT )
      {
        m_cellXstart = m_view.getTableWidth();
        m_cellXend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellXstart = m_view.getColumnPositionXStart( columnPos );
        m_cellXend = m_view.getColumnPositionXStart( columnPos + 1 );
      }

      m_view.mouseColumnPos.set( columnPos );
    }

    // check if mouse moved outside current row 
    if ( y < m_cellYstart || y >= m_cellYend )
    {
      int rowPos = m_view.getRowPositionAtY( y );

      if ( rowPos == TableView.HEADER )
      {
        m_cellYstart = 0;
        m_cellYend = m_view.getColumnHeaderHeight();
      }
      else if ( rowPos == TableView.ABOVE )
      {
        m_cellYstart = Integer.MIN_VALUE;
        m_cellYend = 0;
      }
      else if ( rowPos == TableView.BELOW )
      {
        m_cellYstart = m_view.getTableHeight();
        m_cellYend = Integer.MAX_VALUE;
      }
      else
      {
        m_cellYstart = m_view.getRowPositionYStart( rowPos );
        m_cellYend = m_view.getRowPositionYStart( rowPos + 1 );
      }

      m_view.mouseRowPos.set( rowPos );
    }
  }

  /**************************************** mouseClicked *****************************************/
  private void mouseClicked( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mousePressed *****************************************/
  private void mousePressed( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /*************************************** mouseReleased *****************************************/
  private void mouseReleased( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /**************************************** mouseDragged *****************************************/
  private void mouseDragged( MouseEvent event )
  {
    // TODO Auto-generated method stub #########################################
  }

  /******************************************* redraw ********************************************/
  public void redraw()
  {
    // return entire canvas
    widthChange( 0, (int) getWidth() );
  }

  /***************************************** widthChange *****************************************/
  private void widthChange( int oldW, int newW )
  {
    // only need to draw if new width is larger than old width
    if ( newW > oldW && m_view.draw.get() )
    {
      // calculate which columns need to be redrawn
      int minColumnPos = m_view.getColumnPositionAtX( oldW );
      if ( minColumnPos == TableView.HEADER )
        minColumnPos = m_view.getColumnPositionAtX( m_view.getRowHeaderWidth() );
      int maxColumnPos = m_view.getColumnPositionAtX( newW );
      m_view.redrawColumns( minColumnPos, maxColumnPos );

      // check if row header needs to be redrawn
      if ( oldW < m_view.getRowHeaderWidth() )
        m_view.redrawColumn( TableView.HEADER );
    }
  }

  /**************************************** heightChange *****************************************/
  private void heightChange( int oldH, int newH )
  {
    // only need to draw if new height is larger than old height
    if ( newH > oldH && m_view.draw.get() )
    {
      // calculate which rows need to be redrawn, and redraw them
      int minRowPos = m_view.getRowPositionAtY( oldH );
      if ( minRowPos == TableView.HEADER )
        minRowPos = m_view.getRowPositionAtY( m_view.getColumnHeaderHeight() );
      int maxRowPos = m_view.getRowPositionAtY( newH );
      m_view.redrawRows( minRowPos, maxRowPos );

      // check if column header needs to be redrawn
      if ( oldH < m_view.getColumnHeaderHeight() )
        m_view.redrawRow( TableView.HEADER );
    }
  }

}
