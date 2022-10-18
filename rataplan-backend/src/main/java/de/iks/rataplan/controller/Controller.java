package de.iks.rataplan.controller;

import java.util.List;

import de.iks.rataplan.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.iks.rataplan.dto.AppointmentMemberDTO;
import de.iks.rataplan.dto.AppointmentRequestDTO;
import de.iks.rataplan.exceptions.ForbiddenException;
import de.iks.rataplan.exceptions.MalformedException;
import de.iks.rataplan.exceptions.ResourceNotFoundException;
import de.iks.rataplan.exceptions.ServiceNotAvailableException;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/v1")
public class Controller {

    private static final String JWT_COOKIE_NAME = "jwttoken";
    private static final String ACCESS_TOKEN = "accesstoken";

    @Autowired
    private AppointmentRequestControllerService appointmentRequestControllerService;

    @Autowired
    private AppointmentMemberControllerService appointmentMemberControllerService;

    @Autowired
    private UserControllerService userControllerService;

    @Autowired
    private GeneralControllerService generalControllerService;

    @ApiResponses({@ApiResponse(code = 200, message = "OK")})
    @RequestMapping(value = "/*", method = RequestMethod.OPTIONS, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> handle() {

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = AppointmentRequestDTO.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests/{participationToken}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentRequestDTO> getAppointmentRequestById(@PathVariable String participationToken,
                                                                           @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        AppointmentRequestDTO appointmentRequestDTO = appointmentRequestControllerService
                .getAppointmentRequestByParticipationToken(participationToken);
        return new ResponseEntity<>(appointmentRequestDTO, HttpStatus.OK);
    }

//    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
//            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
//            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
//            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
//    @RequestMapping(value = "/appointmentRequests/{requestId}/edit", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<AppointmentRequestDTO> getAppointmentRequestByIdForEdit(@PathVariable Integer requestId,
//                                                                                  @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken,
//                                                                                  @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken) {
//
//        AppointmentRequestDTO appointmentRequestDTO = appointmentRequestControllerService
//                .getAppointmentRequestById(true, requestId, jwtToken, accessToken);
//        return new ResponseEntity<>(appointmentRequestDTO, HttpStatus.OK);
//    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = AppointmentRequestDTO.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentRequestDTO> createAppointmentRequest(
            @RequestBody AppointmentRequestDTO appointmentRequestDTO,
            @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        AppointmentRequestDTO createdAppointmentRequestDTO = appointmentRequestControllerService
                .createAppointmentRequest(appointmentRequestDTO, jwtToken);
        return new ResponseEntity<>(createdAppointmentRequestDTO, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 202, message = "ACCEPTED", response = AppointmentRequestDTO.class),
            @ApiResponse(code = 400, message = "There are no Appointments in this AppointmentRequest.", response = MalformedException.class),
            @ApiResponse(code = 400, message = "AppointmentType does not fit the AppointmentRequest.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests/{requestId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentRequestDTO> updateAppointmentRequest(@PathVariable Integer requestId,
                                                                          @RequestBody AppointmentRequestDTO appointmentRequestDTO,
                                                                          @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken,
                                                                          @RequestHeader(value = ACCESS_TOKEN, required = false) String accessToken) {

        AppointmentRequestDTO updatedAppointmentRequestDTO = appointmentRequestControllerService
                .updateAppointmentRequest(appointmentRequestDTO, requestId, jwtToken, accessToken);
        return new ResponseEntity<>(updatedAppointmentRequestDTO, HttpStatus.ACCEPTED);
    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = AppointmentMemberDTO.class),
            @ApiResponse(code = 400, message = "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests/{participationToken}/appointmentMembers", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentMemberDTO> addAppointmentMember(@PathVariable String participationToken,
                                                                     @RequestBody AppointmentMemberDTO appointmentMemberDTO,
                                                                     @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        AppointmentMemberDTO addedAppointmentMemberDTO = appointmentMemberControllerService
                .createAppointmentMember(appointmentMemberDTO, participationToken, jwtToken);
        return new ResponseEntity<>(addedAppointmentMemberDTO, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 204, message = "NO_CONTENT"),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests/{participationToken}/appointmentMembers/{memberId}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteAppointmentMember(@PathVariable String participationToken, @PathVariable Integer memberId,
                                                     @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        appointmentMemberControllerService.deleteAppointmentMember(participationToken, memberId, jwtToken);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = AppointmentRequestDTO.class),
            @ApiResponse(code = 400, message = "AppointmentDecisions don't fit the DecisionType in the AppointmentRequest.", response = MalformedException.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 404, message = "AppointmentRequest not found.", response = ResourceNotFoundException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/appointmentRequests/{participationToken}/appointmentMembers/{memberId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AppointmentMemberDTO> updateAppointmentMember(@PathVariable String participationToken,
                                                                        @PathVariable Integer memberId, @RequestBody AppointmentMemberDTO appointmentMemberDTO,
                                                                        @CookieValue(value = JWT_COOKIE_NAME, required = false) String jwtToken) {

        AppointmentMemberDTO updatedAppointmentMemberDTO = appointmentMemberControllerService.updateAppointmentMember(participationToken, memberId, appointmentMemberDTO, jwtToken);
        return new ResponseEntity<>(updatedAppointmentMemberDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 201, message = "CREATED", response = FrontendUser.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/register", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FrontendUser> registerUser(@RequestBody FrontendUser frontendUser) {

        FrontendUser createdFrontendUser = userControllerService.registerUser(frontendUser);
        return new ResponseEntity<>(createdFrontendUser, HttpStatus.CREATED);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/mailExists", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkIfMailExists(@RequestBody String mail) {

        return userControllerService.checkIfMailExists(mail);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/usernameExists", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean checkIfUsernameExists(@RequestBody String username) {

        return userControllerService.checkIfUsernameExists(username);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/forgotPassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean sendForgotPasswordMail(@RequestBody String mail) {

        boolean response = userControllerService.sendForgotPasswordMail(mail);

        return response;
    }


    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = FrontendUser.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FrontendUser> loginUser(@RequestBody FrontendUser frontendUser) {

        FrontendUser fetchedFrontendUser = userControllerService.loginUser(frontendUser);
        return new ResponseEntity<>(fetchedFrontendUser, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> logoutUser() {

        userControllerService.logoutUser();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = FrontendUser.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/profile", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FrontendUser> getUserData(
            @CookieValue(value = JWT_COOKIE_NAME, required = true) String jwtToken) {

        FrontendUser frontendUser = userControllerService.getUserData(jwtToken);
        return new ResponseEntity<>(frontendUser, HttpStatus.OK);
    }


    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 400, message = "Password has not been changed.", response = MalformedException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/profile/changePassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Boolean> changePassword(@RequestBody PasswordChange passwords,
                                                  @CookieValue(value = JWT_COOKIE_NAME, required = true) String jwtToken) {

        boolean success = userControllerService.changePassword(passwords, jwtToken);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 400, message = "Email has not been changed", response = MalformedException.class),
            @ApiResponse(code = 500, message = "Internal Server Error", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "users/profile/changeEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<Boolean> changeEmail(@RequestBody String email,
                                               @CookieValue(value = JWT_COOKIE_NAME, required = true)String jwtToken) {

        System.out.println(email);
        System.out.println(jwtToken);

        boolean success = userControllerService.changeEmail(email, jwtToken);

        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
    @ApiResponse(code = 400, message = "Displayname has not been changed", response = MalformedException.class),
    @ApiResponse(code = 500, message = "Internal Server Error", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "users/profile/changeDisplayName", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<Boolean> changeDisplayName(@RequestBody String displayName,
                                                     @CookieValue(value = JWT_COOKIE_NAME, required = true) String jwtToken) {

        boolean success = userControllerService.changeDisplayName(displayName,jwtToken);

        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 400, message = "Password has not been changed.", response = MalformedException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/resetPassword", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Boolean> resetPassword(@RequestBody ResetPasswordData resetPasswordData) {
        boolean success = userControllerService.resetPassword(resetPasswordData);
        return new ResponseEntity<>(success, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/appointmentRequests/creations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AppointmentRequestDTO>> getAppointmentRequestsCreatedByUser(
            @CookieValue(value = JWT_COOKIE_NAME, required = true) String jwtToken) {

        List<AppointmentRequestDTO> appointmentRequestsDTO = appointmentRequestControllerService.getAppointmentRequestsCreatedByUser(jwtToken);
        return new ResponseEntity<>(appointmentRequestsDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = List.class),
            @ApiResponse(code = 403, message = "No access.", response = ForbiddenException.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/users/appointmentRequests/participations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AppointmentRequestDTO>> getAppointmentRequestsWhereUserParticipates(
            @CookieValue(value = JWT_COOKIE_NAME, required = true) String jwtToken) {

        List<AppointmentRequestDTO> appointmentRequestsDTO = appointmentRequestControllerService.getAppointmentRequestsWhereUserParticipates(jwtToken);
        return new ResponseEntity<>(appointmentRequestsDTO, HttpStatus.OK);
    }

    @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = Boolean.class),
            @ApiResponse(code = 500, message = "Internal Server Error.", response = ServiceNotAvailableException.class)})
    @RequestMapping(value = "/contacts", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Boolean> contact(@RequestBody ContactData contactData) {

        generalControllerService.sendMailToContact(contactData);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

}
