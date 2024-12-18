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


package pt.webdetails.cpf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pt.webdetails.cpf.repository.api.IContentAccessFactory;
import pt.webdetails.cpf.repository.api.IReadAccess;

public abstract class AbstractCpfProperties extends Properties {

  protected static final long serialVersionUID = 1L;
  protected static final Log logger = LogFactory.getLog( CpfProperties.class );
  protected static String PROPERTIES_FILE = "config.properties";

  protected AbstractCpfProperties( IContentAccessFactory accessor ) {
    loadSettings( accessor );
  }

  protected boolean loadProperties( IReadAccess location, String fileName ) throws IOException {
    if ( location.fileExists( fileName ) ) {
      loadAndClose( location.getFileInputStream( fileName ) );
      return true;
    }
    return false;
  }

  protected void loadSettings( IContentAccessFactory accessor ) {
    try {

      // 1) a config.properties inside the jar
      // this one should always exist
      if ( !loadClassProperties( PROPERTIES_FILE ) ) {
        logger.warn( "No CPF base settings." );
      }

      // 2) a config.properties in repository:cpf/config.properties
      // factory not so good for this one
      IReadAccess inRepositoryCpf = accessor.getPluginRepositoryReader( "../cpf" );
      if ( !loadAsSystem( inRepositoryCpf ) && logger.isDebugEnabled() ) {
        logger.debug( "No global CPF settings." );
      }

      // 3) in system/<plugin>/config.properties
      IReadAccess inSystem = PluginEnvironment.repository().getPluginSystemReader( "" );
      if ( !loadProperties( inSystem, PROPERTIES_FILE ) && logger.isDebugEnabled() ) {
        logger.debug( "No plugin-specific CPF settings." );
      }

    } catch ( IOException ioe ) {
      logger.error( "Failed to read CPF settings", ioe );
    }
  }

  protected boolean loadClassProperties( String fileName ) throws IOException {
    InputStream file = getClass().getResourceAsStream( fileName );
    if ( file != null ) {
      loadAndClose( file );
      return true;
    }
    // not found
    return false;
  }

  public boolean getBooleanProperty( String property, boolean defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      return Boolean.parseBoolean( propertyValue );
    }
    return defaultValue;
  }

  public int getIntProperty( String property, int defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      try {
        return Integer.parseInt( propertyValue );
      } catch ( NumberFormatException e ) {
        logger.error( "getIntProperty: " + property + " is not a valid int value." );
      }
    }
    return defaultValue;
  }

  public long getLongProperty( String property, long defaultValue ) {
    String propertyValue = getProperty( property, null );
    if ( !StringUtils.isEmpty( propertyValue ) ) {
      try {
        return Long.parseLong( propertyValue );
      } catch ( NumberFormatException e ) {
        logger.error( "getLongProperty: " + property + " is not a valid long value." );
      }
    }
    return defaultValue;
  }

  protected void loadAndClose( InputStream input ) throws IOException {
    try {
      load( input );
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

  protected abstract boolean loadAsSystem( final IReadAccess inRepositoryCpf );

}
