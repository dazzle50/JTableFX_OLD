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

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Control;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import rjc.table.view.TableView;

/*************************************************************************************************/
/****************************** Base class for a table cell editor *******************************/
/*************************************************************************************************/

public class CellEditorBase
{
  private static CellEditorBase       m_cellEditorInProgress;
  private static SimpleStringProperty m_errorMessage;

  private Control                     m_control;             // primary control that has focus
  private TableView                   m_view;                // associated table view

  /***************************************** constructor *****************************************/
  public CellEditorBase()
  {
    // initialise variables
    m_errorMessage = new SimpleStringProperty();
  }

  /********************************************* open ********************************************/
  public void open( Object value, TableView view )
  {
    // store variables
    m_cellEditorInProgress = this;
    m_view = view;
    setValue( value );
    m_control.requestFocus();
  }

  /******************************************** close ********************************************/
  public void close( boolean commit )
  {
    // clear any error message, remove control from table, and give focus back to table
    m_cellEditorInProgress = null;
    m_errorMessage.set( null );
    m_view.requestFocus();
    if ( commit )
      commit();
  }

  /******************************************** commit ********************************************/
  public void commit()
  {
    // commit value
  }

  /******************************************* getValue ******************************************/
  public Object getValue()
  {
    // get editor text
    return null;
  }

  /******************************************* setValue ******************************************/
  public void setValue( Object value )
  {
    // set editor text
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
    return m_errorMessage.get() == null;
  }

  /***************************************** isValueValid ****************************************/
  public boolean isValueValid( Object value )
  {
    // return if value is valid for starting cell editor
    return true;
  }

  /***************************************** setControl ******************************************/
  public void setControl( Control control )
  {
    // set focus control
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
    // return focus control
    return m_control;
  }
}