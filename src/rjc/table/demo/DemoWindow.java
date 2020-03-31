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

package rjc.table.demo;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import rjc.table.Status;
import rjc.table.Utils;
import rjc.table.data.TableData;
import rjc.table.demo.edit.EditData;
import rjc.table.demo.edit.EditView;
import rjc.table.demo.large.LargeData;
import rjc.table.demo.large.LargeView;
import rjc.table.undo.UndoStack;
import rjc.table.undo.UndoStackWindow;
import rjc.table.view.TableView;

/*************************************************************************************************/
/******************************* Main window for demo application ********************************/
/*************************************************************************************************/

public class DemoWindow
{
  private static TextField m_statusBar;   // status bar at bottom of window

  private MenuBar          m_menus;       // menu bar at top of window
  private TabPane          m_tabs;        // tabs pane to show the demos
  private UndoStack        m_undostack;   // undo-stack for application
  private UndoStackWindow  m_undoWindow;  // window to interact with undo-stack

  private TableData        m_defaultTable;
  private LargeData        m_largeTable;
  private EditData         m_editTable;

  /**************************************** constructor ******************************************/
  public DemoWindow( Stage stage )
  {
    // create demo table data sources
    m_defaultTable = new TableData();
    m_largeTable = new LargeData();
    m_undostack = new UndoStack();
    m_editTable = new EditData( m_undostack );

    // create demo windows contents
    m_menus = makeMenuBar();
    m_tabs = makeTabs();
    m_statusBar = new TextField( "Started" );
    m_statusBar.setFocusTraversable( false );
    m_statusBar.setEditable( false );

    // create demo window layout
    GridPane grid = new GridPane();
    grid.add( m_menus, 0, 0 );
    grid.add( m_tabs, 0, 1 );
    grid.add( m_statusBar, 0, 2 );
    GridPane.setHgrow( m_tabs, Priority.ALWAYS );
    GridPane.setVgrow( m_tabs, Priority.ALWAYS );

    // create demo application window
    Scene scene = new Scene( grid );
    stage.setScene( scene );
    stage.setTitle( "JTableFX " + Utils.VERSION + " demo application" );

    // close demo app when main window is closed (in case other windows are open)
    stage.setOnHidden( event -> Platform.exit() );

    // TEMP placing and sizing for my convenience #############
    //stage.setX( -1100 );
    //stage.setY( 700 );
    //stage.setWidth( 1000 );
    stage.show();
  }

  /****************************************** makeTabs *******************************************/
  private TabPane makeTabs()
  {
    // create default table in tab
    TableView defaultView = new TableView( m_defaultTable );
    Tab defaultTab = new Tab();
    defaultTab.setText( "Default" );
    defaultTab.setClosable( false );
    defaultTab.setContent( defaultView );
    defaultView.visibleProperty().bind( defaultTab.selectedProperty() );

    // create large table in tab
    TableView largeView = new LargeView( m_largeTable );
    Tab largeTab = new Tab();
    largeTab.setText( "Large" );
    largeTab.setClosable( false );
    largeTab.setContent( largeView );
    largeView.visibleProperty().bind( largeTab.selectedProperty() );

    // create editable table in tab
    TableView editView = new EditView( m_editTable );
    Tab editTab = new Tab();
    editTab.setText( "Edit" );
    editTab.setClosable( false );
    editTab.setContent( editView );
    editView.visibleProperty().bind( editTab.selectedProperty() );

    // create demo tab pane, when selected tab changes request focus for the tab contents
    TabPane tabs = new TabPane();
    tabs.getSelectionModel().selectedItemProperty().addListener(
        ( observable, oldTab, newTab ) -> Platform.runLater( () -> ( newTab.getContent() ).requestFocus() ) );
    tabs.getTabs().addAll( defaultTab, largeTab, editTab );

    return tabs;
  }

