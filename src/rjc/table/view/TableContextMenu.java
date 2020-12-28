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

package rjc.table.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import rjc.table.Utils;
import rjc.table.view.icons.Icons;

/*************************************************************************************************/
/************************* Context menu with menu items for table views **************************/
/*************************************************************************************************/

public class TableContextMenu extends ContextMenu
{
  private static TableContextMenu m_menu;   // any previous shown context-menu

  private TableView               m_view;   // table view
  private int                     m_colPos; // column position
  private int                     m_rowPos; // row position

  /**************************************** constructor ******************************************/
  public TableContextMenu( TableView view, double x, double y )
  {
    // check input parameters
    if ( view == null )
      throw new NullPointerException( view + " " + x + " " + y );

    // check no other context-menus showing
    if ( m_menu != null && m_menu.isShowing() )
      m_menu.hide();

    // build context-menu depending on requested position
    if ( x < view.getRowHeaderWidth() && y < view.getColumnHeaderHeight() )
      buildCornerMenu();
    else if ( x > view.getTableWidth() || y > view.getTableHeight() )
      buildOffTableMenu();
    else if ( y < view.getColumnHeaderHeight() )
      buildColumnMenu();
    else if ( x < view.getRowHeaderWidth() )
      buildRowMenu();
    else
      buildBodyMenu();

    // show menu if has items
    if ( !getItems().isEmpty() )
    {
      m_menu = this;
      m_view = view;
      m_colPos = view.getColumnPositionAtX( (int) x );
      m_rowPos = view.getRowPositionAtY( (int) y );
      Point2D point = view.localToScreen( x, y );
      show( view.getCanvas(), point.getX(), point.getY() );
    }
  }

  /************************************** buildCornerMenu ****************************************/
  private void buildCornerMenu()
  {
    // build header corner menu
  }

  /************************************** buildColumnMenu ****************************************/
  private void buildColumnMenu()
  {
    // build column header menu
    addCut();
    addCopy().setDisable( false );
    addPaste();
    addInsert();
    addDelete();
    addSeparator();
    addHide();
    addUnhide();
    addClear();
  }

  /*************************************** buildRowMenu ******************************************/
  private void buildRowMenu()
  {
    // build row header menu
  }

  /*************************************** buildBodyMenu *****************************************/
  private void buildBodyMenu()
  {
    // build table body menu
    addCopy().setDisable( false );
  }

  /************************************* buildOffTableMenu ***************************************/
  private void buildOffTableMenu()
  {
    // build off table menu
  }

  /******************************************* addItem *******************************************/
  private MenuItem addItem( String text, Image icon, EventHandler<ActionEvent> action )
  {
    // build menu item, and add to menu
    MenuItem item = new MenuItem( text );
    item.setDisable( true );
    item.setOnAction( action );

    if ( icon != null )
    {
      Node node = new ImageView( icon );
      item.setGraphic( node );
    }

    getItems().add( item );
    return item;
  }

  /***************************************** addSeparator ****************************************/
  private void addSeparator()
  {
    // add separator menu item
    getItems().add( new SeparatorMenuItem() );
  }

  /******************************************** addCut *******************************************/
  private MenuItem addCut()
  {
    // add 'cut' menu item
    return addItem( "Cut", Icons.CONTENT_CUT, event -> Utils.trace( "Cut - TODO not yet implemented" ) );
  }

  /******************************************* addCopy *******************************************/
  private MenuItem addCopy()
  {
    // add 'copy' menu item
    return addItem( "Copy", Icons.CONTENT_COPY, event -> new ContentCopy( m_view, m_colPos, m_rowPos ) );
  }

  /******************************************* addPaste ******************************************/
  private MenuItem addPaste()
  {
    // add 'paste' menu item
    return addItem( "Paste", Icons.CONTENT_PASTE, event -> Utils.trace( "Paste - TODO not yet implemented" ) );
  }

  /****************************************** addInsert ******************************************/
  private MenuItem addInsert()
  {
    // add 'insert' menu item
    return addItem( "Insert", null, event -> Utils.trace( "Insert - TODO not yet implemented" ) );
  }

  /****************************************** addDelete ******************************************/
  private MenuItem addDelete()
  {
    // add 'delete' menu item
    return addItem( "Delete", Icons.DELETE, event -> Utils.trace( "Delete - TODO not yet implemented" ) );
  }

  /******************************************* addHide *******************************************/
  private MenuItem addHide()
  {
    // add 'hide' menu item
    return addItem( "Hide", null, event -> Utils.trace( "Hide - TODO not yet implemented" ) );
  }

  /****************************************** addUnhide ******************************************/
  private MenuItem addUnhide()
  {
    // add 'unhide' menu item
    return addItem( "Unhide", null, event -> Utils.trace( "Unhide - TODO not yet implemented" ) );
  }

  /******************************************* addClear ******************************************/
  private MenuItem addClear()
  {
    // add 'clear' menu item
    return addItem( "Clear", Icons.CONTENT_CLEAR, event -> Utils.trace( "Clear - TODO not yet implemented" ) );
  }

}
