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

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import rjc.table.support.Utils;

/*************************************************************************************************/
/****************************** Fit cell text into cell dimensions *******************************/
/*************************************************************************************************/

public class TextLines
{
  // structure that contains one line of text to be drawn in cell
  private class Line
  {
    public String txt;
    public double x;
    public double y;
    public double w;

    @Override
    public String toString()
    {
      return "TextLine " + txt + " " + x + " " + y + " " + w;
    }
  }

  private ArrayList<Line> m_lines  = new ArrayList<Line>();
  private Text            node     = new Text();

  static final String     ELLIPSIS = "...";                // ellipsis to show text has been truncated

  /**************************************** constructor ******************************************/
  public TextLines( String cellText, TableView m_view, double w, double h )
  {
    // prepare Text node for measuring string boundaries
    Font font = m_view.getCellTextFont();
    node.setFont( font );
    m_view.gc.setFont( font );

    Insets insets = m_view.getCellTextInsets();
    w = w - insets.getLeft() - insets.getRight();
    h = h - insets.getTop() - insets.getBottom();
    Bounds bounds = null;

    // determine how text needs to be split into lines
    while ( cellText != null )
    {
      node.setText( cellText );
      bounds = node.getBoundsInLocal();

      // if text fits width, add to lines and exit loop
      if ( bounds.getWidth() <= w )
      {
        // text fits in width
        Line line = new Line();
        line.txt = cellText;
        line.w = bounds.getWidth();
        m_lines.add( line );
        break;
      }

      // if last line, truncate this line
      if ( bounds.getHeight() * 2.0 > h )
      {
        truncateLastLine( cellText, w );
        break;
      }

      // not last line, so truncate at space if can
      cellText = truncateLine( cellText, w - insets.getLeft() - insets.getRight() );
    }

    // position the lines depending on the cell text alignment
    Pos alignment = m_view.getCellTextAlignment();
    int size = m_lines.size();
    int height = bounds == null ? 1 : (int) ( bounds.getHeight() + 0.5 );
    for ( int index = 0; index < size; index++ )
    {
      Line line = m_lines.get( index );

      // horizontal left
      if ( alignment.getHpos() == HPos.LEFT )
        line.x = insets.getLeft();

      // horizontal centre
      if ( alignment.getHpos() == HPos.CENTER )
        line.x = insets.getLeft() + ( w - line.w ) / 2.0;

      // horizontal right
      if ( alignment.getHpos() == HPos.RIGHT )
        line.x = insets.getLeft() + w - line.w;

      // vertical top
      if ( alignment.getVpos() == VPos.TOP )
        line.y = insets.getTop() + index * height - bounds.getMinY() - bounds.getMaxY() - 1.0;

      // vertical centre
      if ( alignment.getVpos() == VPos.CENTER )
        line.y = insets.getTop() + index * height + ( h - size * height ) / 2.0 - bounds.getMinY();

      // vertical bottom
      if ( alignment.getVpos() == VPos.BOTTOM || alignment.getVpos() == VPos.BASELINE )
        line.y = insets.getTop() + index * height + h - size * height - bounds.getMinY();
    }
  }

  /**************************************** truncateLine *****************************************/
  private String truncateLine( String cellText, double w )
  {
    // TODO Auto-generated method stub
    return null;
  }

  /************************************** truncateLastLine ***************************************/
  private void truncateLastLine( String cellText, double w )
  {
    // need to shorten text to fit width
    Bounds bounds = node.getBoundsInLocal();
    int currentLen = cellText.length();
    int testLen = (int) ( currentLen * w / bounds.getWidth() );
    String test = cellText.substring( 0, testLen ) + ELLIPSIS;
    Utils.trace( "#######################################" );
    Utils.trace( bounds );
    node.setText( test );
    Utils.trace( bounds );
    Utils.trace( node.getBoundsInLocal() );

  }

  /****************************************** getText ********************************************/
  public String getText( int line )
  {
    // return text for requested line if exists, otherwise return null
    if ( line < m_lines.size() )
      return m_lines.get( line ).txt;
    else
      return null;
  }

  /******************************************** getX *********************************************/
  public double getX( int line )
  {
    // return delta-x for requested line
    return m_lines.get( line ).x;
  }

  /******************************************** getY *********************************************/
  public double getY( int line )
  {
    // return delta-y for requested line
    return m_lines.get( line ).y;
  }

}
