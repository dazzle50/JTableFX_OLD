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
import java.time.YearMonth;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import rjc.table.data.Date;
import rjc.table.data.DateTime;
import rjc.table.data.Time;

/*************************************************************************************************/
/**************************** Pop-up window to support selecting date ****************************/
/*************************************************************************************************/

public class DateTimeDropDown extends DropDown
{
  private DateField        m_date;     // date field (or null if not)
  private TimeField        m_time;     // time field (or null if not)
  private DateTimeField    m_datetime; // date-time filed (or null if not)

  private MonthSpinField   m_month;
  private NumberSpinField  m_year;
  private CalendarWidget   m_calendar;
  private Button           m_today;

  private static final int BORDER = 4;

  /**************************************** constructor ******************************************/
  public DateTimeDropDown( XTextField parent )
  {
    // create pop-up window and check parent is supported
    super( parent );
    if ( parent instanceof DateField )
      m_date = (DateField) parent;
    else if ( parent instanceof DateTimeField )
      m_datetime = (DateTimeField) parent;
    else if ( parent instanceof TimeField )
      m_time = (TimeField) parent;
    else
      throw new IllegalArgumentException( "Parent must be DateField, TimeField or DateTimeField " + parent.getClass() );

    // create the widgets
    m_month = new MonthSpinField();
    m_year = new NumberSpinField();
    m_calendar = new CalendarWidget();
    m_today = new Button( "Today" );

    // layout the widgets
    GridPane grid = new GridPane();
    grid.setHgap( BORDER );
    grid.setVgap( BORDER );
    grid.setPadding( new Insets( BORDER ) );
    grid.addRow( 0, m_month, m_year );
    grid.add( m_calendar, 0, 1, 2, 1 );
    grid.add( m_today, 0, 2, 2, 1 );
    if ( m_datetime != null )
      grid.add( new TimeWidget( m_calendar.getWidth() ), 0, 3, 2, 1 );
    getContent().add( grid );

    // configure the widgets
    int w = (int) ( m_calendar.getWidth() * 0.6 );
    m_month.setMaxWidth( w );
    m_month.setWrapField( m_year );
    m_year.setMaxWidth( m_calendar.getWidth() - BORDER - w );
    m_year.setRange( 0, 5000 );
    m_year.setValue( 2000 );
    m_today.setPrefWidth( m_calendar.getWidth() );
    m_calendar.requestFocus();
    m_calendar.setDate( Date.MIN_VALUE );

    // when grid size changes ensure background size matches
    grid.widthProperty().addListener( x -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );
    grid.heightProperty().addListener( x -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );

    // listen to field changes
    if ( m_date != null )
      m_date.addListener( date -> setDate( (Date) date[0] ) );
    if ( m_datetime != null )
      m_datetime.addListener( datetime -> setDateTime( (DateTime) datetime[0] ) );

    // listen to widget changes
    m_year.addListener( year -> setYear( ( (Double) year[0] ).intValue() ) );
    m_month.addListener( month -> setMonth( (Month) month[0] ) );
    m_calendar.addListener( date -> setDate( (Date) date[0] ) );
    m_today.setOnAction( event -> setDate( Date.now() ) );

    // listen to mouse scroll wheel
    grid.setOnScroll( event -> parent.mouseScroll( event ) );
  }

  /****************************************** setDate ********************************************/
  private void setDate( Date date )
  {
    // set widgets to date
    m_month.setValue( date.getMonth() );
    m_year.setValue( date.getYear() );
    m_calendar.setDate( date );

    if ( m_date != null )
      m_date.setDate( date );
    //if ( m_datetime != null )
    //  m_date.setDateTime( date );
  }

  /****************************************** setTime ********************************************/
  private void setTime( Time time )
  {
    // set widgets to time TODO

    //if ( m_time != null )
    //  m_time.setTime( time );
  }

  /**************************************** setDateTime ******************************************/
  private void setDateTime( DateTime datetime )
  {
    // set widgets to date-time
    setDate( datetime.getDate() );
    setTime( datetime.getTime() );
  }

  /****************************************** setMonth *******************************************/
  private void setMonth( Month month )
  {
    // change date month
    Date date = m_calendar.getDate();
    int year = date.getYear();
    int day = date.getDayOfMonth();
    YearMonth ym = YearMonth.of( year, month );
    if ( day > ym.lengthOfMonth() )
      day = ym.lengthOfMonth();
    setDate( new Date( year, month.getValue(), day ) );
  }

  /******************************************* setYear *******************************************/
  private void setYear( int year )
  {
    // change date year
    Date date = m_calendar.getDate();
    setDate( new Date( year, date.getMonth(), date.getDayOfMonth() ) );
  }

}
