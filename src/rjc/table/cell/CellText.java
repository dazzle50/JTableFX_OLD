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

package rjc.table.cell;

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/*************************************************************************************************/
/****************************** Fit cell text into cell dimensions *******************************/
/*************************************************************************************************/

public class CellText
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
  private Bounds          bounds   = null;
  private int             lineHeight;

  static final String     ELLIPSIS = "...";                // ellipsis to show text has been truncated

  /**************************************** constructor ******************************************/
  public CellText( String cellText, Font font, Insets insets, Pos alignment, double width, double height )
  {
    // prepare Text node for measuring string boundaries
    node.setFont( font );
    width = width - insets.getLeft() - insets.getRight();
    height = height - insets.getTop() - insets.getBottom();

    // determine how text needs to be split into lines
    while ( cellText != null )
    {
      node.setText( cellText );
      bounds = node.getBoundsInLocal();
      lineHeight = (int) ( bounds.getHeight() + 0.5 );

      // if text fits width, add to lines and exit loop
      if ( bounds.getWidth() <= width )
      {
        // text fits in width
        Line line = new Line();
        line.txt = cellText;
        line.w = bounds.getWidth();
        m_lines.add( line );
        break;
      }

      // if last line, truncate this line
      if ( lineHeight * ( 2 + m_lines.size() ) > height )
      {
        truncateEllipsis( cellText, width );
        break;
      }

      // not last line, so truncate at space if can
      cellText = truncateLine( cellText, width );
    }

    // position the lines depending on the cell text alignment
    if ( bounds != null )
    {
      int numberOfLines = m_lines.size();
      for ( int index = 0; index < numberOfLines; index++ )
      {
        Line line = m_lines.get( index );

        // horizontal left
        if ( alignment.getHpos() == HPos.LEFT )
          line.x = insets.getLeft();

        // horizontal centre
        if ( alignment.getHpos() == HPos.CENTER )
          line.x = insets.getLeft() + ( width - line.w ) / 2.0;

        // horizontal right
        if ( alignment.getHpos() == HPos.RIGHT )
          line.x = insets.getLeft() + width - line.w;

        // vertical top
        if ( alignment.getVpos() == VPos.TOP )
          line.y = insets.getTop() + index * lineHeight - bounds.getMinY() - bounds.getMaxY() - 1.0;

        // vertical centre
        if ( alignment.getVpos() == VPos.CENTER )
          line.y = insets.getTop() + index * lineHeight + ( height - numberOfLines * lineHeight ) / 2.0
              - bounds.getMinY();

        // vertical bottom
        if ( alignment.getVpos() == VPos.BOTTOM || alignment.getVpos() == VPos.BASELINE )
          line.y = insets.getTop() + index * lineHeight + height - numberOfLines * lineHeight - bounds.getMinY();
      }
    }
  }

  /**************************************** truncateLine *****************************************/
  private String truncateLine( String cellText, double maxWidth )
  {
    // need to shorten text to fit maximum width (but preferably breaking at white space)
    int currentLen = cellText.length();
    int testLen = (int) ( currentLen * maxWidth / bounds.getWidth() );
    double testWidth = getWidth( cellText, testLen );

    // determine max length that can be shown breaking at white space if possible
    if ( testWidth < maxWidth )
    {
      // test width is smaller than max, so try incrementing length until too wide
      do
      {
        testWidth = getWidth( cellText, ++testLen );
      }
      while ( testWidth <= maxWidth );
      --testLen;
    }
    else
    {
      // test width is larger than max, so try decrementing length until not too wide
      do
      {
        testWidth = getWidth( cellText, --testLen );
      }
      while ( testWidth > maxWidth );
    }

    // if space not found before test length show as much of word as possible
    int space = cellText.lastIndexOf( ' ', testLen + 1 );
    if ( space == -1 )
    {
      truncateEllipsis( cellText, maxWidth );
      space = cellText.indexOf( ' ', testLen );
      if ( space == -1 )
        return null;
      return cellText.substring( space + 1 );
    }

    // space found so break there instead
    Line line = new Line();
    line.w = getWidth( cellText, space );
    line.txt = node.getText();
    m_lines.add( line );
    return cellText.substring( space + 1 );
  }

  /****************************************** getWidth *******************************************/
  private double getWidth( String text, int length )
  {
    // return bounds width of substring of text
    node.setText( text.substring( 0, length ) );
    return node.getBoundsInLocal().getWidth();
  }

  /************************************** truncateEllipsis ***************************************/
  private void truncateEllipsis( String cellText, double maxWidth )
  {
    // if no usable width, don't add any line
    if ( maxWidth < 1.0 )
      return;

    // need to shorten text to fit maximum width (but still showing as much as possible)
    int currentLen = cellText.length();
    int testLen = (int) ( currentLen * maxWidth / bounds.getWidth() );
    double testWidth = getWidthEllipsis( cellText, testLen );

    // determine max length that can be shown with ellipsis
    if ( testWidth < maxWidth )
    {
      // test width is smaller than max, so try incrementing length until too wide
      double okayWidth;
      do
      {
        okayWidth = testWidth;
        testWidth = getWidthEllipsis( cellText, ++testLen );
      }
      while ( testWidth <= maxWidth );

      Line line = new Line();
      line.txt = cellText.substring( 0, --testLen ) + ELLIPSIS;
      line.w = okayWidth;
      m_lines.add( line );
    }
    else
    {
      // test width is larger than max, so try decrementing length until not too wide
      do
      {
        testWidth = getWidthEllipsis( cellText, --testLen );
      }
      while ( testWidth > maxWidth );

      if ( testWidth < 0.0 )
      {
        node.setText( ELLIPSIS );
        testWidth = node.getBoundsInLocal().getWidth();
      }

      Line line = new Line();
      line.txt = node.getText();
      line.w = testWidth;
      m_lines.add( line );
    }
  }

  /************************************** getWidthEllipsis ***************************************/
  private double getWidthEllipsis( String text, int length )
  {
    // return bounds width of substring of text with ellipsis added
    if ( length <= 0 )
      return -1.0;

    node.setText( text.substring( 0, length ) + ELLIPSIS );
    return node.getBoundsInLocal().getWidth();
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
