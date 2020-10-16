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
  private DateField        m_dateField;     // date field (or null if not)
  private TimeField        m_timeField;     // time field (or null if not)
  private DateTimeField    m_datetimeField; // date-time filed (or null if not)

  private MonthSpinField   m_monthField;
  private NumberSpinField  m_yearField;
  private CalendarWidget   m_calendar;
  private Button           m_todayButton;

  private static final int BORDER = 4;

  /**************************************** constructor ******************************************/
  public DateTimeDropDown( XTextField parent )
  {
    // create pop-up window and check parent is supported
    super( parent );
    if ( parent instanceof DateField )
      m_dateField = (DateField) parent;
    else if ( parent instanceof DateTimeField )
      m_datetimeField = (DateTimeField) parent;
    else if ( parent instanceof TimeField )
      m_timeField = (TimeField) parent;
    else
      throw new IllegalArgumentException( "Parent must be DateField, TimeField or DateTimeField " + parent.getClass() );

    // create the widgets
    m_monthField = new MonthSpinField();
    m_yearField = new NumberSpinField();
    m_calendar = new CalendarWidget();
    m_todayButton = new Button( "Today" );

    // layout the widgets
    GridPane grid = new GridPane();
    grid.setHgap( BORDER );
    grid.setVgap( BORDER );
    grid.setPadding( new Insets( BORDER ) );
    grid.addRow( 0, m_monthField, m_yearField );
    grid.add( m_calendar, 0, 1, 2, 1 );
    grid.add( m_todayButton, 0, 2, 2, 1 );
    if ( m_datetimeField != null )
      grid.add( new TimeWidget( m_calendar ), 0, 3, 2, 1 );
    getContent().add( grid );

    // configure the widgets
    int w = (int) ( m_calendar.getWidth() * 0.6 );
    m_monthField.setMaxWidth( w );
    m_monthField.setWrapField( m_yearField );
    m_yearField.setMaxWidth( m_calendar.getWidth() - BORDER - w );
    m_yearField.setRange( 0, 5000 );
    m_todayButton.setPrefWidth( m_calendar.getWidth() );
    m_calendar.requestFocus();

    // when grid size changes ensure background size matches
    grid.widthProperty().addListener( x -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );
    grid.heightProperty().addListener( x -> setBackgroundSize( grid.getWidth(), grid.getHeight() ) );

    // listen to field changes
    if ( m_dateField != null )
      m_dateField.addListener( date -> setDate( (Date) date[0] ) );
    if ( m_datetimeField != null )
      m_datetimeField.addListener( datetime -> setDateTime( (DateTime) datetime[0] ) );

    // listen to widget changes
    m_yearField.addListener( year -> setYear( ( (Double) year[0] ).intValue() ) );
    m_monthField.addListener( month -> setMonth( (Month) month[0] ) );
    m_calendar.addListener( date -> setDate( (Date) date[0] ) );
    m_todayButton.setOnAction( event -> setDate( Date.now() ) );

    // listen to mouse scroll wheel
    grid.setOnScroll( event -> parent.mouseScroll( event ) );
  }

  /****************************************** setDate ********************************************/
  private void setDate( Date date )
  {
    // set widgets to date
    m_monthField.setValue( date.getMonth() );
    m_yearField.setValue( date.getYear() );
    m_calendar.setDate( date );

    if ( m_dateField != null )
      m_dateField.setDate( date );
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
