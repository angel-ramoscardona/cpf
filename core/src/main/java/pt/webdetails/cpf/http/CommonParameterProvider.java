/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package pt.webdetails.cpf.http;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CommonParameterProvider implements ICommonParameterProvider {

  @SuppressWarnings( "unused" )
  private Map<String, Object> params;

  public CommonParameterProvider() {
    params = new HashMap<String, Object>();
  }

  public void put( String name, Object value ) {
    params.put( name, value );
  }

  public String getStringParameter( String name, String defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (String) params.get( name );
    } else {
      return defaultValue;
    }
  }

  public long getLongParameter( String name, long defaultValue ) {
    // TODO Auto-generated method stub
    return 0;
  }

  public Date getDateParameter( String name, Date defaultValue ) {
    // TODO Auto-generated method stub
    return null;
  }

  public BigDecimal getDecimalParameter( String name, BigDecimal defaultValue ) {
    // TODO Auto-generated method stub
    return null;
  }

  public Object[] getArrayParameter( String name, Object[] defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (Object[]) params.get( name );
    } else {
      return defaultValue;
    }
  }

  public String[] getStringArrayParameter( String name, String[] defaultValue ) {
    if ( params.containsKey( name ) && params.get( name ) != null ) {
      return (String[]) params.get( name );
    } else {
      return defaultValue;
    }

  }

  public Iterator<String> getParameterNames() {
    return params.keySet().iterator();
  }

  public Object getParameter( String name ) {
    return params.get( name );
  }

  public boolean hasParameter( String name ) {
    return params.containsKey( name );
  }

  @Override
  public Map<String, Object> getParameters() {
    return params;
  }
}
