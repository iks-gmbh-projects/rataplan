package de.iks.rataplan.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import de.iks.rataplan.domain.AppointmentRequest;
import de.iks.rataplan.domain.AppointmentRequestConfig;
import de.iks.rataplan.domain.AuthUser;
import de.iks.rataplan.domain.BackendUser;
import de.iks.rataplan.domain.ErrorCode;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.restservice.AuthService;
import de.iks.rataplan.service.AppointmentRequestService;
import de.iks.rataplan.service.BackendUserService;
import de.iks.rataplan.utils.CookieBuilder;

@Service
public class AuthorizationControllerService {

//	@Autowired
//	private AppointmentRequestService appointmentRequestService;

	@Autowired
	private AuthService authService;

	@Autowired
	private BackendUserService backendUserService;

	@Autowired
	private HttpServletResponse servletResponse;

	@Autowired
	private CookieBuilder cookieBuilder;

	/**
	 * Validates if authorization can be given with the following parameters
	 * Description of authorization-process can be found in Confluence: "https://www.iksblogs.de/confluence/display/RAT/Authorisierung"
	 * or in the Tests:
	 * "src/test/java/de/iks/rataplan/controller/AuthorizationControllerServiceTest"
	 *
	 * @param isEdit
	 * @param requestId
	 * @param jwtToken
	 * @param accessToken
	 * @param backendUser
	 * @return
	 */
	public AppointmentRequest getAppointmentRequestIfAuthorized(boolean isEdit, Integer requestId, String jwtToken, String accessToken, BackendUser backendUser) {

//		AppointmentRequest appointmentRequest = appointmentRequestService.getAppointmentRequestById(requestId);
//
//		if (appointmentRequest == null) {
//			throw new ResourceNotFoundException("AppointmentRequest does not exist.");
//		}
//
//		if (jwtToken != null) {
//			if (backendUser == null) {
//				backendUser = this.getBackendUserAndRefreshCookie(jwtToken);
//			}
//
//			if (backendUser.hasAccessByRequestId(requestId, isEdit)) {
//				return appointmentRequest;
//			}
//		}
//
		throw new ForbiddenException();
	}

	/**
	 * Should be called whenever authService.getUserData() is called to refresh cookie
	 *
	 * @param jwtToken
	 * @return
	 */
	public BackendUser getBackendUserAndRefreshCookie(String jwtToken) {
		ResponseEntity<AuthUser> authServiceResponse = authService.getUserData(jwtToken);

		this.refreshCookie(authServiceResponse.getHeaders().getFirst("jwttoken"));

		AuthUser authUser = authServiceResponse.getBody();
		return backendUserService.getBackendUserByAuthUserId(authUser.getId());
	}

	public void refreshCookie(String jwtToken) {
		servletResponse.addHeader("Set-Cookie", cookieBuilder.generateCookieValue(jwtToken, false));
	}
}
