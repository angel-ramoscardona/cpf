/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package pt.webdetails.cpf.plugins;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import pt.webdetails.cpf.PentahoPluginEnvironment;
import pt.webdetails.cpf.PluginEnvironment;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.api.IContentAccessFactory;

public class PluginsAnalyzer {

  private List<Plugin> installedPlugins;
  protected Log logger = LogFactory.getLog( this.getClass() );
  IPluginManager pluginManager;
  IContentAccessFactory repositoryAccess;

  public PluginsAnalyzer() {
    try {
      setFactory( PentahoPluginEnvironment.getInstance().repository() );
    } catch ( NullPointerException npe ) {
      setFactory( null );
    }
    setPluginManager( PentahoSystem.get( IPluginManager.class ) );
  }

  private void setFactory( IContentAccessFactory factory ) {
    this.repositoryAccess = factory;
  }

  private void setPluginManager( IPluginManager pluginManager ) {
    this.pluginManager = pluginManager;
  }

  public PluginsAnalyzer( IContentAccessFactory factory, IPluginManager pluginManager ) {
    this.repositoryAccess = factory;
    this.pluginManager = pluginManager;
  }

  public void refresh() {
    buildPluginsList();
  }

  public List<Plugin> getInstalledPlugins() {
    return installedPlugins;
  }

  //TODO: change name;
  public class PluginWithEntity {
    private Plugin plugin;
    private Node registeredEntity;

    public PluginWithEntity( Plugin plugin, Node registeredEntity ) {
      this.plugin = plugin;
      this.registeredEntity = registeredEntity;
    }

    public Plugin getPlugin() {
      return plugin;
    }

    public Node getRegisteredEntity() {
      return registeredEntity;
    }
  }

  ;

  public class PluginPair<T> {
    private T value;
    private Plugin plugin;

    public Plugin getPlugin() {
      return plugin;
    }

    public T getValue() {
      return value;
    }

    public PluginPair( Plugin plugin, T value ) {
      this.value = value;
      this.plugin = plugin;
    }
  }

  /**
   * @param xpath for node in plugin's settings
   * @return list of plugin+node entries
   */
  public List<PluginWithEntity> getRegisteredEntities( String xpath ) {
    List<PluginWithEntity> result = new ArrayList<PluginWithEntity>();
    for ( Plugin plugin : installedPlugins ) {
      Node registeredEntity = plugin.getRegisteredEntities( xpath );
      if ( registeredEntity != null ) {
        result.add( new PluginWithEntity( plugin, registeredEntity ) );
      }
    }
    return result;
  }

  public List<PluginPair<List<Element>>> getPluginsWithSection( String xpath ) {
    List<PluginPair<List<Element>>> pluginsWithSection = new ArrayList<PluginsAnalyzer.PluginPair<List<Element>>>();
    for ( Plugin plugin : installedPlugins ) {
      List<Element> section = plugin.getSettingsSection( xpath );
      if ( !section.isEmpty() ) {
        pluginsWithSection.add( new PluginPair<List<Element>>( plugin, section ) );
      }
    }
    return pluginsWithSection;
  }

  private void buildPluginsList() {
    List<String> registeredPluginIds = pluginManager.getRegisteredPlugins();
    installedPlugins = new ArrayList<Plugin>( registeredPluginIds.size() );
    for ( String pluginId : registeredPluginIds ) {
      IReadAccess pluginDir = repositoryAccess.getOtherPluginSystemReader( pluginId, null );
      Plugin plugin = new Plugin( pluginId, pluginDir );
      installedPlugins.add( plugin );
    }
  }

  public List<Plugin> getPlugins( IPluginFilter filter ) {
    List<Plugin> pluginsList = new ArrayList<Plugin>();

    for ( Plugin plugin : installedPlugins ) {
      if ( filter.include( plugin ) ) {
        pluginsList.add( plugin );
      }
    }

    return pluginsList;
  }

}
