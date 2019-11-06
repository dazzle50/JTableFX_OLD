/**************************************************************************
 *  Copyright (C) 2019 by Richard Crook                                   *
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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import rjc.table.Status;
import rjc.table.Status.Level;
import rjc.table.view.TableCell;
import rjc.table.view.TableView;

/*************************************************************************************************/
/****************************** Base class for a table cell editor *******************************/
/*************************************************************************************************/

public class CellEditorBase
{
  private static CellEditorBase         m_cellEditorInProgress;

  private Control                       m_control;             // primary control that has focus
  private TableCell                     m_cell;                // table cell context

  private ReadOnlyObjectWrapper<Status> m_status;              // error status of cell editor

  /***************************************** constructor *****************************************/
  public CellEditorBase()
  {
    // initialise variables
    m_status = new ReadOnlyObjectWrapper<>();
  }

  /********************************************* open ********************************************/
  public void open( Object value, TableCell cell )
  {
    // check editor is set
    if ( m_control == null )
      throw new IllegalStateException( "Editor control not set" );

    // set editor position & size
    m_cell = cell;
    m_control.setLayoutX( cell.x - 1 );
    m_control.setLayoutY( cell.y - 1 );
    m_control.setMaxSize( cell.w + 1, cell.h + 1 );
    m_control.setMinSize( cell.w + 1, cell.h + 1 );

    // if control derived from XTextField 
    TableView view = cell.view;
    if ( m_control instanceof XTextField )
    {
      // set min & max width
      XTextField field = ( (XTextField) m_control );
      double max = view.getWidth() - cell.x - 1;
      double min = view.getWidth() + 1;
      if ( min > max )
        min = max;
      field.setPadding( view.getZoomTextInsets( cell ) );
      field.setFont( view.getZoomFont( cell ) );
      field.setWidths( min, max );

      // listen to text field status
      field.getStatusProperty().addListener( ( observable, oldS, newS ) -> m_status.set( newS ) );
    }

    // if control derived from NumberSpinField add wheel scroll listener
    if ( m_control instanceof NumberSpinField )
      view.setOnScroll( event -> ( (NumberSpinField) m_control ).mouseScroll( event ) );

    // display editor, give focus, and set editor value
    m_cellEditorInProgress = this;
    view.add( m_control );
    m_control.requestFocus();
    setValue( value );
  }

  /******************************************** close ********************************************/
  public void close( boolean commit )
  {
    // clear any error message, remove control from table, and give focus back to table
    m_cellEditorInProgress = null;
    m_status.set( null );
    m_cell.view.remove( m_control );
    m_cell.view.setOnScroll( event -> m_cell.view.mouseScroll( event ) );
    m_cell.view.requestFocus();
    if ( commit )
      commit();
  }

  /******************************************** commit ********************************************/
  public void commit()
  {
    // attempt to commit editor value to data source
    m_cell.view.getData().setValue( m_cell.columnIndex, m_cell.rowIndex, getValue() );
  }

  /******************************************* getValue ******************************************/
  public Object getValue()
  {
    // get editor value - normally overloaded
    return null;
  }

  /******************************************* setValue ******************************************/
  public void setValue( Object value )
  {
    // set editor value - normally overloaded
  }

  /****************************************** endEditing *****************************************/
  public static void endEditing()
  {
    // if there is a currently active editor, close it
    if ( m_cellEditorInProgress != null )
      m_cellEditorInProgress.close( !m_cellEditorInProgress.isError() );
  }

  /******************************************* isError *******************************************/
  public Boolean isError()
  {
    // return if editor in error state
    var level = m_status.get() == null ? null : m_status.get().getSeverity();
    return level == Level.ERROR || level == Level.FATAL;
  }

  /************************************** getStatusProperty **************************************/
  public ReadOnlyObjectProperty<Status> getStatusProperty()
  {
    // return read only status property
    return m_status.getReadOnlyProperty();
  }

  /***************************************** isValueValid ****************************************/
  public boolean isValueValid( Object value )
  {
    // return if value is valid for starting cell editor
    if ( m_control instanceof XTextField )
      return value == null || ( (XTextField) m_control ).isAllowed( value.toString() );

    return true;
  }

  /***************************************** setControl ******************************************/
  public void setControl( Control control )
  {
    // set editor main control
    m_control = control;

    // add listener to end editing if focus lost
    m_control.focusedProperty().addListener( ( observable, oldFocus, newFocus ) ->
    {
      if ( !newFocus )
        endEditing();
    } );

    // add key press event handler to close if escape or enter pressed
    m_control.addEventHandler( KeyEvent.KEY_PRESSED, event ->
    {
      if ( event.getCode() == KeyCode.ESCAPE )
        close( false ); // abandon edit
      if ( event.getCode() == KeyCode.ENTER && !isError() )
        close( true ); // commit edit
    } );
  }

  /***************************************** getControl ******************************************/
  public Control getControl()
  {
    // return editor main control
    return m_control;
  }
}