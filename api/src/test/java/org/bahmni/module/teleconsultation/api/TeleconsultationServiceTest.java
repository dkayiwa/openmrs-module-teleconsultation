package org.bahmni.module.teleconsultation.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.APIAuthenticationException;
import org.openmrs.api.context.Context;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


@org.springframework.test.context.ContextConfiguration(locations = {
		"classpath:TestingApplicationContext.xml" }, inheritLocations = true)
public class TeleconsultationServiceTest extends BaseModuleWebContextSensitiveTest {

	@Autowired
	TeleconsultationService teleconsultationService;

	@BeforeEach
	public void init() throws SQLException {
		executeDataSet("userRolesAndPrivileges.xml");
		// RolePrivilegeCache loads roles on a daemon thread with its own session, which can't see this
		// test's uncommitted rows, so commit them (tearDown deletes them again).
		getConnection().commit();
	}

	@AfterEach
	public void tearDown() {
		deleteAllData();
	}

	@Test
	public void shouldPassCreateTeleconsultationLinkIfTheUserHasCreateTeleconsultationPrivileges() {
		Context.authenticate("userWithPrivilege", "P@ssw0rd");
		UUID uuid = UUID.randomUUID();
		String link = teleconsultationService.generateTeleconsultationLink(uuid.toString());
		assertNotNull(link, "https://meet.jit.si/" + uuid);
	}

	@Test
	public void shouldThrowAuthenticationExceptionIfUserDoesNotHaveSufficientPrivileges() {
		Context.authenticate("userWithoutPrivilege", "P@ssw0rd");
		TeleconsultationService service = Context.getService(TeleconsultationService.class);
		assertThrows(APIAuthenticationException.class, () -> service.generateTeleconsultationLink("uuid"));
	}
}
