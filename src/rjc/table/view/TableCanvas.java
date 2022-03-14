/**************************************************************************
 *  Copyright (C) 2022 by Richard Crook                                   *
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

/*************************************************************************************************/
/************** Canvas showing the table headers & body cells + BLANK excess space ***************/
/*************************************************************************************************/

public class TableCanvas extends CanvasBase
{
  /**************************************** constructor ******************************************/
  public TableCanvas( TableView tableView )
  {
    super( tableView );

    // when canvas size changes draw new areas
    widthProperty().addListener( ( observable, oldW, newW ) -> widthChange( oldW.intValue(), newW.intValue() ) );
    heightProperty().addListener( ( observable, oldH, newH ) -> heightChange( oldH.intValue(), newH.intValue() ) );

    // ensure canvas visibility is same as parent table-view
    visibleProperty().bind( tableView.visibleProperty() );
  }

  /***************************************** widthChange *****************************************/
  public void widthChange( int oldW, int newW )
  {
    // only need to draw if new width is larger than old width
    if ( newW > oldW && isVisible() && oldW < m_view.getTableWidth() && getHeight() > 0.0 )
    {
      // clear background (+0.5 needed so anti-aliasing doesn't impact previous column)
      getGraphicsContext2D().clearRect( oldW + 0.5, 0.0, newW, getHeight() );

      // calculate which columns need to be redrawn
      int minColumnPos = m_view.getColumnPositionAtX( oldW );
      if ( minColumnPos <= HEADER )
        minColumnPos = m_view.getColumnPositionAtX( m_view.getHeaderWidth() );
      int maxColumnPos = m_view.getColumnPositionAtX( newW );
      redrawColumnsNow( minColumnPos, maxColumnPos );

      // check if row header needs to be redrawn
      if ( oldW < m_view.getHeaderWidth() )
        redrawColumnNow( HEADER );

      // draw table overlay
      redrawOverlayNow();
    }
  }

  /**************************************** heightChange *****************************************/
  public void heightChange( int oldH, int newH )
  {
    // only need to draw if new height is larger than old height
    if ( newH > oldH && isVisible() && oldH < m_view.getTableHeight() && getWidth() > 0.0 )
    {
      // clear background
      getGraphicsContext2D().clearRect( 0.0, oldH + 0.5, getWidth(), newH );

      // calculate which rows need to be redrawn, and redraw them
      int minRowPos = m_view.getRowPositionAtY( oldH );
      if ( minRowPos <= HEADER )
        minRowPos = m_view.getRowPositionAtY( m_view.getHeaderHeight() );
      int maxRowPos = m_view.getRowPositionAtY( newH );
      redrawRowsNow( minRowPos, maxRowPos );

      // check if column header needs to be redrawn
      if ( oldH < m_view.getHeaderHeight() )
        redrawRowNow( HEADER );

      // draw table overlay
      redrawOverlayNow();
    }
  }

  /******************************************* resize ********************************************/
  @Override
  public void resize( double width, double height )
  {
    // resize the canvas and overlay
    setWidth( width );
    setHeight( height );
    m_overlay.setWidth( width );
    m_overlay.setHeight( height );
  }

}
