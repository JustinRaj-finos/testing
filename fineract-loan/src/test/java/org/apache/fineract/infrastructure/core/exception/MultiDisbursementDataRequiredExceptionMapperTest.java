/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.exception;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.fineract.infrastructure.core.data.ApiGlobalErrorResponse;
import org.apache.fineract.portfolio.loanaccount.exception.MultiDisbursementDataRequiredException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MultiDisbursementDataRequiredExceptionMapperTest {

    @Test
    public void testExceptionMapper() {
        MultiDisbursementDataRequiredExceptionMapper exceptionMapper = new MultiDisbursementDataRequiredExceptionMapper();
        MultiDisbursementDataRequiredException exception = new MultiDisbursementDataRequiredException("entity", "message", "args");
        Response response = exceptionMapper.toResponse(exception);
        Assertions.assertEquals(3003, exceptionMapper.errorCode());

        Assertions.assertInstanceOf(ApiGlobalErrorResponse.class, response.getEntity());
        Assertions.assertEquals(1, ((ApiGlobalErrorResponse) response.getEntity()).getErrors().size());
        Assertions.assertEquals("message", ((ApiGlobalErrorResponse) response.getEntity()).getErrors().get(0).getDefaultUserMessage());
        Assertions.assertEquals(SC_FORBIDDEN, response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
    }

}
