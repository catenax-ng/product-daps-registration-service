/********************************************************************************
 * Copyright (c) 2021,2022 T-Systems International GmbH
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.dapsreg.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.dapsreg.service.DapsClient;
import org.eclipse.tractusx.dapsreg.service.DapsManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("api/v1/daps")
@RequiredArgsConstructor
public class DapsController {

    private final DapsClient dapsClient;
    private final ObjectMapper mapper;
    private final DapsManager dapsManager;

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<JsonNode> handleIOException(WebClientResponseException ex) {
        return ResponseEntity.status(ex.getRawStatusCode()).body(
              mapper.createObjectNode()
                      .put("timestamp", Instant.now().toString())
                      .put("status", ex.getRawStatusCode())
                      .put("error", ex.getMessage())
        );
    }

    //C reate
    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole(@securityRoles.createRole)")
    public void createClient(@RequestParam String clientName, @RequestParam(required = false) String securityProfile,
                             @RequestParam(required = false) String referringConnector, @RequestPart("file") MultipartFile file) throws CertificateException, IOException {
        dapsManager.createClient(clientName, securityProfile, referringConnector, file);
    }

    //R etrieve
    @GetMapping(value = "/{client_id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @PreAuthorize("isAuthenticated()")
    public JsonNode getClient(@PathVariable String client_id) {
        return dapsClient.getClient(client_id);
    }

    //U pdate
    @PutMapping("/{client_id}")
    @PreAuthorize("hasRole(@securityRoles.updateRole)")
    public void addAttribute(@PathVariable String client_id, @RequestParam Map<String, String> newAttr) {
        dapsManager.addAttribute(client_id, newAttr);
    }

    //D elete
    @DeleteMapping("/{client_id}")
    @PreAuthorize("hasRole(@securityRoles.deleteRole)")
    public void deleteClient(@PathVariable String client_id) {
        dapsManager.deleteClient(client_id);
    }

}