/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.eidinterface.web.controller;

import liquibase.csv.opencsv.CSVWriter;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.patient.impl.LuhnIdentifierValidator;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

@Controller
public class PatientStatusController {

	Integer CCC_NUMBER_PATIENT_IDENTIFIER_ID = 28;

	@ResponseBody
	@RequestMapping(value = "module/eidinterface/getPatientStatus", method = RequestMethod.POST)
	public String getPatientStatus(HttpServletResponse response, @RequestBody String identifiers) throws IOException {

		StringWriter sw = new StringWriter();
		CSVWriter csv = new CSVWriter(sw);

		String[] header = {"Identifier", "Status", "CCC Number"};
		csv.writeNext(header);

		StringReader sr = new StringReader(identifiers);
		Scanner s = new Scanner(sr);

		// iterate through identifiers
		while (s.hasNext()) {

			String identifier = s.next().trim();

			String status;
			String ccc = "";

			String[] parts = identifier.split("-");
			String validIdentifier = new LuhnIdentifierValidator().getValidIdentifier(parts[0]);

			if (!OpenmrsUtil.nullSafeEquals(identifier, validIdentifier)) {
				status = "INVALID IDENTIFIER";
			} else {
				List<Patient> patients = Context.getPatientService().getPatients(null, identifier, null, true);
				if (patients != null && patients.size() == 1) {
					Patient p = patients.get(0);
					PatientIdentifier pi = p.getPatientIdentifier(CCC_NUMBER_PATIENT_IDENTIFIER_ID);
					if (pi != null) {
						status = "ENROLLED";
						ccc = pi.getIdentifier();
					} else {
						status = "NOT ENROLLED";
					}
				} else if (patients != null && patients.size() > 1) {
					status = "MULTIPLE FOUND";
				} else {
					status = "NOT FOUND";
				}
			}

			csv.writeNext(new String[]{identifier, status, ccc});
		}

		// flush the string writer
		sw.flush();

		// set the information
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		// respond with it
		return sw.toString();
	}
}
