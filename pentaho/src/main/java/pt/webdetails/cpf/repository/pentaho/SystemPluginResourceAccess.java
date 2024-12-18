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

package pt.webdetails.cpf.repository.pentaho;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.plugin.services.pluginmgr.PluginClassLoader;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.impl.FileBasedResourceAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;

/**
 * Implementation of {@link IPluginResourceAccess} for directories under system.<br>
 */
public class SystemPluginResourceAccess extends FileBasedResourceAccess implements IRWAccess {

  protected File basePath;

  public SystemPluginResourceAccess( ClassLoader classLoader, String basePath ) {
    initPathFromClassLoader( classLoader, basePath );
  }

  public SystemPluginResourceAccess( String pluginId, String basePath ) {
    IPluginManager pm = PentahoSystem.get( IPluginManager.class );
    ClassLoader classLoader = pm.getClassLoader( pluginId );
    initPathFromClassLoader( classLoader, basePath );
  }

  /**
   * @param classLoader Current plugin's ClassLoader
   * @param basePath    All paths will be considered relative to this (optional)
   */
  private void initPathFromClassLoader( ClassLoader classLoader, String basePath ) {
    if ( classLoader == null ) {
      throw new IllegalArgumentException( "Unknown plugin" );
    }
    if ( classLoader instanceof PluginClassLoader ) {
      this.basePath = ( (PluginClassLoader) classLoader ).getPluginDir();
    } else { //shouldn't get here, but..
      URL rootFileUrl = RepositoryHelper.getClosestResource( classLoader, "plugin.xml" );
      if ( rootFileUrl != null ) {
        this.basePath = new File( rootFileUrl.getPath() ).getParentFile();
      }
    }
    if ( this.basePath == null ) {
      throw new IllegalArgumentException( "Couldn't find a valid base path from class loader" );
    }

    if ( !StringUtils.isEmpty( basePath ) ) {
      this.basePath = new File( this.basePath, basePath );
    }
  }

  @Override
  protected File getFile( String path ) {
    if ( path != null && path.startsWith( "/system/" ) ) {           //XXX - review ...
      String[] sections = path.split( "/" );
      String sysPluginDir = sections[ 1 ] + "/" + sections[ 2 ];
      String baseString = FilenameUtils.separatorsToUnix( basePath.toString() );
      if ( baseString.indexOf( sysPluginDir ) != -1
          && ( baseString.lastIndexOf( sysPluginDir ) + sysPluginDir.length() == baseString.length() ) ) {
        path = path.replaceFirst( "/.*?/.*?/", "/" );
      } else if ( baseString.indexOf( sysPluginDir ) == -1 ) {
        String systemPath = StringUtils.substringBeforeLast( basePath.getAbsolutePath(), "system" );
        systemPath = systemPath + sysPluginDir;
        path = path.replaceFirst( "/.*?/.*?/", "/" );
        return new File( systemPath, path );
      }
    }
    return StringUtils.isEmpty( path ) ? basePath : new File( basePath, path );
  }

  @Override
  public boolean fileExists( String path ) {
    if ( super.fileExists( path ) ) {
      String normPath = FilenameUtils.normalize( this.getFile( path ).getAbsolutePath() );
      return normPath != null && normPath.startsWith( FilenameUtils.normalize( basePath.getAbsolutePath() ) );
    }
    return false;
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    if ( this.fileExists( path ) ) {
      return super.fetchFile( path );
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer( getClass().getSimpleName() );
    sb.append( ":" ).append( basePath );
    return sb.toString();
  }
}
