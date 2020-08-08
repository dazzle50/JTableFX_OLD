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

import java.time.Month;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import rjc.table.Utils;
import rjc.table.data.Date;

/*************************************************************************************************/
/**************************** Pop-up window to support selecting date ****************************/
/*************************************************************************************************/

public class DateTimeDropDown extends DropDown
{
  private DateField        m_parent;
  private MonthSpinField   m_month;
  private NumberSpinField  m_year;
  private Button           m_today;
  private CalendarWidget   m_widget;

  private static final int BORDER = 4;

  /**************************************** constructor ******************************************/
  public DateTimeDropDown( DateField parent )
  {
    // create pop-up window to allow easy date selection
    super( parent );

    // create the widgets
    m_month = new MonthSpinField();
    m_year = new NumberSpinField();
    m_widget = new CalendarWidget();
    m_today = new Button( "Today" );

    // layout the widgets
    GridPane grid = new GridPane();
    grid.setHgap( BORDER );
    grid.setVgap( BORDER );
    grid.setPadding( new Insets( BORDER ) );
    grid.addRow( 0, m_month, m_year );
    grid.add( m_widget, 0, 1, 2, 1 );
    grid.add( m_today, 0, 2, 2, 1 );
    getContent().add( grid );

    // configure the widgets
    int w = (int) ( m_widget.getWidth() * 0.6 );
    m_month.setMaxWidth( w );
    m_year.setMaxWidth( m_widget.getWidth() - BORDER - w );
    m_year.setRange( 0, 5000 );
    m_year.setValue( 2000 );
    m_today.setPrefWidth( m_widget.getWidth() );

    // when grid size changes ensure background size matches
    grid.widthProperty().addListener( ( x ) -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );
    grid.heightProperty().addListener( ( x ) -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );

    // listen to widget changes
    m_year.addListener( year ->
    {
      Date date = m_widget.getDate();
      setDate( new Date( ( (Double) year[0] ).intValue(), date.getMonth(), date.getDayOfMonth() ) );
    } );
    m_month.addListener( month ->
    {
      Date date = m_widget.getDate();
      Utils.trace( date.getYear(), month, ( (Month) month[0] ).getValue(), date.getDayOfMonth() );
      setDate( new Date( date.getYear(), ( (Month) month[0] ).getValue(), date.getDayOfMonth() ) );
    } );
    m_widget.addListener( date -> setDate( (Date) date[0] ) );
  }

  /****************************************** setDate ********************************************/
  public void setDate( Date date )
  {
    // TODO ...............
    m_month.setValue( date.getMonth() );
    m_year.setValue( date.getYear() );
    m_widget.setDate( date );

    Utils.trace( date );
  }
}
