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

package org.pentaho.ctools.cpf.repository.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;

/**
 * Class used for forwarding {@code IReadAccess} operations to the appropriate {@code ReadAccess} service instance that
 * contains the resources being accessed.
 *
 * @see IReadAccess
 */
public final class ReadAccessProxy implements IReadAccess {
  private List<IReadAccess> readAccesses;
  private final String basePath;
  private final String DEFAULT_PATH_SEPARATOR = "/";

  public ReadAccessProxy( List<IReadAccess> readAccesses, String basePath ) {
    this.readAccesses = readAccesses;
    this.basePath = basePath == null ? "" : basePath;
  }

  @Override
  public InputStream getFileInputStream( String path ) throws IOException {
    final String fullPath = buildPath( path );
    final IReadAccess readAccess = getReadAccess( fullPath );

    if ( readAccess == null ) {
      return null;
    }

    return readAccess.getFileInputStream( fullPath );
  }

  @Override
  public boolean fileExists( String path ) {
    final String fullPath = buildPath( path );
    return getReadAccess( fullPath ) != null;
  }

  @Override
  public long getLastModified( String path ) {
    final String fullPath = buildPath( path );
    final IReadAccess readAccess = getReadAccess( fullPath );
    return readAccess.getLastModified( fullPath );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( path, filter, -1, true, true );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( path,  filter, maxDepth, true, true );
  }

  @Override
  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( path, filter, maxDepth, includeDirs, true );
  }

  @Override
  public List<IBasicFile> listFiles(
      String path,
      IBasicFileFilter filter,
      int maxDepth,
      boolean includeDirs,
      boolean showHiddenFilesAndFolders ) {

    final String fullPath = buildPath( path );
    final IReadAccess readAccess = getReadAccess( fullPath );

    if ( readAccess == null ) {
      return Collections.emptyList();
    }

    return readAccess.listFiles( fullPath, filter, maxDepth, includeDirs, showHiddenFilesAndFolders );
  }

  @Override
  public IBasicFile fetchFile( String path ) {
    final String fullPath = buildPath( path );
    final IReadAccess readAccess = getReadAccess( fullPath );

    if ( readAccess == null ) {
      return null;
    }

    return readAccess.fetchFile( fullPath );
  }

  private IReadAccess getReadAccess( String path ) {

    // get the first that contains the resource at path
    Optional<IReadAccess> readAccessOpt = this.readAccesses.stream()
      .filter( readA -> readA.fileExists( path ) )
      .findFirst();

    return readAccessOpt.orElse( null );

  }

  private String buildPath( String path ) {
    if ( path == null ) {
      return this.basePath;
    }

    String fullPath = this.basePath;
    if ( fullPath.endsWith( DEFAULT_PATH_SEPARATOR ) && path.startsWith( DEFAULT_PATH_SEPARATOR ) ) {
      fullPath += path.substring( 1 );
    } else {
      fullPath += path;
    }

    return fullPath;
  }
}