  /**************************************** makeMenuBar ******************************************/
  private MenuBar makeMenuBar()
  {
    // create menu bar
    MenuBar menus = new MenuBar();
    Menu benchmarks = new Menu( "Benchmarks" );
    Menu views = new Menu( "View" );
    menus.getMenus().addAll( benchmarks, views );

    // benchmarking
    addBenchmark( benchmarks, "Null", () ->
    {
    }, 1000 );

    addBenchmark( benchmarks, "Trace Here", () -> Utils.trace( "Here" ), 100 );

    addBenchmark( benchmarks, "Redraw", () ->
    {
      var tab = m_tabs.getSelectionModel().getSelectedItem();
      TableView view = (TableView) tab.getContent();
      view.redraw();
    }, 1000 );

    // views
    CheckMenuItem undowindow = new CheckMenuItem( "Undo Stack ..." );
    undowindow.setOnAction( event -> showUndoWindow( undowindow ) );

    MenuItem newwindow = new MenuItem( "New window ..." );
    newwindow.setOnAction( event -> openNewWindow() );

    views.getItems().addAll( undowindow, newwindow );

    return menus;
  }

  /**************************************** openNewWindow ****************************************/
  private void openNewWindow()
  {
    // open new window with different views to same data
    Stage stage = new Stage();
    TabPane tabs = makeTabs();
    stage.setScene( new Scene( tabs ) );
    stage.setTitle( "New window" );
    stage.show();
  }

  /*************************************** showUndoWindow ****************************************/
  private void showUndoWindow( CheckMenuItem menuitem )
  {
    // create undo-stack window if not already created
    if ( m_undoWindow == null )
    {
      m_undoWindow = new UndoStackWindow( m_undostack );
      m_undoWindow.setOnHiding( event -> menuitem.setSelected( false ) );
    }

    // make the undo-stack window visible or hidden
    if ( m_undoWindow.isShowing() )
      m_undoWindow.hide();
    else
    {
      m_undoWindow.show();
      m_undoWindow.toFront();
    }
  }

  /**************************************** addBenchmark *****************************************/
  private MenuItem addBenchmark( Menu menu, String name, Runnable test, int count )
  {
    // add benchmark test to menu
    MenuItem benchmark = new MenuItem( "BenchMark - " + count + " " + name );
    menu.getItems().addAll( benchmark );

    benchmark.setOnAction( event ->
    {
      // run benchmark once to get over any first-run unique delays
      test.run();

      // run benchmark requested number of times
      long[] nanos = new long[count + 1];
      Utils.trace( "######### BENCHMARK START - " + name + " " + count + " times" );
      nanos[0] = System.nanoTime();
      for ( int num = 1; num <= count; num++ )
      {
        test.run();
        nanos[num] = System.nanoTime();
      }

      // report each run duration
      long min = Long.MAX_VALUE;
      long max = Long.MIN_VALUE;
      for ( int num = 0; num < count; num++ )
      {
        long nano = nanos[num + 1] - nanos[num];
        report( "Run " + ( num + 1 ) + " duration =", nano );
        if ( nano < min )
          min = nano;
        if ( nano > max )
          max = nano;
      }

      // report total & average duration
      long total = nanos[count] - nanos[0];
      report( "  Total duration =", total );
      report( "Average duration =", total / count );
      report( "Minimum duration =", min );
      report( "Maximum duration =", max );
    } );

    return benchmark;
  }

  /******************************************* report ********************************************/
  private void report( String text, long nanos )
  {
    // generate trace output with nano-seconds in human readable format
    String units = " ns";
    double div = 1.0;

    if ( nanos > 1000L )
    {
      units = " \u00B5s";
      div = 1000.0;
    }

    if ( nanos > 1000000L )
    {
      units = " ms";
      div = 1000000.0;
    }

    if ( nanos > 1000000000L )
    {
      units = " s";
      div = 1000000000.0;
    }

    Utils.trace( "BENCHMARK " + text + String.format( "%8.3f", nanos / div ) + units );
  }

  /****************************************** setStatus ******************************************/
  public static void setStatus( Status status )
  {
    // set status bar to show status
    m_statusBar.setText( status == null ? null : status.getMessage() );
    m_statusBar.setStyle( status == null ? null : status.getStyle() );
  }

}
