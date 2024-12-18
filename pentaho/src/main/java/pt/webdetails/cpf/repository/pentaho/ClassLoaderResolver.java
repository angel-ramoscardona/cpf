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

import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import pt.webdetails.cpf.repository.api.IBasicFile;
import pt.webdetails.cpf.repository.api.IBasicFileFilter;
import pt.webdetails.cpf.repository.api.IReadAccess;
import pt.webdetails.cpf.repository.impl.ClassLoaderResourceAccess;

/**
 * Simple implementation without IBasicFile methods. TODO: do this straight at ClassLoaderResourceAccess? 
 */
public class ClassLoaderResolver extends ClassLoaderResourceAccess implements IReadAccess {

  public ClassLoaderResolver(Class<?> classe) {
    super(classe.getClassLoader(), null);
  }

  public IBasicFile fetchFile(String path) {
    throw new NotImplementedException();
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter) {
    throw new NotImplementedException();
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth) {
    throw new NotImplementedException();
  }

  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs) {
    throw new NotImplementedException();
  }
  
  public List<IBasicFile> listFiles(String path, IBasicFileFilter filter, int maxDepth, boolean includeDirs, boolean showHiddenFilesAndFolders) {
    throw new NotImplementedException();
  }

}
