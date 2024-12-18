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

package pt.webdetails.cpf.bean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BeanFactoryTest {

  /*
   * We tend to use BeanFactory class to load spring xml files that are
   * in the classloader's base path.
   *
   * But in unit testing, things differ: the test class
   * is in a /test-src dir, but it gets copied into /bin/test/classes
   *
   * So we count from that base dir onwards
   */

  private final String SPRING_XML_FILE = "test.spring.xml";

  IDummyBean dummyBean;
  IBeanFactory factory;

  @Before
  public void setUp() {

    factory = new AbstractBeanFactory() {
      @Override public String getSpringXMLFilename() {
        return SPRING_XML_FILE;
      }
    };
  }

  @Test
  public void testSpringXmlFileFound() {
    assertNotNull( factory );
    assertFalse( factory.containsBean( "IBogusBean" ) );
    assertTrue( factory.containsBean( IDummyBean.class.getSimpleName() ) );
  }

  @Test
  public void testBeanLoadingOK() {

    assertNotNull( factory );

    dummyBean = (IDummyBean) factory.getBean( IDummyBean.class.getSimpleName() );

    assertNotNull( dummyBean );
    assertTrue( dummyBean.isBeanOK() );
  }

  @After
  public void tearDown() {
    factory = null;
    dummyBean = null;
  }
}
