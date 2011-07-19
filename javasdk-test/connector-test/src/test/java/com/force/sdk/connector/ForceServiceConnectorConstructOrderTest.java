/**
 * Copyright (c) 2011, salesforce.com, inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided
 * that the following conditions are met:
 *
 *    Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *    following disclaimer.
 *
 *    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and
 *    the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 *    Neither the name of salesforce.com, inc. nor the names of its contributors may be used to endorse or
 *    promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package com.force.sdk.connector;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Tests the order in which the ForceServiceConnector constructs a ConnectorConfig.
 *
 * @author Tim Kral
 */
public class ForceServiceConnectorConstructOrderTest extends BaseForceServiceConnectorTest {

    @DataProvider
    protected Object[][] connectorConstructOrderProvider() {
        return new Object[][] {
            {"testExternalConfigIsFirst", true, false},
            {"testConnNameLookupIsSecond", null, true},
        };
    }
    
    @Test(dataProvider = "connectorConstructOrderProvider")
    public void testConnectorConstructOrder(String testName, Boolean addGoodConfig, Boolean addGoodConnName) throws Exception {
        ForceConnectorConfig goodConfig = createConfig();
        ForceConnectorConfig badConfig = createBadConfig();
        
        try {
            // Set an invalid config on ThreadLocal
            ForceServiceConnector.setThreadLocalConnectorConfig(badConfig);
            
            
            ForceServiceConnector connector = new ForceServiceConnector("testConnectorConstructOrder");
            
            if (addGoodConfig != null && addGoodConfig) {
                connector.setConnectorConfig(goodConfig);
            }
            
            if (addGoodConnName != null) {
                if (addGoodConnName) {
                    System.setProperty("force.testConnectorConstructOrder.url", createConnectionUrl());
                } else {
                    System.setProperty("force.testConnectorConstructOrder.url", "test");
                }
            }
            
            // The connector can still get a connection because it is using
            // good state based on the order of how things are used for construction
            verifyConnection(connector.getConnection());
        } finally {
            ForceServiceConnector.setThreadLocalConnectorConfig(null);
            System.clearProperty("force.testConnectorConstructOrder.url");
        }
    }
    
    private ForceConnectorConfig createBadConfig() {
        ForceConnectorConfig badConfig = new ForceConnectorConfig();
        badConfig.setAuthEndpoint("BadEndPoint");
        
        return badConfig;
    }
}