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

package org.pentaho.ctools.cpf.repository.utils;

import org.apache.commons.io.IOUtils;
import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IRWAccess;
import pt.webdetails.cpf.repository.util.RepositoryHelper;
import pt.webdetails.cpf.utils.CharsetHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This one is based on files. How they are fetched is anyone's guess
 */
public abstract class FileBasedResourceAccess implements IRWAccess {

  public InputStream getFileInputStream( String path ) throws IOException {
    File file = getFile( path );
    if ( file.exists() ) {
      return new FileInputStream( file );
    }
    return null;
  }

  public String getFileContents( String path ) throws IOException {
    InputStream input = null;
    try {
      input = getFileInputStream( path );
      return IOUtils.toString( input, CharsetHelper.getEncoding() );
    } finally {
      IOUtils.closeQuietly( input );
    }
  }

  public boolean fileExists( String path ) {
    return getFile( path ).exists();
  }

  public long getLastModified( String path ) {
    return getFile( path ).lastModified();
  }

  public boolean saveFile( String path, String contents ) {
    FileOutputStream out = null;
    try {
      out = new FileOutputStream( getFile( path ) );
      IOUtils.write( contents, out );
    } catch ( IOException e ) {
      return false;
    } finally {
      IOUtils.closeQuietly( out );
    }
    return true;
  }

  public boolean deleteFile( String path ) {
    return getFile( path ).delete();
  }

  public boolean copyFile( String pathFrom, String pathTo ) {
    try {
      return saveFile( pathTo, getFileContents( pathFrom ) );
    } catch ( IOException e ) {
      return false;
    }
  }

  protected abstract File getFile( String path );

  public IBasicFile fetchFile( String path ) {
    return asBasicFile( getFile( path ), path );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter ) {
    return listFiles( new ArrayList<IBasicFile>(), getFile( path ), asFileFilter( filter ), false, false, -1 );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs ) {
    return listFiles( new ArrayList<IBasicFile>(), getFile( path ), asFileFilter( filter ), includeDirs, false, -1 );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth ) {
    return listFiles( new ArrayList<IBasicFile>(), getFile( path ), asFileFilter( filter ), false, false, maxDepth );
  }

  public List<IBasicFile> listFiles( String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs,
                                     boolean showHiddenFilesAndFolders ) {
    return listFiles( new ArrayList<IBasicFile>(), getFile( path ), asFileFilter( filter ), includeDirs,
      showHiddenFilesAndFolders, maxDepth );
  }

  private List<IBasicFile> listFiles( List<IBasicFile> list, File root, FileFilter filter, boolean includeDirs,
                                      boolean showHiddenFilesAndFolders, int depth ) {

    if ( root.isDirectory() ) {
      if ( includeDirs && filter.accept( root ) ) {
        list.add( asBasicFile( root, relativizePath( root ) ) );
      }
      if ( depth != 0 ) {
        for ( File file : root.listFiles() ) {
          listFiles( list, file, filter, includeDirs, showHiddenFilesAndFolders, depth - 1 );
        }
      }
    } else if ( filter.accept( root ) ) {
      list.add( asBasicFile( root, relativizePath( root ) ) );
    }
    return list;
  }

  private String relativizePath( File file ) {
    return RepositoryHelper.relativizePath(
      getFile( null ).getAbsolutePath(),
      file.getAbsolutePath(),
      false );
  }

  private FileFilter asFileFilter( final IBasicFileFilter filter ) {
    return new FileFilter() {

      public boolean accept( File file ) {
        return filter.accept( asBasicFile( file, relativizePath( file ) ) );
      }
    };
  }

  protected IBasicFile asBasicFile( final File file, final String relPath ) {
    if ( file == null ) {
      return null;
    }

    return new IBasicFile() {

      public InputStream getContents() throws IOException {
        return new FileInputStream( file );
      }

      public String getName() {
        return file.getName();
      }

      public String getFullPath() {
        return file.getAbsolutePath(); //TODO: . ..
      }

      public String getPath() {
        return relPath;
      }

      public String getExtension() {
        return RepositoryHelper.getExtension( getName() );
      }

      public boolean isDirectory() {
        return file.isDirectory();
      }

    };
  }

  public boolean saveFile( String path, InputStream in ) {
    File file = getFile( path );
    FileOutputStream fout = null;
    try {
      // create a new file if not exists
      if ( file != null && !file.exists() ) {
        if ( file.getParentFile() != null && !file.getParentFile().exists() ) {
          file.getParentFile().mkdirs();
        }
        file.createNewFile();
      }
      fout = new FileOutputStream( file );
      IOUtils.copy( in, fout );
      return true;
    } catch ( IOException e ) {
      return false;
    } finally {
      IOUtils.closeQuietly( fout );
    }
  }

  public boolean createFolder( String path ) {
    return createFolder( path, false );
  }

  public boolean createFolder( String path, boolean isHidden ) {
    File folder = getFile( path );
    return folder.mkdirs();
  }
}
