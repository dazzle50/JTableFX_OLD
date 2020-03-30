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

package rjc.table.undo;

import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import rjc.table.Colors;
import rjc.table.Utils;

/*************************************************************************************************/
/****************************** Window for undo-stack command list *******************************/
/*************************************************************************************************/

public class UndoStackWindow extends Stage
{
  private UndoStack m_undostack;
  private Canvas    m_canvas;
  private ScrollBar m_scrollbar;
  private int       m_rowDescent;
  private int       m_rowHeight;
  private boolean   m_redrawIsRequested;

  /**************************************** constructor ******************************************/
  public UndoStackWindow( UndoStack undostack )
  {
    // get default string bounds
    Bounds bounds = ( new Text( "Qwerty" ) ).getLayoutBounds();
    m_rowHeight = (int) Math.ceil( bounds.getHeight() );
    m_rowDescent = (int) Math.floor( -bounds.getMinY() );

    // create undo-stack window
    setTitle( "Undostack" );
    setWidth( bounds.getWidth() * 7 );
    setHeight( bounds.getHeight() * 20 );
    m_undostack = undostack;
    undostack.addListener( observable -> requestRedraw() );

    // setup scroll bar
    m_scrollbar = new ScrollBar();
    m_scrollbar.setOrientation( Orientation.VERTICAL );
    m_scrollbar.setMinWidth( 18 );
    m_scrollbar.setVisible( false );
    m_scrollbar.valueProperty().addListener( ( observable, oldV, newV ) -> requestRedraw() );

    // setup canvas
    m_canvas = new Canvas();
    m_canvas.setFocusTraversable( true );
    m_canvas.focusedProperty().addListener( ( observable, oldF, newF ) -> requestRedraw() );
    m_canvas.setOnMousePressed( event -> setIndex( getIndexAtY( event.getY() ) + 1 ) );
    m_canvas.setOnMouseDragged( event -> setIndex( getIndexAtY( event.getY() ) + 1 ) );
    m_canvas.setOnScroll( event ->
    {
      // increase or decrease undo-stack index on mouse scroll event
      if ( event.getDeltaY() > 0 )
        setIndex( m_undostack.getIndex() - 1 );
      else
        setIndex( m_undostack.getIndex() + 1 );
    } );
    m_canvas.setOnKeyPressed( event ->
    {
      // update undo-stack index on key press event
      switch ( event.getCode() )
      {
        case HOME:
          setIndex( 0 );
          break;
        case END:
          setIndex( m_undostack.getSize() );
          break;
        case PAGE_UP:
          setIndex( m_undostack.getIndex() - (int) ( m_canvas.getHeight() / m_rowHeight ) );
          break;
        case PAGE_DOWN:
          setIndex( m_undostack.getIndex() + (int) ( m_canvas.getHeight() / m_rowHeight ) );
          break;
        case UP:
        case KP_UP:
          setIndex( m_undostack.getIndex() - 1 );
          break;
        case DOWN:
        case KP_DOWN:
          setIndex( m_undostack.getIndex() + 1 );
          break;
        default:
          break;
      }
    } );

    // set horizontal layout for canvas and scroll-bar    
    HBox hbox = new HBox();
    hbox.getChildren().addAll( m_canvas, m_scrollbar );

    // setup scene
    Scene scene = new Scene( hbox );
    scene.heightProperty().addListener( ( observable, oldH, newH ) -> resize() );
    scene.widthProperty().addListener( ( observable, oldW, newW ) -> resize() );
    setScene( scene );
  }

  /******************************************* setIndex ******************************************/
  private void setIndex( int index )
  {
    // set undo-stack index
    index = Utils.clamp( index, 0, m_undostack.getSize() );
    m_undostack.setIndex( index );

    // make specified index visible
    int y = getYStart( index - 1 );
    if ( y < 0 )
      m_scrollbar.setValue( m_scrollbar.getValue() + y );
    else if ( y > m_canvas.getHeight() - m_rowHeight )
      m_scrollbar.setValue( m_scrollbar.getValue() + y - m_canvas.getHeight() + m_rowHeight );
  }

  /******************************************** resize *******************************************/
  private void resize()
  {
    // resize canvas and scroll-bar, do nothing if window not showing
    if ( !isShowing() )
      return;

    // set scroll bar to correct visibility
    double fullHeight = m_rowHeight * ( m_undostack.getSize() + 1 );
    boolean need = getScene().getHeight() < fullHeight;
    m_scrollbar.setVisible( need );

    // set scroll bars correct thumb size and position
    if ( m_scrollbar.isVisible() )
    {
      double max = fullHeight - getScene().getHeight();
      m_scrollbar.setMax( max );
      m_scrollbar.setVisibleAmount( max * getScene().getHeight() / fullHeight );
      if ( m_scrollbar.getValue() > max )
        m_scrollbar.setValue( max );
    }
    else
      m_scrollbar.setValue( 0.0 );

    // set canvas to correct size to not overlap scroll bars
    m_canvas.setHeight( getScene().getHeight() );
    m_canvas.setWidth( getScene().getWidth() - ( m_scrollbar.isVisible() ? m_scrollbar.getWidth() : 0 ) );

    // request canvas is redrawn
    requestRedraw();
  }

  /**************************************** requestRedraw ****************************************/
  private void requestRedraw()
  {
    // request redraw of undo-stack window contents
    if ( isShowing() && !m_redrawIsRequested )
    {
      m_redrawIsRequested = true;
      Platform.runLater( () -> redraw() );
    }
  }

  /******************************************** redraw *******************************************/
  private void redraw()
  {
    // redraw undo-stack window contents
    m_redrawIsRequested = false;
    GraphicsContext gc = m_canvas.getGraphicsContext2D();
    gc.setFontSmoothingType( FontSmoothingType.LCD );

    // fill background
    gc.setFill( Colors.CELL_DEFAULT_FILL );
    gc.fillRect( 0.0, 0.0, m_canvas.getWidth(), m_canvas.getHeight() );

    // determine undo-stack visible range
    int min = getIndexAtY( 0.0 );
    int max = getIndexAtY( m_canvas.getHeight() - 1.0 );

    // draw undo-stack text
    String text;
    for ( int item = min; item <= max; item++ )
    {
      if ( item < 0 )
        text = "<empty>";
      else
        text = m_undostack.getText( item );

      // colour current index item differently
      int y = getYStart( item );
      if ( item == m_undostack.getIndex() - 1 )
      {
        if ( m_canvas.isFocused() )
          gc.setFill( Colors.CELL_SELECTED_FILL );
        else
          gc.setFill( Colors.CELL_SELECTED_FILL.desaturate().desaturate() );

        gc.fillRect( 0.0, y, getWidth(), m_rowHeight );
        gc.setFill( Colors.TEXT_SELECTED );
      }
      else
        gc.setFill( Colors.TEXT_DEFAULT );

      gc.fillText( text, 3.0, y + m_rowDescent );
    }
  }

  /****************************************** getYStart ******************************************/
  private int getYStart( int index )
  {
    // get start y-coordinate for index row on canvas
    return (int) ( ( index + 1 ) * m_rowHeight - m_scrollbar.getValue() );
  }

  /***************************************** getIndexAtY *****************************************/
  private int getIndexAtY( double y )
  {
    // get index at y-coordinate on canvas
    int row = (int) ( ( y + m_scrollbar.getValue() ) / m_rowHeight ) - 1;
    return Utils.clamp( row, -1, m_undostack.getSize() - 1 );
  }
}
